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
package org.caleydo.vis.rank.layout;

import java.util.Arrays;

import org.caleydo.vis.rank.layout.RowHeightLayouts.IRowHeightLayout;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
class FishEyeRowHeightLayout implements IRowHeightLayout {
	private static final float MAX_ROW_HEIGHT = 40;
	private static final float MIN_ROW_HEIGHT = 3;

	FishEyeRowHeightLayout() {
	}

	private static float rowHeightFor(int distance) {
		if (distance == 0)
			return MAX_ROW_HEIGHT;
		if (distance > 25)
			return MIN_ROW_HEIGHT;
		float ratio = distance / 25.f;
		float hi = 30 * (float) (1 - Math.sqrt(ratio));
		return MIN_ROW_HEIGHT + hi;
	}

	@Override
	public float[] compute(final int numRows, int selectedRowIndex, float h) {
		if (selectedRowIndex < 0) // default first
			selectedRowIndex = 0;

		float[] r = new float[numRows];
		float sum = 0;
		for (int i = 0; i < r.length; ++i) {
			int delta = Math.abs(i - selectedRowIndex);
			float hi = rowHeightFor(delta);
			r[i] = hi;
			sum += hi;
			if (sum >= h) {
				r = Arrays.copyOf(r, i);
				break;
			}
		}
		return r;
	}

	@Override
	public String getIcon() {
		return RenderStyle.ICON_ALIGN_FISH;
	}
}

