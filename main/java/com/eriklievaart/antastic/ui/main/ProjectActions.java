package com.eriklievaart.antastic.ui.main;

import com.eriklievaart.antastic.model.GlobalsIO;
import com.eriklievaart.antastic.ui.properties.PropertiesController;
import com.eriklievaart.antastic.ui.script.ScriptController;
import com.eriklievaart.toolkit.swing.api.menu.Command;
import com.eriklievaart.toolkit.swing.api.menu.Menu;
import com.google.inject.Inject;

@Menu(text = "project")
public class ProjectActions {

	@Inject
	private PropertiesController properties;
	@Inject
	private ScriptController scripts;

	@Command(name = "global properties")
	public void globals() {
		properties.setData(GlobalsIO.readGlobals());
		properties.show("Set Global Properties", GlobalsIO::storeGlobals);
	}

	@Command(name = "script")
	public void scriptEditor() {
		scripts.show();
	}
}
