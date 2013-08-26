/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.layout;

import org.caleydo.vis.lineup.model.ColumnRanker;
import org.caleydo.vis.lineup.model.RankTableModel;

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
