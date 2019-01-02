package com.eriklievaart.antastic.ui.util;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class LineBorderFactory {

	public static void setBorder(JComponent component, String title) {
		Color c = Color.GRAY;
		Border l = BorderFactory.createLineBorder(c);
		component.setBorder(BorderFactory.createTitledBorder(l, title, TitledBorder.CENTER, TitledBorder.TOP, null, c));
	}
}
