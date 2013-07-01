/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model;


import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IHideableColumnMixin;
import org.caleydo.vis.rank.ui.detail.ColoredValueElement;
import org.caleydo.vis.rank.ui.detail.ValueElement;
/**
 * @author Samuel Gratzl
 *
 */
public class RankRankColumnModel extends ARankColumnModel implements IGLRenderer, ICollapseableColumnMixin,
		IHideableColumnMixin {
	public static final String PROP_SHOW_RANK_DELTA = "showRankDelta";

	private boolean showRankDelta = false;

	public RankRankColumnModel() {
		super(Color.GRAY, new Color(0.95f, .95f, .95f));
		setHeaderRenderer(GLRenderers.drawText("Rank", VAlign.CENTER));
		setWidth(45);
	}

	public RankRankColumnModel(RankRankColumnModel copy) {
		super(copy);
		setHeaderRenderer(getHeaderRenderer());
	}

	@Override
	public RankRankColumnModel clone() {
		return new RankRankColumnModel(this);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new GLElement(); // dummy
	}

	@Override
	public ValueElement createValue() {
		return (ValueElement) (new ColoredValueElement().setRenderer(this));
	}

	/**
	 * @param showRankDelta
	 *            setter, see {@link showRankDelta}
	 */
	public void setShowRankDelta(boolean showRankDelta) {
		this.showRankDelta = showRankDelta;
	}

	/**
	 * @return the showRankDelta, see {@link #showRankDelta}
	 */
	public boolean isShowRankDelta() {
		return showRankDelta;
	}

	@Override
	public String getValue(IRow row) {
		ColumnRanker ranker = getMyRanker();
		if (!ranker.hasDefinedRank())
			return "";
		int rank = ranker.getVisualRank(row);
		if (showRankDelta) {
			ColumnRanker previous = getTable().getPreviousRanker(ranker);
			if (previous != null) {
				int prank = previous.getVisualRank(row);
				int delta = prank - rank; // upwards = positive
				if (prank >= 0 && delta != 0) {
					return String.format("(%+d) %2d.", delta, rank);
				}
			}
		}
		return String.format("%2d.", rank);
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		if (h < 5 || w < 15)
			return;
		float hi = Math.min(h, 12);
		String value = getValue(parent.getLayoutDataAs(IRow.class, null));
		if (value.length() == 0)
			return;
		g.drawText(value, 1, (h - hi) * 0.5f, w - 10, hi, VAlign.RIGHT);
	}
}
