package com.eriklievaart.antastic.boot.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

import com.eriklievaart.antastic.ant.AntJob;
import com.eriklievaart.antastic.ant.AntJobBuilder;
import com.eriklievaart.antastic.config.ProjectLocation;
import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.properties.PropertiesIO;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.collection.CollectionTool;

public class JobParserFixture {

	public MockJobMetadata metadata = new MockJobMetadata();
	public AntJobBuilder builder = new AntJobBuilder(metadata);
	public JobParser parser = new JobParser(builder);
	public CliExpectations expectations = new CliExpectations(builder);

	public AntJob getUniqueJob() {
		return CollectionTool.getSingle(builder.getJobs());
	}

	public void createAntJobs(String... args) {
		Check.isTrue(args.length > 0);
		for (String arg : args) {
			parser.createAntJobs(arg);
		}
	}

	public static class MockJobMetadata implements JobMetadataI {
		private File mock = new File("/tmp/mock");
		private File build = new File("/tmp/mock/build.xml");

		@Override
		public WorkspaceProject getProject(String name) {
			ProjectLocation location = new ProjectLocation(name, getMockDir(name));
			location.setPropertyFile(getMockFile(name));
			return new WorkspaceProject(location);
		}

		@Override
		public BuildFile getBuildFile() {
			FileTool.createNewFile(build);
			return new BuildFile(build, new ArrayList<>());
		}

		public void setConfiguredArgs(String project, String value) {
			antProperties(project, map -> map.put("target", value));
		}

		public void setAnnotatedArgs(String project, String value) {
			antProperties(project, map -> map.put("annotated", value));
		}

		private void antProperties(String project, Consumer<Map<String, String>> consumer) {
			Map<String, String> properties = getMockProperties(project);
			consumer.accept(properties);
			PropertiesIO.storeStrings(properties, getMockFile(project));

		}

		private File getMockDir(String project) {
			return new File(mock, project);
		}

		private File getMockFile(String project) {
			return new File(getMockDir(project), "ant.properties");
		}

		private Map<String, String> getMockProperties(String project) {
			File file = getMockFile(project);
			return file.exists() ? PropertiesIO.loadStrings(file) : new Hashtable<>();
		}
	}
}
