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
package org.caleydo.core.view.opengl.canvas.internal.newt;

import java.awt.Dimension;
import java.awt.Point;

import javax.media.opengl.GLDrawable;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

/**
 * @author Samuel Gratzl
 *
 */
final class NEWTMouseAdapter implements MouseListener {

	private final IGLMouseListener listener;

	public NEWTMouseAdapter(IGLMouseListener listener) {
		this.listener = listener;
	}

	/**
	 * @return the listener
	 */
	public IGLMouseListener getListener() {
		return listener;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		listener.mouseClicked(wrap(e));

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		listener.mouseEntered(wrap(e));
	}

	@Override
	public void mouseExited(MouseEvent e) {
		listener.mouseExited(wrap(e));
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
	public void mouseMoved(MouseEvent e) {
		listener.mouseMoved(wrap(e));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		listener.mouseDragged(wrap(e));
	}

	@Override
	public void mouseWheelMoved(MouseEvent e) {
		listener.mouseWheelMoved(wrap(e));
	}

	/**
	 * @param e
	 * @return
	 */
	private static IMouseEvent wrap(MouseEvent e) {
		return new NEWTMouseEventAdapter(e);
	}

	private static class NEWTMouseEventAdapter implements IMouseEvent {
		private final MouseEvent event;

		NEWTMouseEventAdapter(MouseEvent event) {
			this.event = event;
		}

		@Override
		public Point getPoint() {
			return new Point(event.getX(), event.getY());
		}

		@Override
		public int getClickCount() {
			return event.getClickCount();
		}

		@Override
		public int getWheelRotation() {
			return event.getWheelRotation();
		}

		@Override
		public int getButton() {
			return event.getButton();
		}

		@Override
		public boolean isButtonDown(int button) {
			return (event.getModifiers() & InputEvent.getButtonMask(button)) != 0;
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

		@Override
		public Dimension getParentSize() {
			Dimension size;
			Object source = event.getSource();
			if (source instanceof GLDrawable) {
				GLDrawable d = (GLDrawable) source;
				size = new Dimension(d.getWidth(), d.getHeight());
			} else if (source instanceof Composite) {
				Composite d = (Composite) source;
				size = new Dimension(d.getSize().x, d.getSize().y);
			} else if (source instanceof Window) {
				Window w = (Window) source;
				size = new Dimension(w.getWidth(), w.getHeight());
			} else {
				Logger.log(new Status(IStatus.ERROR, this.toString(), "can't determine size"));
				size = new Dimension(1, 1);
			}
			return size;
		}

	}

}
