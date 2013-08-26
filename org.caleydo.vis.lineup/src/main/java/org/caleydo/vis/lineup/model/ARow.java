/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

/**
 * @author Samuel Gratzl
 *
 */
public class ARow implements IRow {
	private int index = 0;

	/**
	 * @return the index, see {@link #index}
	 */
	@Override
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            setter, see {@link index}
	 */
	@Override
	public void setIndex(int index) {
		this.index = index;
	}
}
