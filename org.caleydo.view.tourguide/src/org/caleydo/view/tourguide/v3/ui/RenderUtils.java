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
package org.caleydo.view.tourguide.v3.ui;

import java.awt.Color;
import java.beans.PropertyChangeListener;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.tourguide.v3.model.SimpleHistogram;

/**
 * @author Samuel Gratzl
 *
 */
public class RenderUtils {
	public static void renderHist(GLGraphics g, SimpleHistogram hist, float w, float h, int selectedBin, Color color,
			Color selectionColor) {
		w -= 2;
		float factor = h / hist.getLargestValue();
		float delta = w / hist.size();

		g.save();
		float x = 1 + delta / 2;
		g.move(0, h - 1).color(color);
		for (int i = 0; i < hist.size(); ++i) {
			if (selectedBin == i) {
				g.color(selectionColor);
			}
			float v = -hist.get(i) * factor;

			if (v <= -1) {
				g.drawLine(x, 0, x, v);
			}
			if (selectedBin == i) {
				g.color(color);
			}
			x += delta;
		}
		g.restore();
	}

	public static void renderStackedHist(GLGraphics g, SimpleHistogram[] hists, float w, float h, int[] selectedBins,
			Color[] colors, Color[] selectionColors) {
		if (hists.length == 1) {
			renderHist(g, hists[0], w, h, selectedBins[0], colors[0], selectionColors[0]);
			return;
		}
		w -= 2;
		int size = hists[0].size();
		int largest = 0;
		for (int i = 0; i < size; ++i) {
			int act = 0;
			for (SimpleHistogram hist : hists)
				act += hist.get(i);
			if (act > largest)
				largest = act;
		}
		float factor = h / largest;
		float delta = w / size;

		g.save();
		float x = 1 + delta / 2;
		g.move(0, h - 1);
		for (int i = 0; i < size; ++i) {
			float vi = 0;
			for (int j = 0; j < hists.length; ++j) {
				if (selectedBins[j] == i) {
					g.color(selectionColors[j]);
				} else
					g.color(colors[j]);
				float v = -hists[j].get(i) * factor;

				if (v <= -1) {
					g.drawLine(x, vi, x, vi + v);
					vi += v;
				}
			}
			x += delta;
		}
		g.restore();
	}
}
