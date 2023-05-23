package com.eriklievaart.antastic.boot.cli;

import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;

public interface JobMetadataI {

	BuildFile getBuildFile();

	WorkspaceProject getProject(String project);
}
