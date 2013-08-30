/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

/**
 * a thing that is a row
 *
 * @author Samuel Gratzl
 *
 */
public interface IRow {
	int getIndex();

	/**
	 * the data index of the row
	 *
	 * @param index
	 */
	void setIndex(int index);
}
