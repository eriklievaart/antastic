package com.eriklievaart.antastic.ant;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.eriklievaart.antastic.boot.cli.CliJob;
import com.eriklievaart.antastic.boot.cli.JobMetadataI;
import com.eriklievaart.toolkit.lang.api.ToString;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AntJobBuilder {

	private JobMetadataI metadata;
	private List<AntJob> jobs = NewCollection.list();

	@Inject
	public AntJobBuilder(JobMetadataI metadata) {
		this.metadata = metadata;
	}

	public void addAll(List<AntJob> queue) {
		jobs.addAll(queue);
	}

	public void createAntJob(CliJob cli, Map<String, String> properties) {
		Check.noneNull(cli.getProject(), cli.getTargets(), properties);
		CheckCollection.notEmpty(cli.getTargets(), "no targets for job $", cli);

		for (String target : cli.getTargets()) {
			AntJob job = new AntJob(metadata.getProject(cli.getProject()), metadata.getBuildFile(), target);
			job.putAll(properties);
			job.putAll(cli.getProperties());
			jobs.add(job);
		}
	}

	public List<String> getPreconfiguredArgs(String project) {
		return metadata.getProject(project).getDefaultTargets();
	}

	public List<String> getAnnotatedArgs(String project) {
		return metadata.getProject(project).getAnnotatedTargets();
	}

	public List<AntJob> getJobs() {
		return Collections.unmodifiableList(jobs);
	}

	@Override
	public String toString() {
		return ToString.simple(this, "$$", jobs);
	}
}
