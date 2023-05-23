package com.eriklievaart.antastic.boot;

import java.io.File;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.eriklievaart.antastic.ant.AntJob;
import com.eriklievaart.antastic.ant.AntJobBuilder;
import com.eriklievaart.antastic.ant.AntJobRunner;
import com.eriklievaart.antastic.ant.AntScheduler;
import com.eriklievaart.antastic.ant.AntScript;
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
		injector.getInstance(AntJobRunner.class).run(createAntJobs(injector, args));
		AntScheduler.awaitTermination();
		System.exit(injector.getInstance(AntJobRunner.class).isDirty() ? 101 : 0);
	}

	private static List<AntJob> createAntJobs(Injector injector, String[] args) {
		AntScript script = injector.getInstance(AntScript.class);
		AntJobBuilder builder = injector.getInstance(AntJobBuilder.class);
		Map<String, String> globals = new Hashtable<>();

		for (String arg : args) {
			if (arg.contains("/") || arg.contains("\\")) {
				builder.addAll(script.parse(new File(arg)));

			} else {
				CliParser parser = new CliParser(arg);
				parser.ifIsProperty((key, value) -> globals.put(key, value));
				parser.ifIsJob(job -> queueJob(Collections.unmodifiableMap(globals), builder, job));
			}
		}
		return builder.getJobs();
	}

	private static void queueJob(Map<String, String> globals, AntJobBuilder builder, CliJob cli) {
		if (cli.getTargets().isEmpty()) {
			queueDefaultArgs(globals, builder, cli.getProject());
		} else {
			builder.queueJob(cli, globals);
		}
	}

	private static void queueDefaultArgs(Map<String, String> globals, AntJobBuilder builder, String project) {
		Map<String, String> properties = new Hashtable<>(globals);

		for (String arg : builder.getPreconfiguredArgs(project)) {
			if (arg.contains("=") && !arg.contains(":")) {
				CliParser.parseProperty(arg).addToMap(properties);
			} else {
				CliParser parser = new CliParser(project + ":" + arg);
				parser.ifIsProperty((key, value) -> properties.put(key, value));
				parser.ifIsJob(job -> builder.queueJob(job, properties));
			}
		}
	}
}
