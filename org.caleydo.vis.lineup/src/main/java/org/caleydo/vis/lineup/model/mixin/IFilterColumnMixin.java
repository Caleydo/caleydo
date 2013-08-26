/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mixin;

import java.util.BitSet;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.vis.lineup.model.IRow;

/**
 * contract that the column can have filters
 *
 * @author Samuel Gratzl
 *
 */
public interface IFilterColumnMixin extends IRankColumnModel {
	String PROP_FILTER = "filter";

	boolean isFiltered();

	/**
	 * whether the filter should be applied over all snapshots or just for the current one
	 *
	 * @return
	 */
	boolean isGlobalFilter();

	/**
	 * whether the filter should influence the ranking or just the visible presented rows
	 *
	 * @return
	 */
	boolean isRankIndependentFilter();

	/**
	 * performs filtering on the given data and updates the given mask
	 *
	 * @param data
	 * @param mask
	 * @param maskRankIndependent
	 */
	void filter(List<IRow> data, BitSet mask, BitSet mask_filteredOutInfluenceRanking);

	/**
	 * triggers the edit dialog of this column given the summary element
	 *
	 * @param summary
	 * @param context
	 */
	void editFilter(GLElement summary, IGLElementContext context);
}
