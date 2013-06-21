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
package org.caleydo.vis.rank.ui;


import java.util.Map;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.caleydo.vis.rank.model.SimpleHistogram;

/**
 * @author Samuel Gratzl
 *
 */
public class RenderUtils {
	public static void renderHist(GLGraphics g, SimpleHistogram hist, float w, float h, int selectedBin, Color color,
			Color selectionColor, Color outlineColor) {
		w -= 2;
		float factor = (h - 2) / hist.getLargestValue(false);
		float delta = w / hist.size();
		g.save();

		final float lineWidth = Math.min(delta - 1, 25);
		final float lineWidthHalf = lineWidth * 0.5f;
		float x = 1 + delta / 2;
		g.move(0, h - 1);
		for (int i = 0; i < hist.size(); ++i) {
			if (selectedBin == i) {
				g.color(selectionColor);
			} else
				g.color(color);
			float v = -hist.get(i) * factor;

			if (v <= -1) {
				g.fillRect(x - lineWidthHalf, 0, lineWidth, v);
				if (outlineColor != null)
					g.color(outlineColor).drawRect(x - lineWidthHalf, 0, lineWidth, v);
			}
			x += delta;
		}
		g.restore();
	}

	public static <CATEGORY_TYPE> void renderHist(GLGraphics g, Map<CATEGORY_TYPE, Integer> hist, float w, float h,
			CATEGORY_TYPE selected, Map<CATEGORY_TYPE, CategoryInfo> metaData, Color outlineColor) {
		w -= 2;
		int largest = 0;
		for (Integer v : hist.values())
			largest = Math.max(v, largest);

		float factor = h / largest;
		float delta = w / metaData.size();

		final float lineWidth = Math.min(delta - 1, 25);
		final float lineWidthHalf = lineWidth * 0.5f;

		g.save();
		float x = 1 + delta / 2;
		g.move(0, h - 1);
		for (Map.Entry<CATEGORY_TYPE, CategoryInfo> entry : metaData.entrySet()) {
			if (entry.getKey() == selected)
				g.color(Color.GRAY);
			else
				g.color(entry.getValue().getColor());
			float v = -(hist.containsKey(entry.getKey()) ? hist.get(entry.getKey()) : 0) * factor;
			if (v <= -1) {
				g.fillRect(x - lineWidthHalf, 0, lineWidth, v);
				if (outlineColor != null)
					g.color(outlineColor).drawRect(x - lineWidthHalf, 0, lineWidth, v);
			}
			x += delta;
		}
		g.restore();
	}
}
