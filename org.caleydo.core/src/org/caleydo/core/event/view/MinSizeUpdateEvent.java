/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.event.view;

import org.caleydo.core.event.AEvent;

/**
 * Event that signals that the minimum size of an object has changed.
 *
 * @author Christian Partl
 *
 */
public class MinSizeUpdateEvent extends AEvent {

	protected int minHeight;
	protected int minWidth;
	protected Object minSizeObject;

	public MinSizeUpdateEvent(Object minSizeObject) {
		this.minSizeObject = minSizeObject;
	}

	public MinSizeUpdateEvent(Object minSizeObject, int minWidth, int minHeight) {
		this(minSizeObject);
		this.minHeight = minHeight;
		this.minWidth = minWidth;
	}

	@Override
	public boolean checkIntegrity() {
		return minSizeObject != null;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public void setMinViewSize(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	/**
	 * @param minSizeObject
	 *            setter, see {@link minSizeObject}
	 */
	public void setMinSizeObject(Object minSizeObject) {
		this.minSizeObject = minSizeObject;
	}

	/**
	 * @return the minSizeObject, see {@link #minSizeObject}
	 */
	public Object getMinSizeObject() {
		return minSizeObject;
	}

}
