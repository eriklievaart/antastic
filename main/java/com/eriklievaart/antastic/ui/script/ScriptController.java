package com.eriklievaart.antastic.ui.script;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.eriklievaart.antastic.ant.AntJobRunner;
import com.eriklievaart.antastic.ant.AntScript;
import com.eriklievaart.antastic.config.AntasticConfig;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.antastic.model.WorkspaceProjectManager;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.ActionLambda;
import com.eriklievaart.toolkit.swing.api.text.UndoTool;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ScriptController {
	private static final File SCRIPT_CACHE = new File(ApplicationPaths.getDataDir(), "script");
	private LogTemplate log = new LogTemplate(getClass());

	private ScriptComponents components;

	@Inject
	private AntJobRunner runner;
	@Inject
	private AntasticConfig config;
	@Inject
	private WorkspaceProjectManager workspace;

	@Inject
	public ScriptController(ScriptComponents components) {
		this.components = components;

		components.getJFrame().add(new JScrollPane(components.getArea()), BorderLayout.CENTER);
		components.getJFrame().add(components.getRunButton(), BorderLayout.SOUTH);
		components.getRunButton().addActionListener(e -> runScript(components.getArea().getText()));
		initArea();
	}

	private void initArea() {
		JTextArea area = components.getArea();
		area.setText(loadScript());
		UndoTool.addUndoFunctionality(area);
		addSaveAction();
	}

	private void addSaveAction() {
		KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
		KeyStroke ctrlR = KeyStroke.getKeyStroke("control R");
		bind(ctrlS, "save.script", e -> storeScript());
		bind(ctrlR, "run.selection", e -> runScript(components.getArea().getSelectedText()));
	}

	private void bind(KeyStroke stroke, String actionId, Consumer<ActionEvent> consumer) {
		Check.notNull(stroke);
		components.getArea().getInputMap().put(stroke, actionId);
		components.getArea().getActionMap().put(actionId, new ActionLambda(consumer));
	}

	private void runScript(String text) {
		storeScript();
		new Thread(() -> {
			try {
				if (text == null) {
					JOptionPane.showMessageDialog(null, "No lines selected!");
				} else {
					runner.run(new AntScript(config, workspace).queueRaw(text));
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(components.getJFrame(), "Exception in script: " + e.getMessage());
				log.warn("Unable to execute script", e);
			}
		}).start();

	}

	public void show() {
		components.getJFrame().setVisible(true);
		components.getRunButton().requestFocus();
	}

	private String loadScript() {
		try {
			String data = FileTool.toString(SCRIPT_CACHE);
			if (!Str.isBlank(data)) {
				return data;
			}
		} catch (RuntimeIOException ignore) {
		}
		return "# [property]=[value]\n# [project] [target]\n\n\n";
	}

	private void storeScript() {
		try {
			log.info("saving script to $", SCRIPT_CACHE);
			FileTool.writeStringToFile(components.getArea().getText(), SCRIPT_CACHE);
		} catch (RuntimeIOException e) {
			log.warn(e);
		}
	}
}
