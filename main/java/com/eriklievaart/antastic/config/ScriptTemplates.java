package com.eriklievaart.antastic.config;

import java.io.File;
import java.util.Optional;

import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.antastic.model.WorkspaceProjectManager;
import com.eriklievaart.toolkit.io.api.Console;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.JvmPaths;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.str.StringBuilderWrapper;

public class ScriptTemplates {

	private WorkspaceProjectManager workspace = WorkspaceProjectManager.singleton();
	private AntasticConfig config = AntasticConfig.singleton();

	public File resolve(String project) {
		String root = JvmPaths.getJarDirOrRunDir(getClass());
		File scripts = new File(root, "data/scripts");
		File resolved = new File(scripts, project);

		if (!resolved.exists()) {
			Optional<WorkspaceProject> optional = workspace.getProjectByName(project);
			RuntimeIOException.unless(optional.isPresent(), "project % does not exist!", resolved.getAbsolutePath());
			Console.println("creating: $", resolved.getAbsolutePath());
			FileTool.writeStringToFile(createTemplate(optional.get()), resolved);
		}
		return resolved;
	}

	private String createTemplate(WorkspaceProject project) {
		StringBuilderWrapper builder = new StringBuilderWrapper();
		builder.appendLine();

		for (String target : project.getProperty("target").split("[, ]++")) {
			builder.append(project.getName()).append(" ").appendLine(target);
		}
		builder.appendLine();
		builder.appendLine("@EOF@");
		builder.appendLine();

		for (String target : config.getBuildFile().getAvailableTargets(project)) {
			builder.append(project.getName()).append(" ").appendLine(target);
		}
		builder.appendLine();
		return builder.toString();
	}
}
