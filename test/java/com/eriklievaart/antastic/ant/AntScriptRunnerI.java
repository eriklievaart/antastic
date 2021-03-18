package com.eriklievaart.antastic.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;

import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.toolkit.io.api.CheckFile;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.ResourceTool;
import com.eriklievaart.toolkit.io.api.ini.IniNode;
import com.eriklievaart.toolkit.io.api.ini.IniNodeIO;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class AntScriptRunnerI {

	private static final int CHUNKS = 100;
	private final File spool = new File("/tmp/build/antastic/run/dummy");

	@Before
	public void init() throws FileNotFoundException {
		FileTool.clean(spool);
		System.setProperty("antastic.headless", "true");
		File config = ApplicationPaths.getAntasticConfigFile();
		if (!config.exists()) {
			config.getParentFile().mkdirs();
			IniNodeIO.write(createDummyConfig(), config);
		}
	}

	private List<IniNode> createDummyConfig() {
		IniNode build = new IniNode("buildfile");
		build.setProperty("path", ResourceTool.getFile("/test.xml").getAbsolutePath());

		IniNode project = new IniNode("project");
		project.setProperty("name", "dummy");
		project.setProperty("file", "/tmp/build/antastic/run/dummy/");
		project.setProperty("properties", "/tmp/build/antastic/run/dummy/ant.properties");

		return Arrays.asList(build, project);
	}

	@Test
	public void runScriptPass() throws Exception {
		Injector injector = Guice.createInjector();
		AntJobRunner runner = injector.getInstance(AntJobRunner.class);
		AntScript script = injector.getInstance(AntScript.class);

		File file = getTestFile();
		CheckFile.notExists(file);

		runner.run(script.queueFile(getScriptThatCreatesFile()));
		checkEventually(() -> file.exists());
	}

	@Test
	public void runScriptFail() throws Exception {
		Injector injector = Guice.createInjector();
		AntJobRunner runner = injector.getInstance(AntJobRunner.class);
		AntScript script = injector.getInstance(AntScript.class);

		runner.run(script.queueFile(getScriptThatFails()));
		checkEventually(() -> runner.isDirty());
	}

	@Test
	public void runScriptFailStopExecution() throws Exception {
		Injector injector = Guice.createInjector();
		AntJobRunner runner = injector.getInstance(AntJobRunner.class);
		AntScript script = injector.getInstance(AntScript.class);

		File fileCreatedBySecondTask = getTestFile();
		CheckFile.notExists(fileCreatedBySecondTask);

		runner.run(script.queueFile(getScriptThatFails()).queueFile(getScriptThatCreatesFile()));

		checkEventually(() -> runner.isDirty());
		waitUntil(() -> fileCreatedBySecondTask.exists());
		CheckFile.notExists(fileCreatedBySecondTask);
	}

	private void checkEventually(Supplier<Boolean> supplier) throws InterruptedException {
		waitUntil(supplier);
		Check.isTrue(supplier.get());
	}

	private void waitUntil(Supplier<Boolean> supplier) throws InterruptedException {
		for (int i = 0; i < CHUNKS; i++) {
			if (supplier.get()) {
				break;
			}
			Thread.sleep(1000 / CHUNKS);
		}
	}

	private File getScriptThatFails() {
		return ResourceTool.getFile("/fail.antastic");
	}

	private File getScriptThatCreatesFile() {
		return ResourceTool.getFile("/createfile.antastic");
	}

	private File getTestFile() {
		return new File(spool, "deleteme");
	}
}
