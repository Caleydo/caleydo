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

import org.caleydo.vis.rank.layout.RowHeightLayouts;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.MaxCompositeRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;

/**
 * @author Samuel Gratzl
 *
 */
public class RankTableConfigBase implements IRankTableConfig {
	@Override
	public boolean isMoveAble(ARankColumnModel model) {
		return true;
	}

	@Override
	public ACompositeRankColumnModel createNewCombined() {
		return new MaxCompositeRankColumnModel(RowHeightLayouts.JUST_SELECTED);
	}

	@Override
	public boolean isCombineAble(ARankColumnModel model, ARankColumnModel with) {
		if (model instanceof ACompositeRankColumnModel && ((ACompositeRankColumnModel)model).canAdd(with))
			return true;
		if (!MaxCompositeRankColumnModel.canBeChild(model) || !MaxCompositeRankColumnModel.canBeChild(with))
			return false;
		return true;
	}

	@Override
	public String getCombineStringHint(ARankColumnModel model, ARankColumnModel with) {
		if (model instanceof StackedRankColumnModel)
			return "AND";
		return "OR";
	}

	@Override
	public boolean isDefaultCollapseAble() {
		return true;
	}

	@Override
	public boolean isDefaultHideAble() {
		return true;
	}

	@Override
	public boolean isInteractive() {
		return true;
	}

	@Override
	public boolean isDestroyOnHide() {
		return false;
	}

}
