package com.eriklievaart.antastic.boot.cli;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.eriklievaart.antastic.ant.AntJobBuilder;
import com.google.inject.Inject;

public class JobParser {

	private Map<String, String> globals = new Hashtable<>();

	private AntJobBuilder builder;

	@Inject
	public JobParser(AntJobBuilder builder) {
		this.builder = builder;
	}

	public void createAntJobs(String arg) {
		CliParser parser = new CliParser(arg);
		parser.ifIsProperty((key, value) -> globals.put(key, value));
		parser.ifIsJob(job -> createJob(job));
	}

	private void createJob(CliJob cli) {
		if (cli.isAnnotated()) {
			createAnnotatedJob(cli);
		} else if (cli.getTargets().isEmpty()) {
			createJobFromDefaultArgs(cli);
		} else {
			builder.createAntJob(cli, globals);
		}
	}

	private void createAnnotatedJob(CliJob cli) {
		createJobFromArgs(cli, builder.getAnnotatedArgs(cli.getProject()));
	}

	private void createJobFromDefaultArgs(CliJob cli) {
		createJobFromArgs(cli, builder.getPreconfiguredArgs(cli.getProject()));
	}

	private void createJobFromArgs(CliJob cli, List<String> args) {
		Map<String, String> properties = new Hashtable<>(globals);
		properties.putAll(cli.getProperties());

		for (String arg : args) {

			if (arg.contains("=") && !arg.contains(":")) {
				CliParser.parseProperty(arg).addToMap(properties);

			} else {
				CliParser parser = new CliParser(cli.getProject() + ":" + arg);
				parser.ifIsProperty((key, value) -> properties.put(key, value));
				parser.ifIsJob(job -> builder.createAntJob(job, properties));
			}
		}
	}
}
