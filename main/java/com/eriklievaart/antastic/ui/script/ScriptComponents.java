package com.eriklievaart.antastic.ui.script;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.eriklievaart.toolkit.swing.api.builder.JFrameBuilder;
import com.google.inject.Singleton;

@Singleton
public class ScriptComponents {

	private JFrame frame = new JFrameBuilder("script").title("script").create();
	private JTextArea area = new JTextArea();
	private JButton runButton = new JButton("Run");

	public JTextArea getArea() {
		return area;
	}

	public JButton getRunButton() {
		return runButton;
	}

	public JFrame getJFrame() {
		return frame;
	}
}
