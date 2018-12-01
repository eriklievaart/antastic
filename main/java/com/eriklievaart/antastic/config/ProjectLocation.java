package com.eriklievaart.antastic.config;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import com.eriklievaart.toolkit.io.api.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

public class ProjectLocation {
	private LogTemplate log = new LogTemplate(getClass());

	private String name;
	private File root;
	private File properties;

	public ProjectLocation(String name, File file) {
		Check.notBlank(name);
		Check.notNull(file);
		this.name = name.trim();
		this.root = file;
	}

	public String getName() {
		return name;
	}

	public File getRoot() {
		return root;
	}

	public boolean hasProperty(String property) {
		return getPropertyNames().contains(property);
	}

	public Collection<String> getPropertyNames() {
		if (properties != null && properties.isFile()) {
			return PropertiesIO.loadStrings(properties).keySet();
		} else {
			log.warn("Property file $ for project % does not exist!", properties, name);
			return Collections.emptyList();
		}
	}

	public void setPropertyFile(File file) {
		properties = file;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProjectLocation) {
			File otherFile = ((ProjectLocation) obj).getRoot();
			return root.getAbsolutePath().equalsIgnoreCase(otherFile.getAbsolutePath());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return root.getAbsolutePath().hashCode();
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$[$]", root);
	}
}
