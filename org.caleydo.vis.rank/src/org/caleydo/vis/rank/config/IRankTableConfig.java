/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.config;

import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;

/**
 * config interface for describing the behavior of a {@link RankTableModel}
 *
 * @author Samuel Gratzl
 *
 */
public interface IRankTableConfig {

	/**
	 * is this column moveable
	 *
	 * @param model
	 * @param clone
	 * @return
	 */
	boolean isMoveAble(ARankColumnModel model, boolean clone);

	/**
	 * creates a new combined column caused by dragging two columns onto each other
	 *
	 * @param combineMode
	 *            the mode identified by {@link #getCombineMode(ARankColumnModel, Pick)}
	 *
	 * @return
	 */
	ACompositeRankColumnModel createNewCombined(int combineMode);

	/**
	 * are these columns combine able
	 *
	 * @param model
	 * @param with
	 * @param clone
	 *            should the with column be cloned
	 * @param combineMode
	 *            as determined by {@link #getCombineMode(ARankColumnModel, Pick)}
	 * @return
	 */
	boolean isCombineAble(ARankColumnModel model, ARankColumnModel with, boolean clone, int combineMode);

	/**
	 * returns a string hint that will be shown to indicate the type of combination
	 *
	 * @param model
	 * @param with
	 * @param combineMode
	 *            as determined by {@link #getCombineMode(ARankColumnModel, Pick)}
	 * @return
	 */
	String getCombineStringHint(ARankColumnModel model, ARankColumnModel with, int combineMode);

	/**
	 * @return whether by default a column can be collapsed
	 */
	boolean isDefaultCollapseAble();

	/**
	 * @return whether by default a column can be hidded
	 */
	boolean isDefaultHideAble();

	/**
	 * @return whether a column should be removed instead of hided
	 */
	boolean isDestroyOnHide(ARankColumnModel model);

	/**
	 * checks whether the given {@link ACompositeRankColumnModel} can be reused for creating a new combiner with the
	 * given mode
	 *
	 * @param t
	 * @param combineMode
	 *            as determined via {@link #getCombineMode(ARankColumnModel, Pick)}
	 * @return
	 */
	boolean canBeReusedForCombining(ACompositeRankColumnModel t, int combineMode);

	/**
	 * determines the mode as used by the other combine methods depending on the current pick state,
	 *
	 * e.g. to trigger a different kind of combination if a special key is pressed during dragging
	 *
	 * @param model
	 * @param pick
	 * @return
	 */
	int getCombineMode(ARankColumnModel model, Pick pick);

	/**
	 * which columns should be automatically created during a new snapshot
	 * 
	 * @return
	 */
	Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model);
}
