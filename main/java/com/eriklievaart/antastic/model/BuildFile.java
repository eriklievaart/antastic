package com.eriklievaart.antastic.model;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.eriklievaart.antastic.ant.AntProject;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class BuildFile implements Comparable<BuildFile> {
	private static final String IF_PROPERTY = "if";
	private static final String TARGET_PROPERTY = "target";

	private LogTemplate log = new LogTemplate(getClass());

	private final File location;
	private List<IniNode> filters;

	public BuildFile(File location, List<IniNode> filters) {
		Check.noneNull(location, filters);
		this.filters = filters;
		if (!location.isFile()) {
			log.warn("not a file: " + location.getAbsolutePath());
		}
		this.location = location;
	}

	public File getFile() {
		return location;
	}

	public String getName() {
		return location.getName();
	}

	public AntProject createAntProject() {
		return new AntProject(location);
	}

	public List<String> getAvailableTargets() {
		return new AntProject(location).getAvailableTargets();
	}

	public String[] getAvailableTargets(WorkspaceProject project) {
		List<String> targets = getAvailableTargets();
		Set<String> result = NewCollection.set();

		if (project == null) {
			return new String[] {};
		}
		for (IniNode node : filters) {
			if (hasAllRequiredProperties(project, node)) {
				for (String target : node.getProperty(TARGET_PROPERTY).split("\\s*+,\\s*+")) {
					if (targets.contains(target)) {
						result.add(target);
					} else {
						log.warn("Cannot add non existing target %", target);
					}
				}
			}
		}
		log.trace("% targets: $", project.getName(), result);
		return ListTool.sortedCopy(result).toArray(new String[] {});
	}

	private boolean hasAllRequiredProperties(WorkspaceProject project, IniNode node) {
		if (!node.hasProperty(IF_PROPERTY)) {
			return true;
		}
		Collection<String> assignedProperties = project.getPropertyNames();
		log.trace("$ if $ in $", node.getProperty(TARGET_PROPERTY), node.getProperty(IF_PROPERTY), assignedProperties);

		for (String check : node.getProperty(IF_PROPERTY).split("\\s*+&\\s*+")) {
			if (!assignedProperties.contains(check)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return location.getName();
	}

	@Override
	public int compareTo(BuildFile o) {
		return location.getName().compareTo(o.location.getName());
	}
}
