/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.event;

import org.caleydo.core.event.AEvent;

/**
 * Event signalling that a layout can not be rendered within its borders.
 * 
 * @author Alexander Lex
 */
public class LayoutSizeCollisionEvent
	extends AEvent {

	private int managingClassID = -1;

	private int layoutID = -1;

	private float toBigBy = Float.NaN;

	/**
	 * Set the uniqueID of the managing class. Used for notifications on collisions via event and an id to
	 * identify the layout.
	 */
	public void tableIDs(int managingClassID, int layoutID) {
		this.managingClassID = managingClassID;
		this.layoutID = layoutID;
	}

	/**
	 * Set how much the layout is to big by
	 * 
	 * @param toBigBy
	 */
	public void setToBigBy(float toBigBy) {
		this.toBigBy = toBigBy;
	}

	/** Get the id of the class managing the layout */
	public int getManagingClassID() {
		return managingClassID;
	}

	public int getLayoutID() {
		return layoutID;
	}

	/** Get how much this thing is to big by */
	public float getToBigBy() {
		return toBigBy;
	}

	@Override
	public boolean checkIntegrity() {
		if (managingClassID == -1 || layoutID == -1 || Float.isNaN(toBigBy))
			return false;
		return true;
	}

}
