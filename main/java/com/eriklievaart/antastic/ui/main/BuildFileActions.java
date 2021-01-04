package com.eriklievaart.antastic.ui.main;

import java.awt.Desktop;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.eriklievaart.antastic.ant.AntProcessBuilder;
import com.eriklievaart.antastic.ant.AntScheduler;
import com.eriklievaart.antastic.config.AntConfig;
import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.toolkit.lang.api.FormattedException;
import com.eriklievaart.toolkit.swing.api.menu.Command;
import com.eriklievaart.toolkit.swing.api.menu.Menu;
import com.google.inject.Inject;

@Menu(text = "build file")
public class BuildFileActions {

	@Inject
	private MainComponents components;
	@Inject
	private AntasticConfig config;
	@Inject
	private AntConfig ant;

	@Command(name = "run selected target")
	public void runSelectedTarget() throws IOException {
		WorkspaceProject project = components.getProjectList().getSelectedValue();
		BuildFile selected = config.getBuildFile();
		String target = components.getTargetList().getSelectedValue();
		boolean selectionIncomplete = project == null || selected == null || target == null;
		FormattedException.on(selectionIncomplete, "Select a project, a build file and a target first!");

		runTarget(project, selected, target);
	}

	private void runTarget(WorkspaceProject project, BuildFile selected, String target) throws IOException {
		AntProcessBuilder builder = new AntProcessBuilder(ant, selected, project.getRoot());
		if (!builder.isAntAvailable()) {
			throw new FormattedException("Ant not available!");
		}
		if (!builder.isAntAvailable()) {
			return;
		}
		AntScheduler.schedule(() -> {
			builder.runTarget(target);
		});
	}

	@Command(name = "edit")
	public void edit() {
		BuildFile selected = config.getBuildFile();
		FormattedException.on(selected == null, "Select a build file first!");
		try {
			Desktop.getDesktop().edit(selected.getFile());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot open file; " + e.getMessage());
			return;
		}
	}
}
