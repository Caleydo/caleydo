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
package org.caleydo.core.view.opengl.canvas.internal.swt;


import java.awt.Dimension;
import java.awt.Point;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Control;

/**
 * @author Samuel Gratzl
 *
 */
final class SWTMouseAdapter implements MouseListener, MouseMoveListener, MouseWheelListener {

	private final IGLMouseListener listener;
	private boolean mouseDown = false;

	public SWTMouseAdapter(IGLMouseListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IGLMouseListener getListener() {
		return listener;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.MouseWheelListener#mouseScrolled(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseScrolled(MouseEvent e) {
		listener.mouseWheelMoved(wrap(e));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent e) {
		listener.mousePressed(wrap(e)); // FIXME
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(MouseEvent e) {
		mouseDown = true;
		listener.mousePressed(wrap(e));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(MouseEvent e) {
		mouseDown = false;
		listener.mouseReleased(wrap(e));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseMove(MouseEvent e) {
		if ((e.stateMask & SWT.BUTTON_MASK) != 0) // any button
			listener.mouseDragged(wrap(e));
		else
			listener.mouseMoved(wrap(e));
	}

	/**
	 * @param e
	 * @return
	 */
	private static IMouseEvent wrap(MouseEvent e) {
		return new SWTMouseEventAdapter(e);
	}

	private static class SWTMouseEventAdapter implements IMouseEvent {
		private final MouseEvent event;

		SWTMouseEventAdapter(MouseEvent event) {
			this.event = event;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getPoint()
		 */
		@Override
		public Point getPoint() {
			return new Point(event.x, event.y);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getClickCount()
		 */
		@Override
		public int getClickCount() {
			return event.count;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getWheelRotation()
		 */
		@Override
		public int getWheelRotation() {
			return event.count;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent#getButton()
		 */
		@Override
		public int getButton() {
			return event.button;
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
				return (event.stateMask & SWT.BUTTON1) != 0;
			case 2:
				return (event.stateMask & SWT.BUTTON2) != 0;
			case 3:
				return (event.stateMask & SWT.BUTTON3) != 0;
			case 4:
				return (event.stateMask & SWT.BUTTON4) != 0;
			case 5:
				return (event.stateMask & SWT.BUTTON5) != 0;
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
			if (event.widget instanceof Control) {
				org.eclipse.swt.graphics.Point size = ((Control) event.widget).getSize();
				return new Dimension(size.x, size.y);
			}
			return new Dimension(1, 1); // TODO log
		}

	}
}
