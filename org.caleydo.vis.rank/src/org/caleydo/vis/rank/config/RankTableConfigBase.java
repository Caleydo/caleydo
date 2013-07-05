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

import java.util.Collections;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.model.ACompositeRankColumnModel;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.GroupRankColumnModel;
import org.caleydo.vis.rank.model.IRankColumnParent;
import org.caleydo.vis.rank.model.MaxRankColumnModel;
import org.caleydo.vis.rank.model.NestedRankColumnModel;
import org.caleydo.vis.rank.model.OrderColumn;
import org.caleydo.vis.rank.model.RankRankColumnModel;
import org.caleydo.vis.rank.model.RankTableModel;
import org.caleydo.vis.rank.model.ScriptedRankColumnModel;
import org.caleydo.vis.rank.model.StackedRankColumnModel;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;

/**
 * basic implementation of {@link IRankTableConfig}
 *
 * with all features enabled and combine by default to a {@link MaxRankColumnModel} and with ALT down to a
 * {@link StackedRankColumnModel}
 *
 * @author Samuel Gratzl
 *
 */
public class RankTableConfigBase implements IRankTableConfig {
	public static final int MAX_MODE = 0;
	public static final int SUM_MODE = 1;
	public static final int NESTED_MODE = 2;
	public static final int SCRIPTED_MODE = 3;
	public static final int GROUP_MODE = 4;

	@Override
	public boolean isMoveAble(ARankColumnModel model, boolean clone) {
		return true;
	}

	@Override
	public int getCombineMode(ARankColumnModel model, Pick pick) {
		int default_ = defaultMode(model);
		if (!(pick instanceof AdvancedPick))
			return default_;
		AdvancedPick apick = (AdvancedPick) pick;
		if (apick.isAltDown())
			return SUM_MODE; // opposite one
		if (apick.isShiftDown())
			return NESTED_MODE;
		return default_;
	}

	/**
	 * @param model
	 * @return
	 */
	private int defaultMode(ARankColumnModel model) {
		if (model instanceof StackedRankColumnModel)
			return SUM_MODE;
		if (model instanceof MaxRankColumnModel)
			return MAX_MODE;
		if (model instanceof NestedRankColumnModel)
			return NESTED_MODE;
		if (model instanceof ScriptedRankColumnModel)
			return SCRIPTED_MODE;
		if (model instanceof GroupRankColumnModel)
			return GROUP_MODE;
		return MAX_MODE;
	}

	@Override
	public ACompositeRankColumnModel createNewCombined(int combineMode) {
		switch (combineMode) {
		case SUM_MODE:
			return new StackedRankColumnModel();
		case NESTED_MODE:
			return new NestedRankColumnModel();
		case MAX_MODE:
			return new MaxRankColumnModel();
		case GROUP_MODE:
			return new GroupRankColumnModel("Group", Color.GRAY, new Color(0.95f, .95f, .95f));
		case SCRIPTED_MODE:
			return new ScriptedRankColumnModel();
		default:
			return new MaxRankColumnModel();
		}
	}

	@Override
	public Iterable<? extends ARankColumnModel> createAutoSnapshotColumns(RankTableModel table, ARankColumnModel model) {
		return Collections.singleton(new RankRankColumnModel());
	}

	@Override
	public boolean canBeReusedForCombining(ACompositeRankColumnModel t, int combineMode) {
		if (t instanceof GroupRankColumnModel)
			return true;
		switch (combineMode) {
		case SUM_MODE:
			return t instanceof StackedRankColumnModel;
		case NESTED_MODE:
			return t instanceof NestedRankColumnModel;
		case GROUP_MODE:
			return t instanceof GroupRankColumnModel;
		case SCRIPTED_MODE:
			return t instanceof ScriptedRankColumnModel;
		case MAX_MODE:
		default:
			return t instanceof MaxRankColumnModel;
		}
	}

	@Override
	public boolean isCombineAble(ARankColumnModel model, ARankColumnModel with, boolean clone, int combineMode) {
		if (model instanceof ACompositeRankColumnModel && ((ACompositeRankColumnModel)model).canAdd(with))
			return true;
		if (!(model instanceof IFloatRankableColumnMixin) || !(with instanceof IFloatRankableColumnMixin))
			return false;
		IRankColumnParent parent = model.getParent();
		switch (combineMode) {
		case SUM_MODE:
			return parent.getClass() != StackedRankColumnModel.class;
		case NESTED_MODE:
			return parent.getClass() != NestedRankColumnModel.class;
		case GROUP_MODE:
			return parent.getClass() != GroupRankColumnModel.class;
		case SCRIPTED_MODE:
			return parent.getClass() != ScriptedRankColumnModel.class;
		case MAX_MODE:
		default:
			return parent.getClass() != MaxRankColumnModel.class;
		}
	}

	@Override
	public String getCombineStringHint(ARankColumnModel model, ARankColumnModel with, int combineMode) {
		switch (combineMode) {
		case SUM_MODE:
			return "SUM";
		case NESTED_MODE:
			return "NESTED";
		case SCRIPTED_MODE:
			return "CODE";
		case GROUP_MODE:
			return "GROUP";
		case MAX_MODE:
		default:
			return "MAX";
		}
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
	public boolean isDestroyOnHide(ARankColumnModel model) {
		return model instanceof OrderColumn;
	}

}
