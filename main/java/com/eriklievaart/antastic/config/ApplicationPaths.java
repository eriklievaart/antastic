package com.eriklievaart.antastic.config;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import com.eriklievaart.toolkit.io.api.CheckFile;
import com.eriklievaart.toolkit.io.api.JvmPaths;

public class ApplicationPaths {

	private static final AtomicReference<File> DATA = new AtomicReference<>(new File(getRunDir(), "data"));

	public static File getRunDir() {
		return new File(JvmPaths.getJarDirOrRunDir(ApplicationPaths.class));
	}

	public static void setDataDir(File file) {
		CheckFile.isDirectory(file);
		DATA.set(file);
	}

	public static File getDataDir() {
		return DATA.get();
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

	public static File getAntConfigFile() {
		return new File(JvmPaths.getJarDirOrClassDir(ApplicationPaths.class), "data/ant.txt");
	}
}
