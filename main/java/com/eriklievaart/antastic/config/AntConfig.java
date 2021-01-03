package com.eriklievaart.antastic.config;

import java.io.File;

import com.eriklievaart.toolkit.io.api.FileTool;

public class AntConfig {
	private static final AntConfig INSTANCE = new AntConfig();

	private File executable = new File("/usr/bin/ant");

	private AntConfig() {
		File file = ApplicationPaths.getAntConfigFile();
		if (file.isFile()) {
			setExecutable(new File(FileTool.toString(file)));
		}
	}

	public static AntConfig singleton() {
		return INSTANCE;
	}

	public File getExecutable() {
		return executable;
	}

	public void setExecutable(File file) {
		this.executable = file;
		FileTool.writeStringToFile(file.getAbsolutePath(), ApplicationPaths.getAntConfigFile());
	}
}
