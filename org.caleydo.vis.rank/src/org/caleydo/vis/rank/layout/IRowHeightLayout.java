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

import org.caleydo.vis.rank.model.ColumnRanker;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * strategy pattern for different ways to layout rows of the {@link RankTableModel}
 *
 * @author Samuel Gratzl
 *
 */
public interface IRowHeightLayout {
	/**
	 * 
	 * @param ranker
	 *            the current relevant {@link ColumnRanker}
	 * @param h
	 *            the available height
	 * @param size
	 *            the total number of items
	 * @param offset
	 *            the offset determined by scrolling
	 * @param forceOffset
	 *            whether a shift in the offset is force, i.e. by scrolling
	 * @param previous
	 *            the previous instance
	 * @return a concrete instance of a {@link IRowLayoutInstance} that performs the actual layouting of rows
	 */
	IRowLayoutInstance layout(ColumnRanker ranker, float h, int size, int offset, boolean forceOffset,
			IRowLayoutInstance previous);

	/**
	 * @return a representative path to an icon of this layout
	 */
	String getIcon();
}
