package com.eriklievaart.antastic.ui.main;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eriklievaart.antastic.model.LastSelection;
import com.google.inject.Inject;

public class GroupSelectionListener implements ListSelectionListener {

	@Inject
	private MainRefresh refresh;
	@Inject
	private MainComponents components;
	@Inject
	private LastSelection last;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		refresh.updateProjectList();
		last.setWorkspaceSelected(components.getGroupList().getSelectedValue());
	}
}
