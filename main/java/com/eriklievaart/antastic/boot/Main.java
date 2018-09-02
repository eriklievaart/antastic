package com.eriklievaart.antastic.boot;

import java.io.File;
import java.util.List;

import com.eriklievaart.antastic.ant.AntScriptRunner;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.antastic.ui.main.MainController;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.WindowSaver;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			runScripts(args);
		} else {
			runGui();
		}
	}

	private static void runScripts(String[] args) throws Exception {
		List<File> files = NewCollection.list();
		for (String path : args) {
			files.add(new File(path));
		}
		Guice.createInjector().getInstance(AntScriptRunner.class).run(files);
	}

	private static void runGui() {
		SwingThread.invokeLater(new Runnable() {
			@Override
			public void run() {
				WindowSaver.initialize(ApplicationPaths.getWindowSaverFile());
				Injector injector = Guice.createInjector();
				injector.getInstance(MainController.class).show();
			}
		});
	}
}
