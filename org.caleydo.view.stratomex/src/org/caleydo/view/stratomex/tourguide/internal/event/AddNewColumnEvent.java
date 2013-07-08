/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.tourguide.internal.event;

import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Samuel Gratzl
 *
 */
public class AddNewColumnEvent extends ADirectedEvent {

	private int objectId;
	private boolean attached;

	/**
	 * @param objectID
	 */
	public AddNewColumnEvent(int objectID) {
		this(objectID, false);
	}

	/**
	 * @param objectID2
	 * @param b
	 */
	public AddNewColumnEvent(int objectID, boolean attached) {
		this.objectId = objectID;
		this.attached = attached;
	}

	/**
	 * @return the dependentOne, see {@link #dependentOne}
	 */
	public boolean isIndependentOne() {
		return attached && objectId % 2 == 1;
	}

	public boolean isDependentOne() {
		return attached && objectId % 2 == 0;
	}

	/**
	 * @return the objectId, see {@link #objectId}
	 */
	public int getObjectId() {
		return attached ? objectId / 2 : objectId;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

}
