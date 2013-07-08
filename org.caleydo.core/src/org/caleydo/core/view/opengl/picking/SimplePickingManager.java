/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
