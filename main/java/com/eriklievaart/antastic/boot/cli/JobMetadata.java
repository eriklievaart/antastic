package com.eriklievaart.antastic.boot.cli;

import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.antastic.model.WorkspaceProjectManager;
import com.google.inject.Inject;

public class JobMetadata implements JobMetadataI {

	private AntasticConfig config;
	private WorkspaceProjectManager workspace;

	@Inject
	public JobMetadata(AntasticConfig config, WorkspaceProjectManager workspace) {
		this.config = config;
		this.workspace = workspace;
	}

	@Override
	public WorkspaceProject getProject(String name) {
		return workspace.getProjectByName(name);
	}

	@Override
	public BuildFile getBuildFile() {
		return config.getBuildFile();
	}
}
