package com.eriklievaart.antastic.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.Group;
import com.eriklievaart.toolkit.io.api.ResourceTool;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.io.api.ini.IniNodeIO;
import com.eriklievaart.toolkit.io.api.ini.IniNodeValidator;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.CollectionTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.collection.SetTool;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.google.inject.Singleton;

@Singleton
public class AntasticConfig {

	private static final String BUILD_FILE_PROPERTY_PATH = "buildfile/path";
	private static final String BUILD_FILTER_PROPERTY_PATH = "buildfile/filter";

	private static final String NAME_PROPERTY = "name";
	private static final String FILE_PROPERTY = "file";
	private static final String ANT_CONFIG_FILE_PROPERTY = "properties";
	private static final String PROJECTS_PROPERTY = "projects";

	private static final String WORKINGSET_NODE = "group";
	private static final String PROJECT_NODE = "project";

	private final LogTemplate log = new LogTemplate(getClass());
	private IniNode configRoot = new IniNode("root");

	public AntasticConfig() throws IOException {
		try {
			init();
		} catch (Exception e) {
			log.error("Failed to start Antastic; $", e, e.getMessage());
			throw new IOException(e);
		}
	}

	private void init() throws IOException {
		File file = ApplicationPaths.getAntasticConfigFile();
		if (file.isFile()) {
			List<IniNode> configuration = IniNodeIO.read(file);
			verifyContents(configuration);
			for (IniNode child : configuration) {
				configRoot.addChild(child);
			}
		} else {
			throw new RuntimeIOException("config file $ does not exist!", file);
		}
	}

	private void verifyContents(List<IniNode> configuration) {
		InputStream is = ResourceTool.getInputStream("/config-schema.ini");
		IniNodeValidator.validate(configuration, CollectionTool.getSingle(IniNodeIO.read(is)));
	}

	public List<ProjectLocation> listProjectLocations() {
		return configRoot.getChildren(PROJECT_NODE).stream().map(this::toFileLocation).collect(Collectors.toList());
	}

	private void save() {
		File file = ApplicationPaths.getAntasticConfigFile();
		try {
			IniNodeIO.write(configRoot.getChildren(), file);
		} catch (RuntimeIOException e) {
			log.warn("Unable to save config file: $", e, file);
		}
	}

	private boolean containsProjectLocation(ProjectLocation query) {
		for (ProjectLocation location : listProjectLocations()) {
			if (location.getName().equals(query.getName())) {
				return true;
			}
		}
		return false;
	}

	public void addFileLocation(ProjectLocation file) throws IOException {
		log.info("% adding project %", file.getName());
		if (containsProjectLocation(file)) {
			throw new IOException("File already exists!");
		}
		IniNode node = new IniNode(PROJECT_NODE);
		node.setProperty(NAME_PROPERTY, file.getName());
		node.setProperty(FILE_PROPERTY, file.getRoot().getAbsolutePath());
		configRoot.addChild(node);
		save();
	}

	public void removeFileLocation(ProjectLocation remove) throws IOException {
		log.info("% removing FileLocation %", remove.getName());
		Check.isTrue(containsProjectLocation(remove), "Failed to remove $", remove);

		List<IniNode> children = configRoot.getChildren();
		configRoot = new IniNode("root");
		for (IniNode child : children) {
			if (child.getName().equals(PROJECT_NODE)) {
				if (child.hasProperty(NAME_PROPERTY) && child.getProperty(NAME_PROPERTY).equals(remove.getName())) {
					continue;
				}
			}
			configRoot.addChild(child);
		}
		save();
	}

	private ProjectLocation toFileLocation(IniNode node) {
		String project = node.getProperty(NAME_PROPERTY);
		File root = new File(node.getProperty(FILE_PROPERTY));
		ProjectLocation pl = new ProjectLocation(project, root);

		if (node.hasProperty(ANT_CONFIG_FILE_PROPERTY)) {
			pl.setPropertyFile(new File(node.getProperty(ANT_CONFIG_FILE_PROPERTY)));
		}
		return pl;
	}

	private static Group toGroup(IniNode node) {
		Group ws = new Group(node.getProperty(NAME_PROPERTY));
		if (node.hasProperty(PROJECTS_PROPERTY)) {
			ws.setProjects(SetTool.of(node.getProperty(PROJECTS_PROPERTY).split("\\s*+,\\s*+")));
		}
		return ws;
	}

	public Group[] getGroups() {
		List<Group> result = new ArrayList<>();
		for (IniNode node : configRoot.getChildren(WORKINGSET_NODE)) {
			result.add(toGroup(node));
		}
		Collections.sort(result);
		return result.toArray(new Group[] {});
	}

	public BuildFile getBuildFile() {
		File file = new File(configRoot.getProperty(BUILD_FILE_PROPERTY_PATH));

		if (configRoot.hasProperty(BUILD_FILTER_PROPERTY_PATH)) {
			File filters = new File(configRoot.getProperty(BUILD_FILTER_PROPERTY_PATH));
			return new BuildFile(file, IniNodeIO.read(filters));
		} else {
			return new BuildFile(file, NewCollection.list());
		}
	}
}
