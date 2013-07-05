/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;

/**
 * @author Samuel Gratzl
 *
 */
final class AWTMouseAdapter implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final IGLMouseListener listener;

	public AWTMouseAdapter(IGLMouseListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IGLMouseListener getListener() {
		return listener;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		listener.mouseWheelMoved(wrap(e));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		listener.mouseDragged(wrap(e));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		listener.mouseMoved(wrap(e));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		listener.mouseClicked(wrap(e));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		listener.mousePressed(wrap(e));
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		listener.mouseReleased(wrap(e));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		listener.mouseEntered(wrap(e));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		listener.mouseExited(wrap(e));
	}

	/**
	 * @param e
	 * @return
	 */
	private static IMouseEvent wrap(MouseEvent e) {
		return new AWTMouseEventAdapter(e);
	}

	private static class AWTMouseEventAdapter implements IMouseEvent {
		private final MouseEvent event;

		AWTMouseEventAdapter(MouseEvent event) {
			this.event = event;
		}

		@Override
		public Point getPoint() {
			return event.getPoint();
		}

		@Override
		public int getClickCount() {
			return event.getClickCount();
		}

		@Override
		public int getWheelRotation() {
			return (event instanceof MouseWheelEvent) ? ((MouseWheelEvent) event).getWheelRotation() : 0;
		}

		@Override
		public int getButton() {
			return event.getButton();
		}

		@Override
		public boolean isButtonDown(int button) {
			switch (button) {
			case 1:
				return (event.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0;
			case 2:
				return (event.getModifiersEx() & InputEvent.BUTTON2_DOWN_MASK) != 0;
			case 3:
				return (event.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) != 0;
			}
			return false;
		}

		@Override
		public Dimension getParentSize() {
			return event.getComponent().getSize();
		}

		@Override
		public boolean isAltDown() {
			return event.isAltDown();
		}

		@Override
		public boolean isCtrlDown() {
			return event.isControlDown();
		}

		@Override
		public boolean isShiftDown() {
			return event.isShiftDown();
		}
	}
}
