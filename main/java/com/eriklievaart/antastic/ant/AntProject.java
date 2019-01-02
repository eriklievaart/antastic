package com.eriklievaart.antastic.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.concurrent.Prototype;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;

/**
 * This is a utility class for calling Ant from Java.
 *
 * @author Erik Lievaart
 */
@Prototype
public class AntProject {
	private final LogTemplate log = new LogTemplate(getClass());

	/** Location of the build file. */
	private final File file;
	private final Map<String, String> properties;

	/**
	 * Constructor.
	 *
	 * @param buildFile
	 *            build file this AntProject should execute.
	 */
	public AntProject(final File buildFile) {
		this.file = buildFile;
		this.properties = Collections.emptyMap();
	}

	public String getDefaultTarget() {
		return createProject().getDefaultTarget();
	}

	public List<String> getAvailableTargets() {
		List<String> list = new ArrayList<String>();
		if (!file.exists()) {
			log.warn("build file $ does not exist; returning empty list of targets", file);
			return list;
		}

		try {
			for (Object key : createProject().getTargets().keySet()) {
				if (!Str.isBlank(key.toString())) {
					list.add(key.toString());
				}
			}
			return list;

		} catch (Exception e) {
			log.warn("not a valid build file: $ ; returning empty list of targets", e, file);
			return NewCollection.list();
		}
	}

	private Project createProject() {
		Project project = new Project();
		configureLogger(project);

		if (properties.containsKey("ant.project.name")) {
			project.setName(properties.get("ant.project.name"));
		}

		project.setNewProperty("ant.file", file.getAbsolutePath());
		for (Entry<String, String> entry : properties.entrySet()) {
			project.setNewProperty(entry.getKey(), entry.getValue());
		}
		project.fireBuildStarted();
		project.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		project.addReference("ant.projectHelper", helper);
		helper.parse(project, file);
		return project;
	}

	/**
	 * Configures the logger for a project.
	 *
	 * @param p
	 *            If this method is not called for p, the project runs or fails
	 *            silently.
	 */
	private void configureLogger(final Project p) {
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);
	}

	public static boolean isValidBuildFile(File file) {
		if (file == null || !file.isFile()) {
			return false;
		}
		AntProject project = new AntProject(file);
		try {
			return !project.getAvailableTargets().isEmpty();

		} catch (Exception e) {
			project.log.warn("Unable to parse build file: $; $", e, file, e.getMessage());
			return false;
		}
	}
}
