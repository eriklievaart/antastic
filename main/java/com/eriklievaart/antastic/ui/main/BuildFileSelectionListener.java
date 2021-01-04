package com.eriklievaart.antastic.ui.main;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.inject.Inject;

public class BuildFileSelectionListener implements ListSelectionListener {

	@Inject
	private MainRefresh refresh;

	@Override
	public void valueChanged(ListSelectionEvent event) {
		refresh.updateTargets();
	}
}
