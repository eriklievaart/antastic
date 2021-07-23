package com.eriklievaart.antastic.boot;

import java.io.File;

import com.eriklievaart.antastic.config.ApplicationPaths;

public class TestRun {

	public static void main(String[] args) throws Exception {
		ApplicationPaths.setDataDir(new File("/home/eazy/Applications/antastic/data"));
		Main.main(new String[] { "felix-auncher" });
	}
}
