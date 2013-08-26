/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.layout;

/**
 * an instance of a {@link IRowHeightLayout}
 *
 * @author Samuel Gratzl
 *
 */
public interface IRowLayoutInstance {
	/**
	 * @return whether this layout will require to render a scrollbar
	 */
	boolean needsScrollBar();

	/**
	 * @return the offset for the scrollbar
	 */
	int getOffset();

	/**
	 * @return the total number of rows
	 */
	int getSize();

	/**
	 * @return the current visible number of rows
	 */
	int getNumVisibles();

	/**
	 * performs layouting of a column
	 *
	 * @param setter
	 *            a setter object to set the bounds of a row
	 * @param x
	 *            the x offset to use
	 * @param w
	 *            the width of the column to use
	 */
	void layout(IRowSetter setter, float x, float w);

	/**
	 * generic callback interface for setting the bounds of a value item
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public interface IRowSetter {
		/**
		 * triggers to set the value of the given row index item with the specified bounds
		 *
		 * @param rowIndex
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 * @param pickable
		 *            whether this value item should be pickable, i.e. it is selected
		 */
		void set(int rowIndex, float x, float y, float w, float h, boolean pickable);
	}
}
