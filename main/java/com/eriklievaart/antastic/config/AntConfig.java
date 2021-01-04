package com.eriklievaart.antastic.config;

import java.io.File;

import com.eriklievaart.toolkit.io.api.FileTool;
import com.google.inject.Singleton;

@Singleton
public class AntConfig {

	private File executable = new File("/usr/bin/ant");

	{
		File file = ApplicationPaths.getAntConfigFile();
		if(file.isFile()) {
			setExecutable(new File(FileTool.toString(file)));
		}
	}

	public File getExecutable() {
		return executable;
	}

	public void setExecutable(File file) {
		this.executable = file;
		FileTool.writeStringToFile(file.getAbsolutePath(), ApplicationPaths.getAntConfigFile());
	}
}
