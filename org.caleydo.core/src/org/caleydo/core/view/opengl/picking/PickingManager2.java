/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.canvas.Units;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;

/**
 * simpler version of {@link PickingManager} for a view specific setup
 *
 * registration: {@link #addPickingListener(Object, int, IPickingListener)} or
 * {@link #addTypePickingListener(Object, IPickingListener)} or {@link #register(Object)} using annotation
 *
 * querying: {@link #getPickingID(Object, int)}
 *
 * executing: {@link #doPicking(GL2, AGLView)}
 *
 * @author Samuel Gratzl
 *
 */
public class PickingManager2 extends APickingManager<PickingManager2.PickingEntry> {
	protected static class PickingEntry extends APickingEntry implements Iterable<IPickingListener> {
		private final PickingListenerComposite listener = new PickingListenerComposite(1);
		private final PickingListenerComposite typeListener;

		public PickingEntry(int objectId, PickingListenerComposite typeListener) {
			super(objectId);
			this.typeListener = typeListener;
		}

		/**
		 * @return
		 */
		public boolean isEmpty() {
			return listener.isEmpty();
		}

		@Override
		protected void fire(Pick pick) {
			listener.pick(pick);
			typeListener.pick(pick);
		}

		@Override
		public Iterator<IPickingListener> iterator() {
			return listener.iterator();
		}

		public void add(IPickingListener l) {
			listener.add(l);
		}

		public boolean remove(IPickingListener l) {
			return listener.remove(l);
		}
	}

	private final Map<Integer, SpacePickingManager> spacePickingManagers = new HashMap<>();

	/**
	 * uses the given {@link GLMouseListener} to convert the current state to a {@link PickingMode}
	 *
	 * @param glMouseListener
	 * @return
	 */
	protected static PickingMode convertToPickingMode(GLMouseListener glMouseListener) {
		if (glMouseListener.wasMouseDoubleClicked()) {
			return PickingMode.DOUBLE_CLICKED;
		} else if (glMouseListener.wasMouseDragged()) {
			return PickingMode.DRAGGED;
		} else if (glMouseListener.wasLeftMouseButtonPressed()) {
			return PickingMode.CLICKED;
		} else if (glMouseListener.wasRightMouseButtonPressed()) {
			return PickingMode.RIGHT_CLICKED;
		} else if (glMouseListener.wasMouseMoved()) {
			return PickingMode.MOUSE_MOVED;
		} else if (glMouseListener.wasMouseReleased()) {
			return PickingMode.MOUSE_RELEASED;
		} else {
			return null; // no picking
		}
	}

	/**
	 * returns a space specific picking manager, e.g. one per view instance
	 *
	 * @param spaceId
	 * @return
	 */
	public SpacePickingManager getSpace(int spaceId) {
		SpacePickingManager p = spacePickingManagers.get(spaceId);
		if (p == null) {
			p = new SpacePickingManager(this);
			spacePickingManagers.put(spaceId, p);
		}
		return p;
	}

	/**
	 * see {@link #doPicking(GL2, PickingMode, Point, IRenderable)} but simpler version for a {@link AGLView}
	 *
	 * @param gl
	 * @param view
	 */
	public void doPicking(final GL2 gl, final AGLView view) {
		PickingMode mode = convertToPickingMode(view.getGLMouseListener());

		Point pickPoint = view.getGLMouseListener().getRAWPickedPoint();
		Vec2f pickDIPPoint = view.getGLMouseListener().getDIPPickedPoint();

		super.doPicking(mode, new DummyMouseEvent(pickPoint, pickDIPPoint), gl, new Runnable() {
			@Override
			public void run() {
				view.display(gl);
			}
		});
	}

	private static class DummyMouseEvent implements IMouseEvent {
		private final Point rawPoint;
		private final Vec2f point;

		public DummyMouseEvent(Point rawPoint, Vec2f point) {
			this.rawPoint = rawPoint;
			this.point = point;
		}

		@Override
		public Vec2f getPoint(Units unit) {
			return unit.unapply(point);
		}

		@Override
		public Vec2f getDIPPoint() {
			return point;
		}

		@Override
		public Point getRAWPoint() {
			return rawPoint;
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
			return 0;
		}

		@Override
		public boolean isButtonDown(int button) {
			return false;
		}

		@Override
		public Dimension getParentSize() {
			return null;
		}

		@Override
		public boolean isShiftDown() {
			return false;
		}

		@Override
		public boolean isAltDown() {
			return false;
		}

		@Override
		public boolean isCtrlDown() {
			return false;
		}

	}

	/**
	 * @param pickingId
	 * @return
	 */
	protected PickingEntry find(int pickingId) {
		return get(pickingId);
	}


	PickingEntry createEntry(int objectId, PickingListenerComposite typeListener) {
		PickingEntry entry = new PickingEntry(objectId, typeListener);
		add(entry);
		return entry;
	}

	void removeEntry(PickingEntry entry) {
		super.remove(entry);
	}
}
