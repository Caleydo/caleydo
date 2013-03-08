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
import static org.caleydo.vis.rank.ui.detail.ScoreBarRenderer.renderValue;

import java.awt.Color;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin.MultiFloat;
/**
 * @author Samuel Gratzl
 *
 */
public class MultiRenderer implements IGLRenderer {
	private final IMultiColumnMixin model;

	public MultiRenderer(IMultiColumnMixin model) {
		this.model = model;
	}


	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		final IRow r = parent.getLayoutDataAs(IRow.class, null);
		MultiFloat v = model.getSplittedValue(r);
		boolean inferred = model.isValueInferred(r);
		float vr = v.get();
		if (v.repr < 0)
			return;
		Color[] colors = model.getColors();
		renderValue(g, w, h, parent, r, vr, inferred, model, false, colors[v.repr], colors[v.repr]);

		if (model.getTable().getSelectedRow() == r && !getRenderInfo(parent).isCollapsed()) {
			for (int i = 0; i < v.size(); ++i) {
				if (i == v.repr)
					continue;
				float vi = v.values[i];
				g.color(Color.WHITE);
				g.fillRect(w * vi - 2, h * 0.3f, 5, (h - 1 - h * 0.3f));
				if (colors[i] == colors[v.repr])
					g.color(Color.DARK_GRAY);
				else
					g.color(colors[i]);
				g.drawLine(w * vi, 1 + h * 0.3f, w * vi, h - 1);
			}
		}
	}
}
