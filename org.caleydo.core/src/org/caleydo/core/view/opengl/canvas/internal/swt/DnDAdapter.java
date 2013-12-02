/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal.swt;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.util.Objects;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.canvas.Units;
import org.caleydo.core.view.opengl.canvas.internal.CaleydoJAXBTransfer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * @author Samuel Gratzl
 *
 */
public class DnDAdapter implements DragSourceListener, DropTargetListener {

	private final DragSource source;
	private final DropTarget target;
	private final SWTGLCanvas canvas;

	private Point old;

	/**
	 *
	 */
	public DnDAdapter(SWTGLCanvas canvas) {
		this.canvas = canvas;
		Composite c = canvas.asComposite();
		source = new DragSource(c, DND.DROP_MOVE | DND.DROP_COPY);
		source.setTransfer(new Transfer[] { TextTransfer.getInstance(), CaleydoJAXBTransfer.getInstance() });
		source.addDragListener(this);

		target = new DropTarget(c, DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);
		target.setTransfer(new Transfer[] { CaleydoJAXBTransfer.getInstance(), TextTransfer.getInstance() });
		target.addDropListener(this);

	}

	private Iterable<IGLMouseListener> mouseListeners() {
		return canvas.getMouseListeners();
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		event.detail = DND.DROP_NONE;

		IMouseEvent mouseEvent = asEvent(event, getPoint(event));
		for(IGLMouseListener l : mouseListeners()) {
			l.mouseEntered(mouseEvent);
		}
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
		IMouseEvent mouseEvent = asEvent(event, getPoint(event));
		for(IGLMouseListener l : mouseListeners()) {
			l.mouseExited(mouseEvent);
		}
	}

	private Point getPoint(DropTargetEvent event) {
		return canvas.asComposite().toControl(event.x, event.y);
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragOver(DropTargetEvent event) {
		Point p = getPoint(event);
		if (Objects.equals(p, old)) //ignore duplicate events
			return;
		System.out.println("fire drag over mouse move event");
		old = p;
		IMouseEvent mouseEvent = asEvent(event, p);
		for(IGLMouseListener l : mouseListeners()) {
			l.mouseMoved(mouseEvent);
		}
	}

	private IMouseEvent asEvent(DropTargetEvent event, Point p) {
		return new SWTDnDMouseEventAdapter(event, p, canvas.toDIP(new java.awt.Point(p.x, p.y)));
	}

	@Override
	public void drop(DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = false;
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		// TODO Auto-generated method stub

	}

	private static class SWTDnDMouseEventAdapter implements IMouseEvent {
		private final DropTargetEvent event;
		private final Vec2f point;
		private final Point raw;

		SWTDnDMouseEventAdapter(DropTargetEvent event, Point p, Vec2f point) {
			this.event = event;
			this.raw = p;
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
		public java.awt.Point getRAWPoint() {
			return new java.awt.Point(raw.x, raw.y);
		}

		@Override
		public int getClickCount() {
			return 0;
		}

		@Override
		public int getWheelRotation() {
			return 0;
		}

		@Override
		public int getButton() {
			return SWT.BUTTON1;
		}

		@Override
		public boolean isButtonDown(int button) {
			switch (button) {
			case 1:
				return true;
			default:
				return false;
			}
		}

		@Override
		public boolean isAltDown() {
			return (event.detail & DND.DROP_LINK) != 0;
		}

		@Override
		public boolean isCtrlDown() {
			return (event.detail & DND.DROP_COPY) != 0;
		}

		@Override
		public boolean isShiftDown() {
			return false;
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
