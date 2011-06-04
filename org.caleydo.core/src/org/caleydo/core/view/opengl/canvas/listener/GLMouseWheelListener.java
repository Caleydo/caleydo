package org.caleydo.core.view.opengl.canvas.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.caleydo.core.view.opengl.canvas.AGLView;

public class GLMouseWheelListener
	extends MouseAdapter
	implements MouseWheelListener, MouseMotionListener {
	
	private AGLView handler;

	public GLMouseWheelListener(AGLView handler) {
		this.handler = handler;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		handler.handleMouseWheel(event.getWheelRotation(), event.getPoint());
	}

}
