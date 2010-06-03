package org.caleydo.view.dataflipper;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ZoomMouseWheelListener extends MouseAdapter implements
		MouseWheelListener, MouseMotionListener {
	
	private GLDataFlipper dataFlipper;

	public ZoomMouseWheelListener(GLDataFlipper master) {
		dataFlipper = master;
	}

	public void mouseWheelMoved(MouseWheelEvent event) {

		dataFlipper.showFocusViewFullScreen(event.getWheelRotation() > 0 ? true : false);
	}
}