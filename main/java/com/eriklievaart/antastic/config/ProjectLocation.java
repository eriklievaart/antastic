package com.eriklievaart.antastic.config;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class ProjectLocation {

	private String name;
	private File file;
	private Map<String, String> properties = new Hashtable<>();

	public ProjectLocation(String name, File file) {
		Check.notBlank(name);
		Check.notNull(file);
		this.name = name.trim();
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public File getFile() {
		return file;
	}

	public boolean hasProperty(String property) {
		return properties.containsKey(property);
	}

	public Collection<String> getPropertyNames() {
		return properties.keySet();
	}

	public void setProperties(Map<String, String> value) {
		properties.clear();
		properties.putAll(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProjectLocation) {
			File otherFile = ((ProjectLocation) obj).getFile();
			return file.getAbsolutePath().equalsIgnoreCase(otherFile.getAbsolutePath());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return file.getAbsolutePath().hashCode();
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", file);
	}
}
