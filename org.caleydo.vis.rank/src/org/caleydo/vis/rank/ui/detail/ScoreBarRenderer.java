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

import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.ui.IColumnRenderInfo;

/**
 * a simple {@link IGLRenderer} for rendering a score bar
 *
 * @author Samuel Gratzl
 *
 */
public class ScoreBarRenderer implements IGLRenderer {
	private final IRankableColumnMixin model;

	public ScoreBarRenderer(IRankableColumnMixin model) {
		this.model = model;
	}

	@Override
	public void render(GLGraphics g, float w, float h, GLElement parent) {
		final IRow r = parent.getLayoutDataAs(IRow.class, null); // current row
		float v = model.getValue(r);
		if (Float.isNaN(v) || v <= 0)
			return;
		if (getRenderInfo(parent).isCollapsed()) {
			// if collapsed use a brightness encoding
			g.color(1 - v, 1 - v, 1 - v, 1).fillRect(w * 0.1f, h * 0.1f, w * 0.8f, h * 0.8f);
		} else {
			// score bar
			g.color(model.getColor()).fillRect(0, h * 0.1f, w * v, h * 0.8f);
			if (model.getTable().getSelectedRow() == r) { // is selected, render the value
				String text = (model instanceof IMappedColumnMixin) ? ((IMappedColumnMixin) model).getRawValue(r)
						: Formatter.formatNumber(v);
				renderLabel(g, h * 0.2f, w, h * 0.45f, text, v, parent);
			}
		}
	}

	private static IColumnRenderInfo getRenderInfo(GLElement parent) {
		return (IColumnRenderInfo) parent.getParent();
	}

	static void renderLabel(GLGraphics g, float y, float w, float h, String text, float v, GLElement parent) {
		if (h < 7)
			return;
		float tw = g.text.getTextWidth(text, h);
		boolean hasFreeSpace = getRenderInfo(parent).hasFreeSpace();

		if (tw < w * v)
			g.drawText(text, 1, y, w * v - 2, h, VAlign.RIGHT);
		else if (tw < w && hasFreeSpace) {
			VAlign alignment = getRenderInfo(parent).getAlignment();
			if (alignment == VAlign.LEFT)
				g.drawText(text, w * v + 1, y, w - w * v, h);
			else
				g.drawText(text, -w + w * v, y, w, h, VAlign.RIGHT);
		}
	}
}