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
class UniformRowHeightLayout implements IRowHeightLayout {
	private static final float ROW_HEIGHT = 20;

	UniformRowHeightLayout() {
	}


	@Override
	public float[] compute(int numRows, int selectedRowIndex, float h) {
		h -= ROW_HEIGHT;
		int visibleRows = (int) Math.round(Math.floor(h / ROW_HEIGHT));
		float[] r;
		r = new float[Math.min(numRows, visibleRows + 1)];
		Arrays.fill(r, ROW_HEIGHT);
		return r;
	}

	@Override
	public String getIcon() {
		return RenderStyle.ICON_ALIGN_UNIFORM;
	}
}
