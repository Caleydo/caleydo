/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.picking;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;
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
		Point pickPoint = view.getGLMouseListener().getPickedPoint();

		super.doPicking(mode, pickPoint, null, gl, new Runnable() {
			@Override
			public void run() {
				view.display(gl);
			}
		});
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
