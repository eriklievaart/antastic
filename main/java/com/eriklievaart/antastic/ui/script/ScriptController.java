package com.eriklievaart.antastic.ui.script;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.eriklievaart.antastic.ant.AntScriptRunner;
import com.eriklievaart.antastic.config.ApplicationPaths;
import com.eriklievaart.toolkit.io.api.FileTool;
import com.eriklievaart.toolkit.io.api.RuntimeIOException;
import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.str.Str;
import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.swing.api.ActionLambda;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ScriptController {
	private static final File SCRIPT_CACHE = new File(ApplicationPaths.getDataDir(), "script");
	private LogTemplate log = new LogTemplate(getClass());

	private ScriptComponents components;

	@Inject
	private AntScriptRunner script;

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
		area.setBackground(Color.DARK_GRAY);
		area.setForeground(Color.WHITE);
		area.setCaretColor(Color.YELLOW);

		addSaveAction();
	}

	private void addSaveAction() {
		Font font = components.getArea().getFont().deriveFont(Font.PLAIN).deriveFont(18.0f);
		components.getArea().setFont(font);

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
				script.run(text);

			} catch (Exception e) {
				JOptionPane.showMessageDialog(components.getJFrame(), "Exception in script: " + e.getMessage());
				log.warn("Unable to execute script", e);
			}
		}).start();

	}

	public void show() {
		components.getJFrame().setVisible(true);
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
