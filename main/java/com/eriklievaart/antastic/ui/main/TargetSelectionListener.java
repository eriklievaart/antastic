package com.eriklievaart.antastic.ui.main;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.eriklievaart.antastic.model.LastSelection;
import com.google.inject.Inject;

public class TargetSelectionListener implements ListSelectionListener {

	@Inject
	private MainComponents components;
	@Inject
	private LastSelection last;

	@Override
	public void valueChanged(ListSelectionEvent e) {
		last.setTarget(components.getProjectList().getSelectedValue(), components.getTargetList().getSelectedValue());
	}
}