/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.newt;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Point;

import javax.media.opengl.GLDrawable;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.AGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.canvas.Units;
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
	private final AGLCanvas canvas;

	public NEWTMouseAdapter(IGLMouseListener listener, AGLCanvas canvas) {
		this.listener = listener;
		this.canvas = canvas;
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
	private IMouseEvent wrap(MouseEvent e) {
		return new NEWTMouseEventAdapter(e, canvas.toDIP(new Point(e.getX(), e.getY())));
	}

	private static class NEWTMouseEventAdapter implements IMouseEvent {
		private final MouseEvent event;
		private final Vec2f point;

		NEWTMouseEventAdapter(MouseEvent event, Vec2f point) {
			this.event = event;
			this.point = point;
		}

		@Override
		public Point getRAWPoint() {
			return new Point(event.getX(), event.getY());
		}

		@Override
		public Vec2f getPoint() {
			return point;
		}

		@Override
		public Vec2f getPoint(Units unit) {
			return unit.unapply(point);
		}

		@Override
		public int getClickCount() {
			return event.getClickCount();
		}

		@Override
		public int getWheelRotation() {
			return (int) event.getRotationScale();
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
				size = new Dimension(d.getSurfaceWidth(), d.getSurfaceHeight());
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
