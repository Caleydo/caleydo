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
package org.caleydo.vis.rank.model;

import org.caleydo.vis.rank.model.mixin.IRankColumnModel;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;


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
