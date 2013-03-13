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

import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public abstract class AMultiRankColumnModel extends ACompositeRankColumnModel implements IMultiColumnMixin {
	private SimpleHistogram cacheHist = null;

	public AMultiRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor);
	}

	public AMultiRankColumnModel(ACompositeRankColumnModel copy) {
		super(copy);
	}

	@Override
	public final Float apply(IRow row) {
		return applyPrimitive(row);
	}

	@Override
	public final SimpleHistogram getHist(int bins) {
		if (cacheHist != null && cacheHist.size() == bins)
			return cacheHist;
		return cacheHist = DataUtils.getHist(bins, getMyRanker().iterator(), this);
	}

	@Override
	public final Color[] getColors() {
		Color[] colors = new Color[size()];
		int i = 0;
		for (ARankColumnModel child : this)
			colors[i++] = child.getColor();
		return colors;
	}

	@Override
	public void onRankingInvalid() {
		cacheHist = null;
		super.onRankingInvalid();
	}

	@Override
	public final boolean[] isValueInferreds(IRow row) {
		boolean[] r = new boolean[size()];
		int i = 0;
		for(IRankableColumnMixin child : Iterables.filter(this, IRankableColumnMixin.class))
			r[i++] = child.isValueInferred(row);
		return r;
	}

	@Override
	public final SimpleHistogram[] getHists(int bins) {
		SimpleHistogram[] hists = new SimpleHistogram[size()];
		int i = 0;
		for (IRankableColumnMixin child : Iterables.filter(this, IRankableColumnMixin.class))
			hists[i++] = child.getHist(bins);
		return hists;
	}

	@Override
	public ColumnRanker getMyRanker(ARankColumnModel model) {
		return getMyRanker();
	}
}