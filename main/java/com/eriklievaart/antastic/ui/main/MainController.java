package com.eriklievaart.antastic.ui.main;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.eriklievaart.antastic.model.Group;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.antastic.ui.util.LineBorderFactory;
import com.eriklievaart.antastic.ui.util.MouseDoubleClickListener;
import com.eriklievaart.toolkit.swing.api.builder.GridPanelBuilder;
import com.eriklievaart.toolkit.swing.api.menu.ReflectionMenuBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MainController {

	private MainComponents components;

	@Inject
	private MainRefresh refresh;

	@Inject
	public MainController(MainComponents components, AntActions antActions, final BuildFileActions buildActions) {
		this.components = components;

		JFrame frame = components.getFrame();
		frame.getContentPane().add(createMainPanel(buildActions));

		ReflectionMenuBuilder menuBuilder = new ReflectionMenuBuilder();
		frame.setJMenuBar(menuBuilder.createMenuBar(antActions, buildActions));
	}

	private JPanel createMainPanel(final BuildFileActions antActions) {
		GridPanelBuilder gridPanel = new GridPanelBuilder(1, 0);

		addGroupList(gridPanel);
		addProjectList(gridPanel);
		addTargetList(antActions, gridPanel);

		return gridPanel.create();
	}

	private void addGroupList(GridPanelBuilder gridPanel) {
		JList<Group> targetList = components.getGroupList();
		LineBorderFactory.setBorder(targetList, "working sets");
		gridPanel.add(targetList);
	}

	private void addTargetList(final BuildFileActions antActions, GridPanelBuilder gridPanel) {
		JList<String> targetList = components.getTargetList();
		targetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		LineBorderFactory.setBorder(targetList, "targets");
		targetList.addMouseListener(new MouseDoubleClickListener(e -> {
			if (targetList.getModel().getSize() > 0) {
				antActions.runSelectedTarget();
			}
		}));
		gridPanel.add(new JScrollPane(targetList));
	}

	private void addProjectList(GridPanelBuilder gridPanel) {
		JList<WorkspaceProject> projectList = components.getProjectList();
		projectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		LineBorderFactory.setBorder(projectList, "projects");
		gridPanel.add(new JScrollPane(projectList));
	}

	@Inject
	public void addGroupSelectionListener(GroupSelectionListener listener) {
		components.getGroupList().addListSelectionListener(listener);
	}

	@Inject
	public void addTargetSelectionListener(TargetSelectionListener listener) {
		components.getTargetList().addListSelectionListener(listener);
	}

	@Inject
	public void addProjectListener(ProjectSelectionListener listener) {
		components.getProjectList().addListSelectionListener(listener);
	}

	public void show() {
		refresh.updateGroups();
		refresh.updateProjectList();
		refresh.updateTargets();

		components.getFrame().setVisible(true);
	}
}
