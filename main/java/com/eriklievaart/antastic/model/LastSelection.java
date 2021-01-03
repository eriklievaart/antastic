package com.eriklievaart.antastic.model;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class LastSelection {
	private static final String WORKING_SET_KEY = "workingset";
	private static final LastSelection INSTANCE = new LastSelection();

	private AntasticConfig config = AntasticConfig.singleton();
	private WorkspaceProjectManager projects = WorkspaceProjectManager.singleton();

	private Map<String, String> data = NewCollection.map();

	private LastSelection() {
		load();
	}

	public static LastSelection singleton() {
		return INSTANCE;
	}

	public void setWorkspaceSelected(Group value) {
		if (value != null) {
			put(WORKING_SET_KEY, value.getName());
		}
	}

	public void setProject(Group selectedGroup, WorkspaceProject project) {
		put(getKey(selectedGroup), project.getName());
	}

	public Optional<WorkspaceProject> getProject(Group selectedGroup) {
		String key = getKey(selectedGroup);
		if (data.containsKey(key)) {
			String lookup = data.get(key);
			for (WorkspaceProject project : projects.getProjects()) {
				if (project.getName().equals(lookup)) {
					return Optional.of(project);
				}
			}
		}
		return Optional.empty();
	}

	public Optional<Group> getGroup() {
		if (data.containsKey(WORKING_SET_KEY)) {
			String lookup = data.get(WORKING_SET_KEY);
			for (Group group : config.getGroups()) {
				if (group.getName().equals(lookup)) {
					return Optional.of(group);
				}
			}
		}
		return Optional.empty();
	}

	public Optional<String> getTarget(WorkspaceProject project) {
		if (project == null) {
			return Optional.empty();
		}
		String key = getTargetKey(project);
		if (data.containsKey(key)) {
			return Optional.of(data.get(key));
		}
		return Optional.empty();
	}

	public void setTarget(WorkspaceProject project, String target) {
		if (project != null && target != null) {
			put(getTargetKey(project), target);
		}
	}

	private String getTargetKey(WorkspaceProject project) {
		return project.getName() + ".target";
	}

	private String getKey(Group selectedGroup) {
		return "." + selectedGroup.getName();
	}

	private void put(String key, String value) {
		if (key == null || value == null) {
			return;
		}
		if (!data.containsKey(key) || !data.get(key).equals(value)) {
			data.put(key, value);
			save();
		}
	}

	private void save() {
		try {
			PropertiesIO.storeStrings(data, ApplicationPaths.getLastSelectionFile());
		} catch (RuntimeIOException e) {
			throw new FormattedException("Unable to store selection data", e);
		}
	}

	private void load() {
		File file = ApplicationPaths.getLastSelectionFile();
		if (!file.isFile()) {
			return;
		}
		data = PropertiesIO.loadStrings(file);
	}
}
