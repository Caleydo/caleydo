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
package org.caleydo.view.tourguide.v3.model;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.v3.layout.RowHeightLayouts.IRowHeightLayout;
import org.caleydo.view.tourguide.v3.model.mixin.IExplodeableColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IHideableColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IRankableColumnMixin;
import org.caleydo.view.tourguide.v3.ui.detail.MultiRenderer;
import org.caleydo.view.tourguide.v3.ui.detail.StackedScoreSummary;

/**
 * @author Samuel Gratzl
 *
 */
public class MaxCompositeRankColumnModel extends ACompositeRankColumnModel implements IRankableColumnMixin,
		IHideableColumnMixin, IExplodeableColumnMixin, IMultiColumnMixin {

	public MaxCompositeRankColumnModel(IRowHeightLayout layout) {
		this(GLRenderers.drawText("Multiple", VAlign.CENTER), layout);
	}

	public MaxCompositeRankColumnModel(IGLRenderer renderer, IRowHeightLayout layout) {
		super(Color.GRAY, new Color(0.95f, 0.95f, 0.95f));
		setHeaderRenderer(renderer);
		setValueRenderer(new MultiRenderer(this, layout));
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new StackedScoreSummary(this);
	}


	@Override
	protected boolean canAdd(ARankColumnModel model) {
		return model instanceof IRankableColumnMixin && !(model instanceof ACompositeRankColumnModel);
	}

	@Override
	public void explode() {
		parent.explode(this);
	}

	@Override
	public MultiFloat getSplittedValue(IRow row) {
		if (children.isEmpty())
			return new MultiFloat(-1);
		float max = Float.NEGATIVE_INFINITY;
		int maxIndex = -1;
		float[] vs = new float[size()];
		int i = 0;
		for (ARankColumnModel col : this) {
			float v = ((IRankableColumnMixin) col).getValue(row);
			vs[i++] = v;
			if (v > max) {
				maxIndex = i - 1;
				max = v;
			}
		}
		return new MultiFloat(maxIndex, vs);
	}

	@Override
	public float getValue(IRow row) {
		if (children.isEmpty())
			return 0;
		float max = Float.NEGATIVE_INFINITY;
		for (ARankColumnModel col : this) {
			float v = ((IRankableColumnMixin) col).getValue(row);
			max = Math.max(max, v);
		}
		if (max > 1) {
			System.err.println();
		}
		return max;
	}

	/**
	 * @param a
	 * @return
	 */
	public static boolean canBeChild(ARankColumnModel model) {
		return model instanceof IRankableColumnMixin;
	}

	@Override
	public boolean isHideAble() {
		return parent.isHideAble(this);
	}

	@Override
	public boolean hide() {
		return parent.hide(this);
	}

	@Override
	public boolean isDestroyAble() {
		return parent.isDestroyAble(this);
	}

	@Override
	public boolean destroy() {
		if (isDestroyAble())
			return getTable().destroy(this);
		return false;
	}
}
