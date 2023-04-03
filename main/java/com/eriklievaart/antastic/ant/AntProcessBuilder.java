package com.eriklievaart.antastic.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.eriklievaart.antastic.config.AntConfig;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

/**
 * Running Ant from java can cause all sorts of nasty classloading issues. Instead, fork a clean java process with
 * nothing but ant on the build path.
 *
 * @author Erik Lievaart
 */
public class AntProcessBuilder {
	private LogTemplate log = new LogTemplate(getClass());

	private BuildFile buildFile;
	private File projectRootDir;
	private File executable;

	private Map<String, String> properties = NewCollection.map();

	public AntProcessBuilder(AntConfig ant, BuildFile buildFile, File projectRootDir) {
		Check.noneNull(buildFile, projectRootDir);
		this.executable = ant.getExecutable();
		this.buildFile = buildFile;
		this.projectRootDir = projectRootDir;

		log.info("ant executable: $", executable);
	}

	public AntProcessBuilder antHome() {
		return this;
	}

	public boolean isAntAvailable() {
		return executable.exists();
	}

	public Process runTarget(String target) {
		try {
			Check.isTrue(isAntAvailable(), "Ant home dir not set!");
			log.info("runnning target: $ of build file $ for project $ ", target, buildFile.getFile(), projectRootDir);
            if(!projectRootDir.exists()) {
                projectRootDir.mkdirs();
            }

			ProcessBuilder builder = new ProcessBuilder(createCommandLineArguments(target));
			builder.directory(new File(projectRootDir.getAbsolutePath()));
			Process process = builder.start();

			copyStreamInThread(process.getInputStream(), System.out);
			copyStreamInThread(process.getErrorStream(), System.err);
			process.waitFor();
			return process;

		} catch (Exception e) {
			throw new RuntimeIOException(e);
		}
	}

	public void putAll(Map<String, String> values) {
		this.properties.putAll(values);
	}

	private String[] createCommandLineArguments(String target) {
		AntCommandBuilder builder = new AntCommandBuilder();

		addExecutable(builder);
		builder.addArguments("-buildfile", buildFile.getFile().getAbsolutePath(), target);

		builder.addProperty("ant.project.name", projectRootDir.getName());
		builder.addProperty("project.name", projectRootDir.getName());
		properties.forEach(builder::addProperty);

		if (ApplicationPaths.getGlobalsFile().isFile()) {
			builder.addPropertyFile(ApplicationPaths.getGlobalsFile());
		}

		log.debug("native command: $", builder);
		return builder.toArray();
	}

	private void addExecutable(AntCommandBuilder builder) {
		builder.addArguments(executable.getAbsolutePath());
	}

	private void copyStreamInThread(InputStream in, PrintStream out) throws IOException {
		new Thread() {
			@Override
			public void run() {
				try {
					IOUtils.copy(in, out);

				} catch (IOException e) {
					log.warn("Unable to copy output from process!", e);
				}
			}
		}.start();
	}
}
