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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.util.IntegerPool;

/**
 * simple version of a picking manager: one ID per PickingListener
 *
 * @author Samuel Gratzl
 *
 */
public class SimplePickingManager {
	private List<PickingEntry> mapping = new ArrayList<>();
	private final IntegerPool pool = new IntegerPool();

	private boolean anyWaiting = false;

	private static class PickingEntry extends APickingEntry {
		private final IPickingListener listener;

		public PickingEntry(int pickingId, IPickingListener listener, int objectId) {
			super(pickingId, objectId);
			this.listener = listener;
		}

		@Override
		protected void fire(Pick pick) {
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
		int id = pool.checkOut();
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
			PickingEntry entry = it.next();
			if (entry.listener == l) {
				pool.checkIn(entry.pickingId);
				it.remove();
			}
		}
	}

	/**
	 * unregister and free a given picking Id
	 *
	 * @param pickingId
	 */
	public void unregister(int pickingId) {
		for (Iterator<PickingEntry> it = mapping.iterator(); it.hasNext();) {
			if (it.next().pickingId == pickingId) {
				pool.checkIn(pickingId);
				it.remove();
				break;
			}
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
	public void doPicking(PickingMouseListener l, final GL2 gl, Runnable toRender) {
		anyWaiting = PickingUtils.doPicking(l, gl, toRender, anyWaiting, this.mapping);
	}
}
