package com.eriklievaart.antastic.boot;

import java.io.File;
import java.util.List;

import com.eriklievaart.antastic.ant.AntScheduler;
import com.eriklievaart.antastic.ant.AntScriptRunner;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.antastic.config.ScriptTemplates;
import com.eriklievaart.antastic.ui.main.MainController;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.WindowSaver;
import com.eriklievaart.toolkit.swing.api.laf.LookAndFeel;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			runGui();
		} else {
			System.setProperty("antastic.headless", "true");
			runScripts(args);
		}
	}

	private static void runGui() {
		SwingThread.invokeLater(new Runnable() {
			@Override
			public void run() {
				LookAndFeel.instance().load();
				WindowSaver.initialize(ApplicationPaths.getWindowSaverFile());
				Injector injector = Guice.createInjector();
				injector.getInstance(MainController.class).show();
			}
		});
	}

	private static void runScripts(String[] args) throws Exception {
		Injector injector = Guice.createInjector();
		List<File> files = getFiles(args, injector.getInstance(ScriptTemplates.class));

		injector.getInstance(AntScriptRunner.class).run(files);
		AntScheduler.awaitTermination();
		System.exit(injector.getInstance(AntScriptRunner.class).isDirty() ? 101 : 0);
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
