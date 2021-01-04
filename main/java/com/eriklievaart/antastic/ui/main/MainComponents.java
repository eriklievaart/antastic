package com.eriklievaart.antastic.ui.main;

import javax.swing.JFrame;
import javax.swing.JList;

import com.eriklievaart.antastic.model.Group;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.toolkit.swing.api.builder.JFrameBuilder;
import com.google.inject.Singleton;

@Singleton
public class MainComponents {

	private JFrame frame = new JFrameBuilder("main").title("Antastic").exitOnClose().create();

	private JList<Group> groupList = new JList<>();
	private JList<WorkspaceProject> projectList = new JList<>();
	private JList<String> targetList = new JList<>();

	public JFrame getFrame() {
		return frame;
	}

	public JList<Group> getGroupList() {
		return groupList;
	}

	public JList<WorkspaceProject> getProjectList() {
		return projectList;
	}

	public JList<String> getTargetList() {
		return targetList;
	}
}
