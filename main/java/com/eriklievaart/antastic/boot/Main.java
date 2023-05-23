package com.eriklievaart.antastic.boot;

import com.eriklievaart.antastic.ant.AntJobRunner;
import com.eriklievaart.antastic.ant.AntScheduler;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.antastic.ui.main.MainController;
import com.eriklievaart.toolkit.logging.api.LogConfigFile;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.WindowSaver;
import com.eriklievaart.toolkit.swing.api.laf.LookAndFeel;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	public static void main(String[] args) throws Exception {
		LogConfigFile.initFromDirectory(ApplicationPaths.getRunDir());

		if (args.length == 0) {
			runGui();
		} else {
			System.setProperty("antastic.headless", "true");
			runScripts(args);
		}
	}

	private static void runScripts(String[] args) throws Exception {
		Injector injector = Guice.createInjector();
		JobParser parser = injector.getInstance(JobParser.class);
		AntJobRunner runner = injector.getInstance(AntJobRunner.class);

		runner.run(parser.createAntJobs(args));
		AntScheduler.awaitTermination();
		System.exit(runner.isDirty() ? 101 : 0);
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
}
