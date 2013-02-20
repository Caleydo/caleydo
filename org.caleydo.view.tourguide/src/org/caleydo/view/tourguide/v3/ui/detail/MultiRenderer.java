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
package org.caleydo.view.tourguide.v3.ui.detail;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.tourguide.v3.layout.RowHeightLayouts.IRowHeightLayout;
import org.caleydo.view.tourguide.v3.model.IRow;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin;
import org.caleydo.view.tourguide.v3.model.mixin.IMultiColumnMixin.MultiFloat;

/**
 * @author Samuel Gratzl
 *
 */
public class MultiRenderer implements IGLRenderer {
	private final IMultiColumnMixin model;
	private final IRowHeightLayout layout;

	public MultiRenderer(IMultiColumnMixin model, IRowHeightLayout layout) {
		this.model = model;
		this.layout = layout;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		MultiFloat v = model.getSplittedValue(parent.getLayoutDataAs(IRow.class, null));
		Color[] colors = model.getColors();
		if (v.repr >= 0) {
			float[] heights = layout.compute(v.size(), v.repr, h * 0.9f);
			float y = h * 0.05f;
			for (int i = 0; i < heights.length; ++i) {
				float hi = heights[i];
				if (hi <= 0)
					continue;
				g.color(colors[i]).fillRect(0, y, w * v.values[i], hi);
				y += hi;
			}
		}
	}
}
