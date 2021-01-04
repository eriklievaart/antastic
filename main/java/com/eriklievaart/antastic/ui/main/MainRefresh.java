package com.eriklievaart.antastic.ui.main;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

import javax.swing.JList;

import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.model.BuildFile;
import com.eriklievaart.antastic.model.Group;
import com.eriklievaart.antastic.model.LastSelection;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.antastic.model.WorkspaceProjectManager;
import com.google.inject.Inject;

public class MainRefresh {

	@Inject
	private MainComponents components;
	@Inject
	private WorkspaceProjectManager projects;
	@Inject
	private AntasticConfig config;
	@Inject
	private LastSelection last;

	public void updateGroups() {
		JList<Group> list = components.getGroupList();
		Group[] groups = config.getGroups();
		list.setListData(groups);

		Optional<Group> lastGroup = last.getGroup();
		if (lastGroup.isPresent()) {
			for (int i = 0; i < groups.length; i++) {
				if (groups[i].getName().equals(lastGroup.get().getName())) {
					list.setSelectedIndex(i);
					return;
				}
			}
		}
	}

	public void updateProjectList() {
		List<Group> selectedGroups = components.getGroupList().getSelectedValuesList();

		if (selectedGroups == null || selectedGroups.isEmpty()) {
			components.getProjectList().setListData(projects.toArray());

		} else {
			components.getProjectList().setListData(getProjectsInGroups(selectedGroups));
			selectProject(last.getProject(selectedGroups.get(0)));
		}
	}

	private void selectProject(Optional<WorkspaceProject> optional) {
		JList<WorkspaceProject> projectList = components.getProjectList();

		if (optional.isPresent()) {
			String select = optional.get().getName();
			for (int i = 0; i < projectList.getModel().getSize(); i++) {
				String current = projectList.getModel().getElementAt(i).getName();
				if (current.equals(select)) {
					projectList.setSelectedIndex(i);
					return;
				}
			}
		} else {
			projectList.clearSelection();
		}
	}

	private Vector<WorkspaceProject> getProjectsInGroups(List<Group> selectedGroups) {
		Vector<WorkspaceProject> filtered = new Vector<>();

		for (WorkspaceProject project : projects.toArray()) {
			for (Group group : selectedGroups) {
				if (group.containsProject(project)) {
					filtered.addElement(project);
					break;
				}
			}
		}
		return filtered;
	}

	public void updateTargets() {
		BuildFile file = config.getBuildFile();
		JList<String> list = components.getTargetList();
		if (file == null) {
			list.setListData(new String[] {});
			return;
		}
		WorkspaceProject selectedProject = components.getProjectList().getSelectedValue();
		String[] available = file.getAvailableTargets(selectedProject);
		list.setListData(available);

		Optional<String> optional = last.getTarget(selectedProject);
		if (optional.isPresent()) {
			for (int i = 0; i < available.length; i++) {
				if (available[i].equals(optional.get())) {
					list.setSelectedIndex(i);
				}
			}
		}
	}
}