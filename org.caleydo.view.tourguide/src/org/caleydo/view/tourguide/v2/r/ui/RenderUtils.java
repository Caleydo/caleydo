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
package org.caleydo.view.tourguide.v2.r.ui;

import java.awt.Color;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Samuel Gratzl
 *
 */
public class RenderUtils {
	public static void renderHist(GLGraphics g, Histogram hist, float w, float h, int selectedBin, Color color,
			Color selectionColor) {
		float factor = h / hist.getLargestValue();
		float delta = w / hist.size();

		g.save();
		g.move(delta / 2, h - 1).color(color);
		for (int i = 0; i < hist.size(); ++i) {
			if (selectedBin == i) {
				g.color(selectionColor);
			}
			float v = -hist.get(i) * factor;

			if (v <= -1) {
				g.drawLine(0, 0, 0, v);
			}
			if (selectedBin == i) {
				g.color(color);
			}
			g.move(delta, 0);
		}
		g.restore();
	}
}
