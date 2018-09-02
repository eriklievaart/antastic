package com.eriklievaart.antastic.ui.util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

import com.eriklievaart.toolkit.logging.api.LogTemplate;
import com.eriklievaart.toolkit.lang.api.ThrowableTool;
import com.eriklievaart.toolkit.lang.api.check.Check;

public class MouseDoubleClickListener implements MouseListener {
	protected LogTemplate log = new LogTemplate(getClass());

	private MouseLambda lambda;

	public MouseDoubleClickListener(MouseLambda lambda) {
		Check.notNull(lambda);
		this.lambda = lambda;
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		try {
			if (me.getButton() == 1 && me.getClickCount() == 2) {
				lambda.doubleClicked(me);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, ThrowableTool.getRootCause(e).getMessage());
			log.warn(e.getMessage(), e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public interface MouseLambda {
		public abstract void doubleClicked(MouseEvent e) throws Exception;
	}
}
