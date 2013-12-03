/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.canvas.Units;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;


/**
 * adapter between a {@link IGLCanvas} and {@link DragSource} and {@link DropTarget}
 *
 * at least on windows if a dragging operation is performed the mouse events are eaten up, therefore simulate them
 *
 * @author Samuel Gratzl
 *
 */
public class DnDAdapter implements DragSourceListener, DropTargetListener, KeyListener {
	private final IGLCanvas canvas;
	private final Iterable<IGLMouseListener> mouseListeners;

	/**
	 * wrapped {@link DragSourceListener}s
	 */
	private final Collection<DragSourceListener> sourceListeners = new CopyOnWriteArrayList<>();
	/**
	 * wrapped {@link DropTargetListener}s
	 */
	private final Collection<DropTargetListener> targetListeners = new CopyOnWriteArrayList<>();

	private DragSource source;
	private DropTarget target;

	/**
	 * on windows the {@link #dragOver(DropTargetEvent)} method will be called all the time, so a backup to just send if
	 * the position changed
	 */
	private Point old;
	private boolean isControlDown;
	private boolean isShiftDown;
	private boolean isAltDown;

	public DnDAdapter(IGLCanvas canvas, Iterable<IGLMouseListener> mouseListeners) {
		this.canvas = canvas;
		this.mouseListeners = mouseListeners;
	}

	public void init() {
		canvas.asComposite().addKeyListener(this);
	}

	/**
	 * lazy creation of target
	 */
	private void ensureTarget() {
		if (this.target != null)
			return;
		target = new DropTarget(canvas.asComposite(), DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK | DND.DROP_DEFAULT);
		target.setTransfer(new Transfer[] { CaleydoTransfer.getInstance(), FileTransfer.getInstance(),
				URLTransfer.getInstance(), TextTransfer.getInstance() });
		target.addDropListener(this);
	}

	/**
	 * lazy dipose of target
	 */
	private void freeTarget() {
		if (targetListeners.isEmpty() && target != null) {
			target.dispose();
			target = null;
		}
	}

	private void freeSource() {
		if (sourceListeners.isEmpty() && source != null) {
			source.dispose();
			source = null;
		}
	}

	private void ensureSource() {
		if (this.source != null)
			return;
		source = new DragSource(canvas.asComposite(), DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		source.setTransfer(new Transfer[] { CaleydoTransfer.getInstance(), FileTransfer.getInstance(),
				URLTransfer.getInstance(), TextTransfer.getInstance() });
		source.addDragListener(this);
	}

	public void addDragListener(DragSourceListener l) {
		sourceListeners.add(l);
		display().asyncExec(new Runnable() {
			@Override
			public void run() {
				ensureSource();
			}
		});
	}

	public void addDropListener(DropTargetListener l) {
		targetListeners.add(l);
		display().asyncExec(new Runnable() {
			@Override
			public void run() {
				ensureTarget();
			}
		});
	}

	public boolean removeDragListener(DragSourceListener l) {
		boolean r = sourceListeners.remove(l);
		if (r && sourceListeners.isEmpty())
			// sync exec here to avoid that the drag source will be disposed during canvas disposal, which will raise an
			// error
			display().syncExec(new Runnable() {
				@Override
				public void run() {
					freeSource();
				}
			});
		return r;
	}

	public boolean removeDropListener(DropTargetListener l) {
		boolean r = targetListeners.remove(l);
		if (r && targetListeners.isEmpty())
			display().syncExec(new Runnable() {
				@Override
				public void run() {
					freeTarget();
				}
			});
		return r;
	}

	private Display display() {
		return canvas.asComposite().getDisplay();
	}

	@Override
	public void keyPressed(KeyEvent event) {
		isControlDown = (event.stateMask & SWT.CONTROL) != 0 || event.keyCode == SWT.CONTROL;
		isShiftDown = (event.stateMask & SWT.SHIFT) != 0 || event.keyCode == SWT.SHIFT;
		isAltDown = (event.stateMask & SWT.ALT) != 0 || event.keyCode == SWT.ALT;
	}

	@Override
	public void keyReleased(KeyEvent event) {
		isControlDown = (event.stateMask & SWT.CONTROL) != 0 && event.keyCode != SWT.CONTROL;
		isShiftDown = (event.stateMask & SWT.SHIFT) != 0 && event.keyCode != SWT.SHIFT;
		isAltDown = (event.stateMask & SWT.ALT) != 0 && event.keyCode != SWT.ALT;
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		IMouseEvent mouseEvent = asEvent(event, getPoint(event));
		// simulate entered
		for (IGLMouseListener l : mouseListeners) {
			l.mouseEntered(mouseEvent);
		}

		if (targetListeners.isEmpty()) // no one listening -> aborting dnd operation
			event.detail = DND.DROP_NONE;
		else
			for (DropTargetListener l : targetListeners)
				l.dragEnter(event);
	}

	@Override
	public void dragLeave(DropTargetEvent event) {
		IMouseEvent mouseEvent = asEvent(event, getPoint(event));
		for (IGLMouseListener l : mouseListeners) {
			l.mouseExited(mouseEvent);
		}

		for (DropTargetListener l : targetListeners)
			l.dragLeave(event);

	}

	private Point getPoint(DropTargetEvent event) {
		return canvas.asComposite().toControl(event.x, event.y);
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		for (DropTargetListener l : targetListeners)
			l.dragOperationChanged(event);
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		Point p = getPoint(event);
		if (Objects.equals(p, old)) //ignore duplicate events
			return;
		old = p;
		IMouseEvent mouseEvent = asEvent(event, p);
		for (IGLMouseListener l : mouseListeners) {
			l.mouseMoved(mouseEvent);
		}

		for (DropTargetListener l : targetListeners)
			l.dragOver(event);
	}

	private IMouseEvent asEvent(DropTargetEvent event, Point p) {
		return new SWTDnDMouseEventAdapter(event, p, canvas.toDIP(new java.awt.Point(p.x, p.y)), isControlDown,
				isShiftDown, isAltDown);
	}

	@Override
	public void drop(DropTargetEvent event) {
		for (DropTargetListener l : targetListeners)
			l.drop(event);
	}

	@Override
	public void dropAccept(DropTargetEvent event) {
		for (DropTargetListener l : targetListeners)
			l.dropAccept(event);
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		if (sourceListeners.isEmpty()) // no one listening -> aborting dnd operation
			event.doit = false;
		else
			for (DragSourceListener l : sourceListeners)
				l.dragStart(event);

	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		for (DragSourceListener l : sourceListeners)
			l.dragSetData(event);
		if (event.data == null)
			event.doit = false; // invalid type
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
		for (DragSourceListener l : sourceListeners)
			l.dragFinished(event);
	}

	private static class SWTDnDMouseEventAdapter implements IMouseEvent {
		private final DropTargetEvent event;
		private final Vec2f point;
		private final Point raw;
		private boolean isControlDown;
		private boolean isShiftDown;
		private boolean isAltDown;

		SWTDnDMouseEventAdapter(DropTargetEvent event, Point p, Vec2f point, boolean isControlDown,
				boolean isShiftDown, boolean isAltDown) {
			this.event = event;
			this.raw = p;
			this.point = point;
			this.isControlDown = isControlDown;
			this.isShiftDown = isShiftDown;
			this.isAltDown = isAltDown;
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

		/**
		 * @return the isAltDown, see {@link #isAltDown}
		 */
		@Override
		public boolean isAltDown() {
			return isAltDown;
		}

		/**
		 * @return the isShiftDown, see {@link #isShiftDown}
		 */
		@Override
		public boolean isShiftDown() {
			return isShiftDown;
		}

		/**
		 * @return the isControlDown, see {@link #isControlDown}
		 */
		@Override
		public boolean isCtrlDown() {
			return isControlDown;
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
