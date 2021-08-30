package com.eriklievaart.antastic.ant;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class AntJobBuilder {

	private WorkspaceProject project;
	private BuildFile file;
	private List<AntJob> jobs;

	private List<String> targets = NewCollection.list();
	private Map<String, String> properties = new Hashtable<>();

	public AntJobBuilder(WorkspaceProject project, BuildFile file, List<AntJob> jobs) {
		this.project = project;
		this.file = file;
		this.jobs = jobs;
	}

	public void putAll(Map<String, String> map) {
		this.properties.putAll(map);
	}

	public void addTarget(String target) {
		targets.add(target);
	}

	public void queue() {
		for (String target : resolveTargets()) {
			AntJob job = new AntJob(project, file, target);
			job.putAll(properties);
			jobs.add(job);
		}
		targets.clear();
	}

	private List<String> resolveTargets() {
		return targets.isEmpty() ? project.getDefaultTargets() : targets;
	}
}
