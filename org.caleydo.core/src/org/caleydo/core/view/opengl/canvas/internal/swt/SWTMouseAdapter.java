/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.swt;


import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Point;

import org.caleydo.core.view.opengl.canvas.AGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.canvas.Units;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Control;

/**
 * @author Samuel Gratzl
 *
 */
final class SWTMouseAdapter implements MouseListener, MouseMoveListener, MouseWheelListener, MouseTrackListener,
		MenuDetectListener, DragDetectListener {

	private final IGLMouseListener listener;
	private final AGLCanvas canvas;

	public SWTMouseAdapter(IGLMouseListener listener, AGLCanvas canvas) {
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
	public void mouseScrolled(MouseEvent e) {
		listener.mouseWheelMoved(wrap(e));
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		listener.mousePressed(wrap(e)); // FIXME
	}

	@Override
	public void mouseDown(MouseEvent e) {
		listener.mousePressed(wrap(e));
	}

	@Override
	public void dragDetected(DragDetectEvent e) {
		listener.mouseDragDetected(wrap(e));
	}

	@Override
	public void menuDetected(MenuDetectEvent e) {
		//
	}

	@Override
	public void mouseUp(MouseEvent e) {
		listener.mouseReleased(wrap(e));
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if ((e.stateMask & SWT.BUTTON_MASK) != 0) // any button
			listener.mouseDragged(wrap(e));
		else
			listener.mouseMoved(wrap(e));
	}

	@Override
	public void mouseEnter(MouseEvent e) {
		listener.mouseEntered(wrap(e));
	}

	@Override
	public void mouseExit(MouseEvent e) {
		listener.mouseExited(wrap(e));
	}

	@Override
	public void mouseHover(MouseEvent e) {
		//
	}

	private IMouseEvent wrap(MouseEvent e) {
		return new SWTMouseEventAdapter(e, canvas.toDIP(new Point(e.x, e.y)));
	}

	private static class SWTMouseEventAdapter implements IMouseEvent {
		private final MouseEvent event;
		private final Vec2f point;

		SWTMouseEventAdapter(MouseEvent event, Vec2f point) {
			this.event = event;
			this.point = point;
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
		public Point getRAWPoint() {
			return new Point(event.x, event.y);
		}

		@Override
		public int getClickCount() {
			return event.count;
		}

		@Override
		public int getWheelRotation() {
			return event.count;
		}

		@Override
		public int getButton() {
			return event.button;
		}

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

		@Override
		public boolean isAltDown() {
			return (event.stateMask & SWT.ALT) != 0;
		}

		@Override
		public boolean isCtrlDown() {
			return (event.stateMask & SWT.CTRL) != 0;
		}

		@Override
		public boolean isShiftDown() {
			return (event.stateMask & SWT.SHIFT) != 0;
		}

		@Override
		public Dimension getParentSize() {
			if (event.widget instanceof Control) {
				org.eclipse.swt.graphics.Point size = ((Control) event.widget).getSize();
				return new Dimension(size.x, size.y);
			}
			return new Dimension(1, 1); // TODO log
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SWTMouseEventAdapter [point=");
			builder.append(point);
			builder.append(", getPoint()=");
			builder.append(getPoint());
			builder.append(", getButton()=");
			builder.append(getButton());
			builder.append("]");
			return builder.toString();
		}

	}
}
