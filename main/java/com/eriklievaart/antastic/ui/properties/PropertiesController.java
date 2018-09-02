package com.eriklievaart.antastic.ui.properties;

import javax.swing.JFrame;

import com.google.inject.Inject;

public class PropertiesController {

	@Inject
	private PropertiesComponents components;

	public void show(String title, PropertiesCallback callback) {
		JFrame frame = components.getFrame();
		frame.setTitle(title);
		frame.setVisible(true);

		components.getSaveButton().addActionListener(e -> {
			frame.setVisible(false);
			callback.call(components.getArea().getText());
		});
	}

	public void setData(String data) {
		components.getArea().setText(data);
	}
}
