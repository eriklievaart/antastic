package com.eriklievaart.antastic.ant;

import java.io.File;
import java.util.List;

import com.eriklievaart.toolkit.lang.api.collection.ListTool;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class AntCommandBuilder {

	private List<String> list = NewCollection.list();
	private List<String> properties = NewCollection.list();
	private List<File> propertyFiles = NewCollection.list();

	public void addArguments(String... strings) {
		ListTool.addAll(list, strings);
	}

	public void addProperty(String key, String value) {
		properties.add(Str.sub("-D$=$", key, value));
	}

	public void addPropertyFile(File file) {
		propertyFiles.add(file);
	}

	public String[] toArray() {
		List<String> array = NewCollection.list();

		array.addAll(list);
		array.addAll(properties);

		propertyFiles.forEach(file -> {
			array.add("-propertyfile");
			array.add(file.getAbsolutePath());
		});

		return array.toArray(new String[] {});
	}

	@Override
	public String toString() {
		return String.join(" ", toArray());
	}
}
