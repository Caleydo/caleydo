/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

/**
 * @author Samuel Gratzl
 *
 */
public class CellSpace {
	private final float position;
	private final float size;

	/**
	 * @param position
	 * @param size
	 */
	public CellSpace(float position, float size) {
		super();
		this.position = position;
		this.size = size;
	}

	/**
	 * @return the position, see {@link #position}
	 */
	public float getPosition() {
		return position;
	}

	/**
	 * @return the size, see {@link #size}
	 */
	public float getSize() {
		return size;
	}
}
