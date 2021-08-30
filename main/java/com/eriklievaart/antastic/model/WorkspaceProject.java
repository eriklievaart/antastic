package com.eriklievaart.antastic.model;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.eriklievaart.antastic.ant.KeyValidator;
import com.eriklievaart.antastic.config.ProjectLocation;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;

public class WorkspaceProject implements Comparable<WorkspaceProject> {
	private ProjectLocation location;

	public WorkspaceProject(ProjectLocation root) {
		KeyValidator.check(root.getName());
		this.location = root;
	}

	public String getName() {
		return location.getName();
	}

	public File getRoot() {
		return location.getRoot();
	}

	public ProjectLocation getFileLocation() {
		return location;
	}

	public Collection<String> getPropertyNames() {
		return location.getPropertyNames();
	}

	public String getProperty(String key) {
		Optional<String> optional = location.getProperty(key);
		return optional.orElseThrow(() -> new RuntimeIOException("property % not set for project %", key, getName()));
	}

	public List<String> getDefaultTargets() {
		return Arrays.asList(getProperty("target").trim().split("[, ]++"));
	}

	@Override
	public String toString() {
		return location.getName();
	}

	@Override
	public int compareTo(WorkspaceProject o) {
		return location.getName().compareTo(o.location.getName());
	}
}
