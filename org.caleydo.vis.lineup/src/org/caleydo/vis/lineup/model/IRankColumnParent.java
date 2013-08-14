/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;

import org.caleydo.vis.lineup.model.mixin.IRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;


/**
 * @author Samuel Gratzl
 *
 */
public interface IRankColumnParent extends IRankColumnModel {

	ColumnRanker getMyRanker(IRankColumnModel model);
	/**
	 * @param aBasicRankColumnModel
	 * @return
	 */
	boolean hide(ARankColumnModel model);

	/**
	 * @param aBasicRankColumnModel
	 * @return
	 */
	boolean isHideAble(ARankColumnModel model);

	/**
	 * @param aBasicRankColumnModel
	 * @return
	 */
	boolean isDestroyAble(ARankColumnModel model);

	/**
	 * @param maxCompositeRankColumnModel
	 */
	void explode(ACompositeRankColumnModel model);

	int indexOf(ARankColumnModel model);

	/**
	 * @param with
	 */
	void remove(ARankColumnModel model);

	/**
	 * @param model
	 * @param w
	 */
	void replace(ARankColumnModel from, ARankColumnModel to);

	/**
	 * @param aBasicRankColumnModel
	 * @return
	 */
	boolean isCollapseAble(ARankColumnModel model);

	boolean isMoveAble(ARankColumnModel model, int index, boolean clone);

	void move(ARankColumnModel model, int index, boolean clone);

	/**
	 * @param aRankColumnModel
	 * @return
	 */
	boolean isHidden(ARankColumnModel model);

	/**
	 * @param aRankColumnModel
	 */
	void orderBy(IRankableColumnMixin model);

}
