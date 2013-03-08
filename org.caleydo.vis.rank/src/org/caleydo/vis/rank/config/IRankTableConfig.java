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
package org.caleydo.vis.rank.config;

import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;

/**
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
	 * 
	 * @return
	 */
	ACompositeRankColumnModel createNewCombined(int combineMode);

	/**
	 * are these columsn combine able
	 *
	 * @param model
	 * @param with
	 * @param clone
	 * @return
	 */
	boolean isCombineAble(ARankColumnModel model, ARankColumnModel with, boolean clone);

	String getCombineStringHint(ARankColumnModel model, ARankColumnModel with, int combineMode);

	/**
	 * @return
	 */
	boolean isDefaultCollapseAble();

	/**
	 * @return
	 */
	boolean isDefaultHideAble();

	/**
	 * triggers that indead of hide a column it will be destroyed
	 *
	 * @return
	 */
	boolean isDestroyOnHide();

	/**
	 * @param combineMode
	 * @return
	 */
	Class<? extends ACompositeRankColumnModel> getCombineClassFor(int combineMode);

	/**
	 * @param t
	 * @param combineMode
	 * @return
	 */
	boolean canBeReusedForCombining(ACompositeRankColumnModel t, int combineMode);

}
