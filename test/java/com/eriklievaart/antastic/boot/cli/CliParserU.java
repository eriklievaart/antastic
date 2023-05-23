package com.eriklievaart.antastic.boot.cli;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.eriklievaart.antastic.boot.cli.CliParser;
import com.eriklievaart.toolkit.lang.api.AssertionException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;

public class CliParserU {

	@Test
	public void parseJobSimple() {
		CliParser parser = new CliParser("q");

		AtomicBoolean called = new AtomicBoolean(false);
		parser.ifIsJob((job) -> {
			called.set(true);

			Check.isEqual(job.getProject(), "q");
			CheckCollection.isEmpty(job.getTargets());
		});
		Check.isTrue(called.get());
	}

	@Test
	public void parseJobComplex() {
		CliParser parser = new CliParser("q:skip.test=true:skip.checkstyle=true:master-osgi-deploy:master-clean");

		AtomicBoolean called = new AtomicBoolean(false);
		parser.ifIsJob((job) -> {
			called.set(true);

			Check.isEqual(job.getProject(), "q");
			Check.isEqual(job.getTargets(), Arrays.asList("master-osgi-deploy", "master-clean"));
			Check.isEqual(job.getProperties().get("skip.test"), "true");
			Check.isEqual(job.getProperties().get("skip.checkstyle"), "true");
		});
		Check.isTrue(called.get());
	}

	@Test
	public void parseGlobalSuccess() {
		CliParser parser = new CliParser("skip.test=true");

		AtomicBoolean called = new AtomicBoolean(false);
		parser.ifIsProperty((key, value) -> {
			Check.isEqual(key, "skip.test");
			Check.isEqual(value, "true");
			called.set(true);
		});
		Check.isTrue(called.get());
	}

	@Test
	public void parseGlobalNotAGlobal() {
		CliParser parser = new CliParser("q:skip.test=true");

		parser.ifIsProperty((key, value) -> {
			Assert.fail();
		});
	}

	@Test(expected = AssertionException.class)
	public void parseGlobalInvalid() {
		CliParser parser = new CliParser("skip.test=true=error");
		parser.ifIsProperty((key, value) -> Assert.fail());
	}
}
