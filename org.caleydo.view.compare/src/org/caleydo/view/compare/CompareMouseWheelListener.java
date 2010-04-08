package org.caleydo.view.compare;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class CompareMouseWheelListener extends MouseAdapter implements
		MouseWheelListener, MouseMotionListener {

	private GLMatchmaker glCompare;

	public CompareMouseWheelListener(GLMatchmaker glCompare) {
		this.glCompare = glCompare;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		glCompare.handleMouseWheel(event.getWheelRotation(), event.getPoint());
	}

}
