package com.eriklievaart.antastic.model;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;

public class GlobalsIO {

	public static String readGlobals() {
		File file = ApplicationPaths.getGlobalsFile();
		try {
			if (file.isFile()) {
				return FileUtils.readFileToString(file);
			}
		} catch (IOException e) {
			throw new RuntimeIOException("Unable to read globals in $", e, file);
		}
		return "";
	}

	public static void storeGlobals(String data) {
		File file = ApplicationPaths.getGlobalsFile();
		try {
			FileUtils.writeStringToFile(file, data);
		} catch (IOException e) {
			throw new RuntimeIOException("Unable to save globals in $", e, file);
		}
	}
}
