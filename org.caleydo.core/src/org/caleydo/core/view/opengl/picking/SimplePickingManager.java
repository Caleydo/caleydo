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


/**
 * simple version of a picking manager: one ID per PickingListener
 *
 * @author Samuel Gratzl
 *
 */
public class SimplePickingManager extends APickingManager<SimplePickingManager.PickingEntry> {

	protected static class PickingEntry extends APickingEntry {
		private final IPickingListener listener;

		public PickingEntry(IPickingListener listener, int objectId) {
			super(objectId);
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
		return add(new PickingEntry(l, objectId));
	}

	/**
	 * unregister and free a given picking Id
	 *
	 * @param pickingId
	 */
	public void unregister(int pickingId) {
		remove(get(pickingId));
	}
}
