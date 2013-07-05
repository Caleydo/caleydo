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
