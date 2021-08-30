package com.eriklievaart.antastic.ant;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.antastic.model.WorkspaceProjectManager;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.LineFilter;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.google.inject.Inject;

public class AntScript {

	private final AntasticConfig config;
	private final WorkspaceProjectManager workspace;

	private List<AntJob> jobs = NewCollection.list();

	@Inject
	public AntScript(AntasticConfig config, WorkspaceProjectManager workspace) {
		this.config = config;
		this.workspace = workspace;
	}

	public AntScript queueFile(File file) {
		jobs.addAll(parse(FileTool.toString(file)));
		return this;
	}

	public AntScript queueRaw(String raw) {
		jobs.addAll(parse(raw));
		return this;
	}

	public AntJobBuilder buildJob(String project) {
		return new AntJobBuilder(workspace.getProjectByName(project), config.getBuildFile(), jobs);
	}

	public List<AntJob> getAntJobs() {
		return Collections.unmodifiableList(jobs);
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

	AntJob parseJob(String line) {
		String[] split = line.trim().split("\\s++");
		Check.isTrue(split.length == 2, "Expected [project] [target] got $", line);
		WorkspaceProject project = workspace.getProjectByName(split[0]);
		BuildFile build = config.getBuildFile();

		return new AntJob(project, build, split[1]);
	}
}
