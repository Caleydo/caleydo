/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui.detail;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.lineup.model.ARankColumnModel;
import org.caleydo.vis.lineup.model.IRow;
import org.caleydo.vis.lineup.model.mixin.IMultiColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IMultiColumnMixin.MultiDouble;
/**
 * @author Samuel Gratzl
 *
 */
public class MultiScoreBarElement extends ScoreBarElement {
	public MultiScoreBarElement(IMultiColumnMixin model) {
		super(model);
	}

	@Override
	public String getTooltip() {
		final IRow r = getLayoutDataAs(IRow.class, null); // current row
		IMultiColumnMixin mmodel = (IMultiColumnMixin) model;
		MultiDouble v = mmodel.getSplittedValue(r);
		if (v.repr < 0 || Double.isNaN(v.get()) || v.get() < 0)
			return null;
		boolean[] inferreds = mmodel.isValueInferreds(r);
		int i = 0;
		StringBuilder b = new StringBuilder();

		for (ARankColumnModel child : mmodel) {
			b.append(child.getLabel()).append(": ").append(getText(r, child, v.values[i], inferreds[i]));
			if (i == v.repr)
				b.append(" MAX");
			b.append("\n");
			i++;
		}
		b.setLength(b.length() - 1);
		return b.toString();
	}

	@Override
	public void renderImpl(GLGraphics g, float w, float h, IRow row) {
		final IRow r = row;
		IMultiColumnMixin mmodel = (IMultiColumnMixin) model;
		MultiDouble v = mmodel.getSplittedValue(r);
		if (v.repr < 0)
			return;
		boolean inferred = mmodel.isValueInferred(r);
		double vr = v.get();
		Color[] colors = mmodel.getColors();
		renderValue(g, w, h, r, vr, inferred, false, colors[v.repr], colors[v.repr]);

		if (model.getTable().getSelectedRow() == r && !getRenderInfo().isCollapsed()) {
			List<Pair.ComparablePair<Double, Integer>> tmp = new ArrayList<>(v.size());
			for (int i = 0; i < v.size(); ++i) {
				if (i == v.repr)
					continue;
				tmp.add(Pair.make(v.values[i], i));
			}
			Collections.sort(tmp, Collections.reverseOrder());

			g.color(1, 1, 1, 0.8f).fillRect(0, 1, w * (float) vr, h * 0.3f);
			for (Pair.ComparablePair<Double, Integer> pair : tmp) {
				int i = pair.getSecond();
				double vi = pair.getFirst() * w;
				if (colors[i] == colors[v.repr])
					g.color(Color.DARK_GRAY);
				else
					g.color(colors[i]);
				g.fillRect(0, 1, (float) vi, h * 0.2f);
				g.fillRect(Math.max((float) vi - 2, 0), 1, 3, h * 0.3f);
			}
		}
	}

	@Override
	protected void renderText(GLGraphics g, float w, float h, final IRow r, double v, boolean inferred) {
		IMultiColumnMixin mmodel = (IMultiColumnMixin) model;
		MultiDouble mv = mmodel.getSplittedValue(r);
		String text = getText(r, mmodel.get(mv.repr), mv.get(), inferred);
		float hi = getTextHeight(h);
		renderLabel(g, (h - hi) * 0.5f, w, hi, text, v);
	}
}
