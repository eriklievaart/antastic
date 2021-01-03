package com.eriklievaart.antastic.boot;

import java.io.File;
import java.util.List;

import com.eriklievaart.antastic.ant.AntScheduler;
import com.eriklievaart.antastic.ant.AntScriptRunner;
import com.eriklievaart.antastic.config.ScriptTemplates;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("missing arguments");
		} else {
			runScripts(args);
		}
	}

	private static void runScripts(String[] args) throws Exception {
		List<File> files = getFiles(args, new ScriptTemplates());

		AntScriptRunner runner = new AntScriptRunner();
		runner.run(files);
		AntScheduler.awaitTermination();
		System.exit(runner.isDirty() ? 101 : 0);
	}

	private static List<File> getFiles(String[] args, ScriptTemplates templates) {
		List<File> files = NewCollection.list();
		for (String arg : args) {
			File file = new File(arg);
			if (file.exists()) {
				files.add(file);
			} else {
				RuntimeIOException.on(arg.contains("/") || arg.contains("\\"), "% does not exist!", arg);
				files.add(templates.resolve(arg));
			}
		}
		return files;
	}
}
