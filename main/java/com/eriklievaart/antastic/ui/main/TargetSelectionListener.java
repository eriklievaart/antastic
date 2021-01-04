package com.eriklievaart.antastic.ui.main;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eriklievaart.antastic.model.LastSelection;
import com.eriklievaart.antastic.model.WorkspaceProject;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.google.inject.Inject;

public class TargetSelectionListener implements ListSelectionListener {
	private LogTemplate log = new LogTemplate(getClass());

	@Inject
	private MainComponents components;
	@Inject
	private LastSelection last;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		WorkspaceProject project = components.getProjectList().getSelectedValue();
		String target = components.getTargetList().getSelectedValue();
		log.trace("executing $ $", project.getName(), target);
		last.setTarget(project, target);
	}
}
