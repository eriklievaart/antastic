package com.eriklievaart.antastic.boot;

import java.io.File;

import com.eriklievaart.antastic.ant.AntJobBuilder;
import com.eriklievaart.antastic.ant.AntJobRunner;
import com.eriklievaart.antastic.ant.AntScheduler;
import com.eriklievaart.antastic.ant.AntScript;
import com.eriklievaart.antastic.boot.cli.JobMetadata;
import com.eriklievaart.antastic.boot.cli.JobMetadataI;
import com.eriklievaart.antastic.boot.cli.JobParser;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.antastic.ui.main.MainController;
import com.eriklievaart.toolkit.logging.api.LogConfigFile;
import com.eriklievaart.toolkit.swing.api.SwingThread;
import com.eriklievaart.toolkit.swing.api.WindowSaver;
import com.eriklievaart.toolkit.swing.api.laf.LookAndFeel;
import com.google.inject.AbstractModule;
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
		Injector injector = createInjector();
		JobParser parser = injector.getInstance(JobParser.class);
		AntJobRunner runner = injector.getInstance(AntJobRunner.class);
		AntJobBuilder builder = injector.getInstance(AntJobBuilder.class);
		AntScript script = injector.getInstance(AntScript.class);

		for (String arg : args) {
			if (arg.contains("/") || arg.contains("\\")) {
				builder.addAll(script.parse(new File(arg)));
			} else {
				parser.createAntJobs(arg);
			}
		}
		runner.run(builder.getJobs());
		AntScheduler.awaitTermination();
		System.exit(runner.isDirty() ? 101 : 0);
	}

	private static Injector createInjector() {
		return Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(JobMetadataI.class).to(JobMetadata.class);
			}
		});
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
