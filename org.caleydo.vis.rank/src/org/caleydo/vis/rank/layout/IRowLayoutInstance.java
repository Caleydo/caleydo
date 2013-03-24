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
package org.caleydo.vis.rank.layout;

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
