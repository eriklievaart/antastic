package com.eriklievaart.antastic.ui.main;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.eriklievaart.antastic.config.ProjectLocation;
import com.eriklievaart.toolkit.lang.api.str.Str;

public class FileLocationActions {

	private String nameLabel = "enter name:";
	private String pathLabel = "enter path:";

	private JTextField nameField = new JTextField();
	private JTextField pathField = new JTextField();

	private int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;

	public void setSelectFile() {
		fileSelectionMode = JFileChooser.FILES_ONLY;
	}

	public void setSelectDirectory() {
		fileSelectionMode = JFileChooser.DIRECTORIES_ONLY;
	}

	public String getNameLabel() {
		return nameLabel;
	}

	public void setNameLabel(String value) {
		this.nameLabel = value;
	}

	public String getPathLabel() {
		return pathLabel;
	}

	public void setPathLabel(String value) {
		this.pathLabel = value;
	}

	public ProjectLocation getFileLocation() {
		return new ProjectLocation(nameField.getText(), new File(pathField.getText()));
	}

	public boolean askUser() {
		int result = JOptionPane.showConfirmDialog(null, createPanel(), "input required", JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION) {
			return false;
		}
		if (Str.isBlank(nameField.getText()) || Str.isBlank(pathField.getText())) {
			JOptionPane.showMessageDialog(null, "both fields are required!");
			return askUser();
		}
		return true;
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));

		panel.add(new JLabel(nameLabel));
		panel.add(nameField);
		panel.add(new JLabel(pathLabel));
		panel.add(pathField);
		panel.add(createBrowseButton());
		panel.setMinimumSize(new Dimension(200, 50));

		return panel;
	}

	private JButton createBrowseButton() {
		JButton browseButton = new JButton("browse...");
		browseButton.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(fileSelectionMode);
			if (chooser.showOpenDialog(browseButton) == JFileChooser.APPROVE_OPTION) {
				pathField.setText(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		return browseButton;
	}

	public boolean isDirectory() {
		return new File(pathField.getText()).isDirectory();
	}

	public boolean isFile() {
		return new File(pathField.getText()).isFile();
	}
}
