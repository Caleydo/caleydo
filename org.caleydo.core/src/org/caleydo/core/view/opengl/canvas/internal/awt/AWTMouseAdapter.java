/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.awt;

import java.awt.Dimension;
import java.awt.Point;
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

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getPoint()
		 */
		@Override
		public Point getPoint() {
			return event.getPoint();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getClickCount()
		 */
		@Override
		public int getClickCount() {
			return event.getClickCount();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getWheelRotation()
		 */
		@Override
		public int getWheelRotation() {
			return (event instanceof MouseWheelEvent) ? ((MouseWheelEvent) event).getWheelRotation() : 0;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getButton()
		 */
		@Override
		public int getButton() {
			return event.getButton();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#isButtonDown(int)
		 */
		@Override
		public boolean isButtonDown(int button) {
			switch (button) {
			case 1:
				return (event.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
			case 2:
				return (event.getModifiersEx() & MouseEvent.BUTTON2_DOWN_MASK) != 0;
			case 3:
				return (event.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0;
			}
			return false;
		}


		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getParentSize()
		 */
		@Override
		public Dimension getParentSize() {
			return event.getComponent().getSize();
		}

	}

}
