/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
