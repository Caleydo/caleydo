package org.caleydo.core.view.opengl.canvas.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Mouse wheel listener that uses a {@link AGLView} as handler.
 * 
 * @author Partl
 */
public class GLMouseWheelListener
	extends MouseAdapter
	implements MouseWheelListener, MouseMotionListener {

	private IMouseWheelHandler handler;

	public GLMouseWheelListener(IMouseWheelHandler handler) {
		this.handler = handler;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		handler.handleMouseWheel(event.getWheelRotation(), event.getPoint());
	}
}
