/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;


import java.util.Map;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.vis.lineup.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.caleydo.vis.lineup.model.SimpleHistogram;

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
