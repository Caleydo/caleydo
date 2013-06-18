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
package org.caleydo.core.util.color;

import java.util.ArrayList;
import java.util.List;

/**
 * a color palette based on the Stephen Few
 *
 * @author Samuel Gratzl
 *
 */
public final class StephenFewColorPalette {
	private StephenFewColorPalette() {

	}

	private static final List<Color> colors;

	static {
		List<Color> cs = new ArrayList<>();
		cs.add(createTriple(140, 140, 140, /**/77, 77, 77, /**/0, 0, 0));
		cs.add(createTriple(136, 189, 230, /**/93, 165, 218, /**/38, 93, 171));
		cs.add(createTriple(251, 178, 88, /**/250, 164, 58, /**/223, 92, 36));
		cs.add(createTriple(144, 205, 151, /**/96, 189, 104, /**/5, 151, 72));
		cs.add(createTriple(246, 170, 201, /**/241, 124, 176, /**/229, 18, 111));
		cs.add(createTriple(191, 165, 84, /**/178, 145, 47, /**/157, 114, 42));
		cs.add(createTriple(188, 153, 199, /**/178, 118, 178, /**/123, 58, 150));
		cs.add(createTriple(237, 221, 70, /**/222, 207, 63, /**/199, 180, 46));
		cs.add(createTriple(240, 126, 110, /**/241, 88, 84, /**/203, 32, 39));
		colors = cs;
	}

	/**
	 * @return the colors, see {@link #colors}
	 */
	public static List<Color> getColors() {
		return colors;
	}

	private static Color createTriple(int rb,int gb, int bb, int r, int b, int g, int rd, int gd, int bd) {
		StephenFewColor medium = new StephenFewColor(r,g, b);
		StephenFewColor light = new StephenFewColor(rb, gb, bb);
		light.darker = medium;
		StephenFewColor dark = new StephenFewColor(rd, gd, bd);
		dark.brighter = medium;
		medium.brighter = light;
		medium.darker = dark;
		return medium;
	}

	private static class StephenFewColor extends Color {
		private Color darker;
		private Color brighter;

		public StephenFewColor(int r, int g, int b) {
			super(r, g, b);
		}

		@Override
		public Color darker() {
			if (darker != null)
				return darker;
			return super.darker();
		}

		@Override
		public Color brighter() {
			if (brighter != null)
				return brighter();
			return super.brighter();
		}
	}
}
