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
package org.caleydo.vis.rank.ui.detail;

import static org.caleydo.vis.rank.ui.detail.ScoreBarRenderer.getRenderInfo;
import static org.caleydo.vis.rank.ui.detail.ScoreBarRenderer.getTextHeight;

import java.awt.Color;

import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.layout.RowHeightLayouts.IRowHeightLayout;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin.MultiFloat;
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

	/**
	 * @return the layout, see {@link #layout}
	 */
	public IRowHeightLayout getLayout() {
		return layout;
	}


	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		final IRow r = parent.getLayoutDataAs(IRow.class, null);
		MultiFloat v = model.getSplittedValue(r);
		if (v.repr < 0)
			return;
		if (getRenderInfo(parent).isCollapsed()) {
			g.color(1-v.get(),1-v.get(),1-v.get(),1);
			g.fillRect(w * 0.1f, h * 0.1f, w * 0.8f, h * 0.8f);
			return;
		}
		// boolean inferred = model.isValueInferred(r);
		// TODO inferred vis
		Color[] colors = model.getColors();
		if (v.repr >= 0) {
			boolean selected = model.getTable().getSelectedRow() == r;
			float[] heights = layout.compute(v.size(), v.repr, h * 0.8f);
			float y = h * 0.1f;
			for (int i = 0; i < heights.length; ++i) {
				if (v.values[i] <= 0 || Float.isNaN(v.values[i]))
					continue;
				float hi = heights[i];
				if (hi <= 0)
					continue;
				g.color(colors[i]).fillRect(0, y, w * v.values[i], hi);
				if (selected) {
					ARankColumnModel modeli = model.get(i);
					String text = (modeli instanceof IMappedColumnMixin) ? ((IMappedColumnMixin) modeli).getRawValue(r)
								: Formatter.formatNumber(v.values[i]);
					float hli = getTextHeight(hi);
					ScoreBarRenderer.renderLabel(g, y + (hi - hli) * 0.5f, w, hli, text, v.values[i], parent);
				}
				y += hi;
			}
		}
	}
}
