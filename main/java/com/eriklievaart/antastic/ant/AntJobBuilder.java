package com.eriklievaart.antastic.ant;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.eriklievaart.antastic.boot.CliJob;
import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.antastic.model.WorkspaceProjectManager;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.google.inject.Inject;

public class AntJobBuilder {

	private final AntasticConfig config;
	private final WorkspaceProjectManager workspace;
	private List<AntJob> jobs = NewCollection.list();

	@Inject
	public AntJobBuilder(AntasticConfig config, WorkspaceProjectManager workspace) {
		this.config = config;
		this.workspace = workspace;
	}

	public void addAll(List<AntJob> queue) {
		jobs.addAll(queue);
	}

	public void queueJob(CliJob cli, Map<String, String> properties) {
		Check.noneNull(cli.getProject(), cli.getTargets(), properties);
		CheckCollection.notEmpty(cli.getTargets(), "no targets for job $", cli);
		WorkspaceProject project = workspace.getProjectByName(cli.getProject());

		for (String target : cli.getTargets()) {
			AntJob job = new AntJob(project, config.getBuildFile(), target);
			job.putAll(properties);
			job.putAll(cli.getProperties());
			jobs.add(job);
		}
	}

	public List<String> getPreconfiguredArgs(String project) {
		return workspace.getProjectByName(project).getDefaultTargets();
	}

	public List<AntJob> getJobs() {
		return Collections.unmodifiableList(jobs);
	}
}
