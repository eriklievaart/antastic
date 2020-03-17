package com.eriklievaart.antastic.ui.main;

import java.io.File;

import javax.swing.JOptionPane;

import com.eriklievaart.antastic.config.AntConfig;
import com.eriklievaart.antastic.model.GlobalsIO;
import com.eriklievaart.antastic.ui.properties.PropertiesController;
import com.eriklievaart.antastic.ui.script.ScriptController;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.swing.api.menu.Accelerator;
import com.eriklievaart.toolkit.swing.api.menu.Command;
import com.eriklievaart.toolkit.swing.api.menu.Menu;
import com.google.inject.Inject;

@Menu(text = "project")
public class AntActions {

	@Inject
	private PropertiesController properties;
	@Inject
	private ScriptController scripts;
	@Inject
	private AntConfig ant;

	@Command(name = "global properties")
	public void globals() {
		properties.setData(GlobalsIO.readGlobals());
		properties.show("Set Global Properties", GlobalsIO::storeGlobals);
	}

	@Command(name = "script")
	@Accelerator("ctrl alt S")
	public void scriptEditor() {
		scripts.show();
	}

	@Command(name = "ant location")
	public void antLocation() {
		String message = "please enter the location of the ant executable";
		String input = JOptionPane.showInputDialog(null, message, ant.getExecutable());
		if(Str.isBlank(input) ) {
			return;
		}
		File file = new File(input);
		if(file.isFile()) {
			ant.setExecutable(file);
		} else {
			JOptionPane.showMessageDialog(null, "not a valid file: " + file);
		}
	}
}
