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
package org.caleydo.core.view.opengl.picking;

import static org.caleydo.core.view.opengl.picking.PickingManager2.doPickingImpl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.collection.Pair;

/**
 * simple version of a picking manager: one ID per PickingListener
 *
 * @author Samuel Gratzl
 *
 */
public class SimplePickingManager {
	private List<PickingEntry> mapping = new ArrayList<>();
	private int pickingIds = 0;
	private boolean anyHovered = false;

	private static class PickingEntry {
		private final IPickingListener listener;
		private final int pickingId;
		private final int objectId;

		private boolean hovered = false;
		private Point dragStart;

		public PickingEntry(int pickingId, IPickingListener listener, int objectId) {
			this.pickingId = pickingId;
			this.listener = listener;
			this.objectId = objectId;
		}

		public void fire(PickingMode mode, Point mouse, float depth) {
			if (mode == PickingMode.DRAGGED && dragStart == null)
				dragStart = mouse;
			if (mode == PickingMode.MOUSE_OUT)
				dragStart = null;
			final Pick pick = new Pick(objectId, mode, mouse, dragStart, depth);
			listener.pick(pick);
		}
	}

	/**
	 * registers a picking listener and returns a new pickingID to use
	 *
	 * @param l
	 * @param objectId
	 *            optional extra pick parameter
	 * @return
	 */
	public int register(IPickingListener l, int objectId) {
		int id = ++pickingIds;
		mapping.add(new PickingEntry(id, l, objectId));
		return id;
	}

	/**
	 * removes all instances of this {@link IPickingListener} object
	 *
	 * @param l
	 */
	public void unregister(IPickingListener l) {
		for (Iterator<PickingEntry> it = mapping.iterator(); it.hasNext();) {
			if (it.next().listener == l)
				it.remove();
		}
	}

	/**
	 * does the actual picking and calls the registered picking listener
	 *
	 * @param mode
	 * @param mousePos
	 * @param g
	 * @param root
	 */
	public void doPicking(PickingMode mode, Point mousePos, final GL2 gl, Runnable toRender) {
		BitSet picked = new BitSet();
		float depth = 0.0f;

		if (mode == null) // nothing changed
			return;

		if (mousePos != null) {
			Pair<int[], Float> tmp = doPickingImpl(mousePos.x, mousePos.y, gl, toRender);
			for (int pi : tmp.getFirst())
				picked.set(pi);
			depth = tmp.getSecond();
		}

		if (picked.isEmpty() && !(anyHovered || mode == PickingMode.MOUSE_OUT))
			return;

		anyHovered = false;
		for (PickingEntry entry : this.mapping) {
			if (picked.get(entry.pickingId)) { // currently picked
				if (!entry.hovered) {
					// send mouse in
					entry.fire(PickingMode.MOUSE_OVER, mousePos, depth);
				}
				entry.hovered = true;
				anyHovered = true;
				entry.fire(mode, mousePos, depth);

			} else if (entry.hovered) { // was picked last time
				// send mouse out
				entry.fire(PickingMode.MOUSE_OUT, mousePos, depth);
				entry.hovered = false;
			}
		}
	}
}
