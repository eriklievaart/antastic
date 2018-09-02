package com.eriklievaart.antastic.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.config.ProjectLocation;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class WorkspaceProjectManager {

	private final List<WorkspaceProject> projects;

	@Inject
	public WorkspaceProjectManager(AntasticConfig config) {
		List<ProjectLocation> nodes = config.listProjectLocations();
		projects = nodes.stream().map(WorkspaceProject::new).collect(Collectors.toList());
		Collections.sort(projects);
	}

	public WorkspaceProject[] toArray() {
		return projects.toArray(new WorkspaceProject[] {});
	}

	public Optional<WorkspaceProject> getProjectByName(String name) {
		for (WorkspaceProject project : projects) {
			if (project.getName().equals(name)) {
				return Optional.of(project);
			}
		}
		return Optional.empty();
	}

	public List<WorkspaceProject> getProjects() {
		return Collections.unmodifiableList(projects);
	}

}