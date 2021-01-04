package com.eriklievaart.antastic.ui.main;

import java.util.List;
import java.util.Optional;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eriklievaart.antastic.model.LastSelection;
import com.eriklievaart.antastic.model.Group;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.google.inject.Inject;

public class ProjectSelectionListener implements ListSelectionListener {

	@Inject
	private MainComponents components;
	@Inject
	private MainRefresh refresh;
	@Inject
	private LastSelection last;

	@Override
	public void valueChanged(ListSelectionEvent event) {
		WorkspaceProject project = components.getProjectList().getSelectedValue();
		if (project == null) {
			return;
		}
		storeLastSelectedProjectForGroup(project);
		refresh.updateTargets();
	}

	private void storeLastSelectedProjectForGroup(WorkspaceProject project) {
		List<Group> selectedGroups = components.getGroupList().getSelectedValuesList();
		if (selectedGroups == null || selectedGroups.size() != 1) {
			return;
		}
		Group selectedGroup = components.getGroupList().getSelectedValue();
		Optional<WorkspaceProject> optional = last.getProject(selectedGroup);
		if (optional.isPresent() && optional.get().equals(project)) {
			return;
		}
		last.setProject(selectedGroup, project);
	}
}
