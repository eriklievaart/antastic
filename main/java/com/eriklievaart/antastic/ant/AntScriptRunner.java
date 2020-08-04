package com.eriklievaart.antastic.ant;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JOptionPane;

import com.eriklievaart.antastic.config.AntConfig;
import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.antastic.model.WorkspaceProjectManager;
import com.eriklievaart.toolkit.io.api.CheckFile;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.LineFilter;
import com.eriklievaart.toolkit.io.api.SystemProperties;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.google.inject.Inject;

public class AntScriptRunner {
	private LogTemplate log = new LogTemplate(getClass());

	@Inject
	private WorkspaceProjectManager projects;
	@Inject
	private AntasticConfig config;
	@Inject
	private AntConfig ant;

	public void run(File file) throws Exception {
		run(Arrays.asList(file));
	}

	public void run(List<File> files) throws Exception {
		List<AntJob> jobs = NewCollection.list();
		for (File file : files) {
			CheckFile.isFile(file);
			jobs.addAll(parse(FileTool.toString(file)));
		}
		runSequentialJobs(jobs);
	}

	public void run(String text) throws Exception {
		runSequentialJobs(parse(text));
	}

	List<AntJob> parse(String raw) {
		Check.notNull(raw);

		List<String> lines = new LineFilter(raw).dropBlank().dropHash().eof().list();
		List<AntJob> result = NewCollection.list();
		Map<String, String> properties = NewCollection.map();

		for (String line : lines) {

			if (line.contains("=")) {
				String[] keyValue = line.trim().split("\\s*+=\\s*+");
				Check.isTrue(keyValue.length > 1, "Missing value for property %", keyValue[0]);
				properties.put(keyValue[0], keyValue[1]);

			} else {
				AntJob job = parseJob(line);
				job.putAll(properties);
				result.add(job);
			}
		}
		return result;
	}

	public AntJob parseJob(String line) {
		String[] split = line.trim().split("\\s++");
		Check.isTrue(split.length == 2, "Expected [project] [target] got $", line);
		Optional<WorkspaceProject> project = projects.getProjectByName(split[0]);
		BuildFile build = config.getBuildFile();

		Check.isTrue(project.isPresent(), "Project % not configured!", split[0]);
		return new AntJob(project.get(), build, split[1]);
	}

	private void runSequentialJobs(List<AntJob> jobs) throws Exception {
		AntScheduler.schedule(() -> {
			AntScheduler.DIRTY.set(false);

			for (AntJob job : jobs) {
				runJob(job);
				if (isDirty()) {
					break; // stop execution
				}
			}
		});
	}

	private void runJob(AntJob job) {
		printBanner(job);
		File project = job.getProject().getRoot();
		AntProcessBuilder builder = new AntProcessBuilder(ant, job.getBuildFile(), project);
		builder.putAll(job.getProperties());
		Process process = builder.runTarget(job.getTarget());

		if (process.exitValue() != 0) {
			AntScheduler.DIRTY.set(true);
			String message = Str.sub("$ $ failed!", job.getProject().getName(), job.getTarget());
			log.info(message);
			if (!SystemProperties.isSet("antastic.headless", "true")) {
				JOptionPane.showMessageDialog(null, message);
			}
			return;
		}
	}

	private void printBanner(AntJob job) {
		String info = Str.sub("## $ ##", job);
		log.info(Str.repeat("#", info.length()));
		log.info(info);
		log.info(Str.repeat("#", info.length()));
	}

	public boolean isDirty() {
		return AntScheduler.DIRTY.get();
	}
}
