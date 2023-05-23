package com.eriklievaart.antastic.boot;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class CliJob {

	private String project;
	private List<String> targets = NewCollection.list();
	private Map<String, String> properties = new Hashtable<>();

	public CliJob(String project) {
		this.project = project;
	}

	public String getProject() {
		return project;
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public List<String> getTargets() {
		return Collections.unmodifiableList(targets);
	}

	void put(String key, String value) {
		properties.put(key, value);
	}

	public void putAll(Map<String, String> all) {
		properties.putAll(all);
	}

	void addTarget(String target) {
		targets.add(target);
	}

	@Override
	public String toString() {
		return project + targets + properties;
	}
}
