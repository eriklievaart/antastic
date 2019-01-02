package com.eriklievaart.antastic.ant;

import java.util.Collections;
import java.util.Map;

import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class AntJob {

	private WorkspaceProject project;
	private BuildFile buildFile;
	private String target;
	private Map<String, String> properties = NewCollection.map();

	public AntJob(WorkspaceProject project, BuildFile buildFile, String target) {
		this.project = project;
		this.buildFile = buildFile;
		this.target = target;
	}

	public WorkspaceProject getProject() {
		return project;
	}

	public BuildFile getBuildFile() {
		return buildFile;
	}

	public String getTarget() {
		return target;
	}

	public void putAll(Map<String, String> values) {
		values.keySet().forEach(key -> {
			KeyValidator.check(key);
		});
		properties.putAll(values);
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$ $ $]", project.getName(), buildFile.getName(), target);
	}
}
