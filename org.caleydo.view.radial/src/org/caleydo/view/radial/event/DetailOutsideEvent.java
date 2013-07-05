/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial.event;

import org.caleydo.core.event.AEvent;

/**
 * This event signals that a detail view of the specified element shall be displayed in radial hierarchy.
 * 
 * @author Christian Partl
 */
public class DetailOutsideEvent
	extends AEvent {

	private int elementID;

	public DetailOutsideEvent() {
		elementID = -1;
	}

	@Override
	public boolean checkIntegrity() {
		if (elementID == -1)
			throw new IllegalStateException("iMaxDisplayedHierarchyDepth was not set");
		return true;
	}

	/**
	 * @return Element the detail view shall be displayed for.
	 */
	public int getElementID() {
		return elementID;
	}

	/**
	 * Sets the element the detail view shall be displayed for.
	 * 
	 * @param elementID
	 *            Element the detail view shall be displayed for.
	 */
	public void setElementID(int elementID) {
		this.elementID = elementID;
	}

}
