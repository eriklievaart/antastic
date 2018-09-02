package com.eriklievaart.antastic.ui.properties;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.google.inject.Singleton;

@Singleton
public class PropertiesComponents {

	private JFrame frame = new JFrame();
	private JTextArea area = new JTextArea();
	private JLabel label = new JLabel("Enter properties; one property per line, key and value separated by '='.");
	private JButton saveButton = new JButton("Save");

	public PropertiesComponents() {
		frame.setName("properties");
		frame.getContentPane().add(label, BorderLayout.NORTH);
		frame.getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
		frame.getContentPane().add(saveButton, BorderLayout.SOUTH);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public JTextArea getArea() {
		return area;
	}

	public void setArea(JTextArea area) {
		this.area = area;
	}

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(JButton saveButton) {
		this.saveButton = saveButton;
	}
}
