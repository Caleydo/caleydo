/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.config;

import java.util.Collections;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.picking.AdvancedPick;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.lineup.model.ACompositeRankColumnModel;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.GroupRankColumnModel;
import org.caleydo.vis.lineup.model.IRankColumnParent;
import org.caleydo.vis.lineup.model.MaxRankColumnModel;
import org.caleydo.vis.lineup.model.NestedRankColumnModel;
import org.caleydo.vis.lineup.model.OrderColumn;
import org.caleydo.vis.lineup.model.RankRankColumnModel;
import org.caleydo.vis.lineup.model.RankTableModel;
import org.caleydo.vis.lineup.model.ScriptedRankColumnModel;
import org.caleydo.vis.lineup.model.StackedRankColumnModel;
import org.caleydo.vis.lineup.model.mixin.IDoubleRankableColumnMixin;

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
		if (!(model instanceof IDoubleRankableColumnMixin) || !(with instanceof IDoubleRankableColumnMixin))
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
