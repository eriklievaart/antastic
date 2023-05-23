package com.eriklievaart.antastic.boot;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.eriklievaart.antastic.ant.AntJob;
import com.eriklievaart.antastic.ant.AntJobBuilder;
import com.eriklievaart.antastic.ant.AntScript;
import com.google.inject.Inject;

public class JobParser {

	private Map<String, String> globals = new Hashtable<>();

	@Inject
	private AntScript script;
	@Inject
	private AntJobBuilder builder;

	public List<AntJob> createAntJobs(String[] args) {

		for (String arg : args) {
			if (arg.contains("/") || arg.contains("\\")) {
				builder.addAll(script.parse(new File(arg)));

			} else {
				CliParser parser = new CliParser(arg);
				parser.ifIsProperty((key, value) -> globals.put(key, value));
				parser.ifIsJob(job -> createJob(job));
			}
		}
		return builder.getJobs();
	}

	private void createJob(CliJob cli) {
		if (cli.getTargets().isEmpty()) {
			createJobFromDefaultArgs(cli.getProject());
		} else {
			builder.createAntJob(cli, globals);
		}
	}

	private void createJobFromDefaultArgs(String project) {
		Map<String, String> properties = new Hashtable<>(globals);

		for (String arg : builder.getPreconfiguredArgs(project)) {

			if (arg.contains("=") && !arg.contains(":")) {
				CliParser.parseProperty(arg).addToMap(properties);

			} else {
				CliParser parser = new CliParser(project + ":" + arg);
				parser.ifIsProperty((key, value) -> properties.put(key, value));
				parser.ifIsJob(job -> builder.createAntJob(job, properties));
			}
		}
	}
}
