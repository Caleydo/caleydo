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

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMultiColumnMixin.MultiFloat;
/**
 * @author Samuel Gratzl
 *
 */
public class MultiScoreBarElement extends ScoreBarElement {
	public MultiScoreBarElement(IMultiColumnMixin model) {
		super(model);
	}

	@Override
	protected String getTooltip() {
		final IRow r = getLayoutDataAs(IRow.class, null); // current row
		IMultiColumnMixin mmodel = (IMultiColumnMixin) model;
		MultiFloat v = mmodel.getSplittedValue(r);
		if (v.repr < 0 || Float.isNaN(v.get()) || v.get() < 0)
			return null;
		boolean[] inferreds = mmodel.isValueInferreds(r);
		int i = 0;
		StringBuilder b = new StringBuilder();

		for (ARankColumnModel child : mmodel) {
			b.append(child.getTooltip()).append(": ").append(getText(r, child, v.values[i], inferreds[i]));
			if (i == v.repr)
				b.append(" MAX");
			b.append("\n");
			i++;
		}
		b.setLength(b.length() - 1);
		return b.toString();
	}

	@Override
	public void renderImpl(GLGraphics g, float w, float h) {
		final IRow r = getLayoutDataAs(IRow.class, null);
		IMultiColumnMixin mmodel = (IMultiColumnMixin) model;
		MultiFloat v = mmodel.getSplittedValue(r);
		boolean inferred = mmodel.isValueInferred(r);
		float vr = v.get();
		if (v.repr < 0)
			return;
		Color[] colors = mmodel.getColors();
		renderValue(g, w, h, r, vr, inferred, false, colors[v.repr], colors[v.repr]);

		if (model.getTable().getSelectedRow() == r && !getRenderInfo().isCollapsed()) {
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

	@Override
	protected void renderText(GLGraphics g, float w, float h, final IRow r, float v, boolean inferred) {
		IMultiColumnMixin mmodel = (IMultiColumnMixin) model;
		MultiFloat mv = mmodel.getSplittedValue(r);
		String text = getText(r, mmodel.get(mv.repr), mv.get(), inferred);
		float hi = getTextHeight(h);
		renderLabel(g, (h - hi) * 0.5f, w, hi, text, v);
	}
}
