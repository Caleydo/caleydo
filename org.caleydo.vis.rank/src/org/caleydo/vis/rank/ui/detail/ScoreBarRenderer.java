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

import java.awt.Color;

import javax.media.opengl.GL2;

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
		float v = model.applyPrimitive(r);
		boolean inferred = model.isValueInferred(r);
		if (Float.isNaN(v) || v <= 0)
			return;
		renderValue(g, w, h, parent, r, v, inferred, model, false, model.getColor(), null);
	}

	static void renderValue(GLGraphics g, float w, float h, GLElement parent, final IRow r, float v, boolean inferred,
			IRankableColumnMixin model, boolean align, Color color, Color collapseColor) {
		if (getRenderInfo(parent).isCollapsed()) {
			// if collapsed use a brightness encoding
			if (collapseColor == null)
				g.color(1 - v, 1 - v, 1 - v, 1);
			else {
				float[] rgb = collapseColor.getColorComponents(null);
				g.color(rgb[0], rgb[1], rgb[2], v);
			}
			g.fillRect(w * 0.1f, h * 0.1f, w * 0.8f, h * 0.8f);
			if (inferred) {
				g.gl.glLineStipple(4, (short) 0xAAAA);
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
				g.color(0, 0, 0, .5f).drawRect(w * 0.1f + 1, h * 0.1f + 1, w * 0.8f - 2, h * 0.8f - 2);
				g.gl.glDisable(GL2.GL_LINE_STIPPLE);
			}
		} else {
			// score bar
			g.color(color).fillRect(0, h * 0.1f, w * v, h * 0.8f);
			if (inferred) {
				g.gl.glLineStipple(1, (short) 0xAAAA);
				g.gl.glEnable(GL2.GL_LINE_STIPPLE);
				g.color(0, 0, 0, .5f).drawRect(1, h * 0.1f + 1, w * v - 1, h * 0.8f - 2);
				g.gl.glDisable(GL2.GL_LINE_STIPPLE);
			}

			if (model.getTable().getSelectedRow() == r) { // is selected, render the value
				String text = (model instanceof IMappedColumnMixin) ? ((IMappedColumnMixin) model).getRawValue(r)
						: Formatter.formatNumber(v);
				float hi = getTextHeight(h);
				renderLabel(g, (h - hi) * 0.5f, w, hi, text, v, parent);
			}
		}
	}

	static float getTextHeight(float h) {
		float hi = Math.min(h * 0.45f, 12);
		return hi;
	}

	static IColumnRenderInfo getRenderInfo(GLElement parent) {
		return (IColumnRenderInfo) parent.getParent();
	}

	static void renderLabel(GLGraphics g, float y, float w, float h, String text, float v, GLElement parent) {
		if (h < 5)
			return;
		float tw = g.text.getTextWidth(text, h);
		boolean hasFreeSpace = getRenderInfo(parent).hasFreeSpace();

		VAlign alignment = getRenderInfo(parent).getAlignment();
		g.drawText(text, 2, y, (hasFreeSpace && alignment == VAlign.LEFT) ? w : (v * w) - 2, h, VAlign.LEFT);
	}
}