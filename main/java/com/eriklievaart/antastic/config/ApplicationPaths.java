package com.eriklievaart.antastic.config;

import java.io.File;

import com.eriklievaart.toolkit.io.api.JvmPaths;

public class ApplicationPaths {

	public static File getRunDir() {
		return new File(JvmPaths.getJarDirOrRunDir(ApplicationPaths.class));
	}

	public static File getDataDir() {
		return new File(getRunDir(), "data");
	}

	public static File getGlobalsFile() {
		return new File(getDataDir(), "globals.properties");
	}

	public static File getLastSelectionFile() {
		return new File(getDataDir(), "last.txt");
	}

	public static File getAntasticConfigFile() {
		return new File(getDataDir(), "antastic.ini");
	}

	public static File getWindowSaverFile() {
		return new File(getDataDir(), "windowsaver.ini");
	}
}
