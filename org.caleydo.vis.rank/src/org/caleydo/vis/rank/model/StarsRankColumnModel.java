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

import java.awt.Color;
import java.util.BitSet;
import java.util.List;

import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.detail.ScoreSummary;
import org.caleydo.vis.rank.ui.detail.StarsValueElement;
import org.caleydo.vis.rank.ui.detail.ValueElement;

/**
 * @author Samuel Gratzl
 *
 */
public class StarsRankColumnModel extends ABasicFilterableRankColumnModel implements IRankableColumnMixin {
	private final int stars;
	private final IFloatFunction<IRow> data;

	private SimpleHistogram cacheHist = null;

	public StarsRankColumnModel(IFloatFunction<IRow> data, IGLRenderer header, Color color, Color bgColor, int stars) {
		super(color, bgColor);
		this.stars = stars;
		this.data = data;
		setHeaderRenderer(header);
	}

	public StarsRankColumnModel(StarsRankColumnModel copy) {
		super(copy);
		this.stars = copy.stars;
		this.data = copy.data;
		this.cacheHist = copy.cacheHist;
		setHeaderRenderer(copy.getHeaderRenderer());
	}

	@Override
	public StarsRankColumnModel clone() {
		return new StarsRankColumnModel(this);
	}

	/**
	 * @return the stars, see {@link #stars}
	 */
	public int getStars() {
		return stars;
	}

	@Override
	public void onRankingInvalid() {
		cacheHist = null;
		super.onRankingInvalid();
	}

	@Override
	public Float apply(IRow row) {
		return applyPrimitive(row);
	}

	protected float map(float value, boolean handleNaNs) {
		if (Float.isNaN(value) && handleNaNs)
			return 0;
		return value;
	}

	public float getRaw(IRow in) {
		return data.applyPrimitive(in);
	}

	@Override
	public float applyPrimitive(IRow in) {
		float v = map(data.applyPrimitive(in), true);
		v /= stars;
		return FloatFunctions.CLAMP01.apply(v);
	}

	@Override
	public boolean isFiltered() {
		return false;
	}

	@Override
	protected void updateMask(BitSet todo, List<IRow> data, BitSet mask) {
		// TODO Auto-generated method stub

	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new ScoreSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new StarsValueElement(this);
	}

	@Override
	public void editFilter(GLElement summary, IGLElementContext context) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isValueInferred(IRow row) {
		float v = data.applyPrimitive(row);
		return Float.isNaN(v);
	}

	@Override
	public SimpleHistogram getHist(int bins) {
		if (cacheHist != null && cacheHist.size() == bins)
			return cacheHist;
		return cacheHist = DataUtils.getHist(bins, getMyRanker().iterator(), new AFloatFunction<IRow>() {
			@Override
			public float applyPrimitive(IRow in) {
				return map(data.applyPrimitive(in), false);
			}
		});
	}
}
