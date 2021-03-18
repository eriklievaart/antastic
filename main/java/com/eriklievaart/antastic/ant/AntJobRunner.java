package com.eriklievaart.antastic.ant;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import com.eriklievaart.antastic.config.AntConfig;
import com.eriklievaart.toolkit.io.api.SystemProperties;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.google.inject.Inject;

public class AntJobRunner {
	private LogTemplate log = new LogTemplate(getClass());

	@Inject
	private AntConfig ant;

	public void run(AntScript script) throws Exception {
		run(script.getAntJobs());
	}

	public void run(List<AntJob> jobs) throws Exception {
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
