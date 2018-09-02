package com.eriklievaart.antastic.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.eriklievaart.toolkit.lang.api.check.CheckStr;

public class Group implements Comparable<Group> {

	private String name;
	private Set<String> projects = new HashSet<>();
	private boolean locked;

	public Group(String name) {
		CheckStr.isIdentifier(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Set<String> getProjects() {
		return Collections.unmodifiableSet(projects);
	}

	public void setProjects(Set<String> projects) {
		if (!locked) {
			this.projects = projects;
		}
	}

	public boolean containsProject(WorkspaceProject project) {
		return projects.contains(project.getName());
	}

	public void lockProjects() {
		locked = true;
	}

	public void unlockProjects() {
		locked = false;
	}

	public boolean isLocked() {
		return locked;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Group) {
			Group other = (Group) obj;
			return name.equals(other.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(Group o) {
		return name.compareTo(o.name);
	}
}
