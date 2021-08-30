package com.eriklievaart.antastic.boot;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import com.eriklievaart.antastic.ant.AntJobBuilder;
import com.eriklievaart.antastic.ant.AntJobRunner;
import com.eriklievaart.antastic.ant.AntScheduler;
import com.eriklievaart.antastic.ant.AntScript;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.antastic.ui.main.MainController;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
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
		injector.getInstance(AntJobRunner.class).run(createAntScript(injector, args));
		AntScheduler.awaitTermination();
		System.exit(injector.getInstance(AntJobRunner.class).isDirty() ? 101 : 0);
	}

	private static AntScript createAntScript(Injector injector, String[] args) {
		AntScript script = injector.getInstance(AntScript.class);
		Map<String, String> globals = new Hashtable<>();

		for (String arg : args) {
			File file = new File(arg);

			if (arg.contains("/") || arg.contains("\\")) {
				RuntimeIOException.unless(file.exists(), "File % does not exist!", arg);
				script.queueFile(file);

			} else {
				CliParser parser = new CliParser(arg);
				parser.ifIsGlobal((key, value) -> globals.put(key, value));
				parser.ifIsJob(job -> {
					globals.forEach(job::put);
					queue(script, job);
				});
			}
		}
		return script;
	}

	private static void queue(AntScript script, CliJob arg) {
		AntJobBuilder builder = script.buildJob(arg.getProject());
		builder.putAll(arg.getProperties());
		for (String target : arg.getTargets()) {
			builder.addTarget(target);
		}
		builder.queue();
	}
}
