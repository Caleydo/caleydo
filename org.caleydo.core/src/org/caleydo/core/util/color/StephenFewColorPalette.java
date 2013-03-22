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

/**
 * a color palette based on the Stephen Few
 *
 * @author Samuel Gratzl
 *
 */
public final class StephenFewColorPalette {
	private StephenFewColorPalette() {

	}

	/**
	 * three different brightness modi
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public enum EBrightness {
		LIGHT, MEDIUM, DARK_AND_BRIGHT
	}

	private static final IColor[] light;
	private static final IColor[] medium;
	private static final IColor[] dark_and_bright;

	static {
		light = new IColor[9];
		light[0] = new Color(140, 140, 140);
		light[1] = new Color(136, 189, 230);
		light[2] = new Color(251, 178, 88);
		light[3] = new Color(144, 205, 151);
		light[4] = new Color(246, 170, 201);
		light[5] = new Color(191, 165, 84);
		light[6] = new Color(188, 153, 199);
		light[7] = new Color(237, 221, 70);
		light[8] = new Color(240, 126, 110);

		medium = new IColor[9];
		medium[0] = new Color(77, 77, 77);
		medium[1] = new Color(93, 165, 218);
		medium[2] = new Color(250, 164, 58);
		medium[3] = new Color(96, 189, 104);
		medium[4] = new Color(241, 124, 176);
		medium[5] = new Color(178, 145, 47);
		medium[6] = new Color(178, 118, 178);
		medium[7] = new Color(222, 207, 63);
		medium[8] = new Color(241, 88, 84);

		dark_and_bright = new IColor[9];
		dark_and_bright[0] = new Color(0, 0, 0);
		dark_and_bright[1] = new Color(38, 93, 171);
		dark_and_bright[2] = new Color(223, 92, 36);
		dark_and_bright[3] = new Color(5, 151, 72);
		dark_and_bright[4] = new Color(229, 18, 111);
		dark_and_bright[5] = new Color(157, 114, 42);
		dark_and_bright[6] = new Color(123, 58, 150);
		dark_and_bright[7] = new Color(199, 180, 46);
		dark_and_bright[8] = new Color(203, 32, 39);
	}

	public static int size() {
		return light.length;
	}

	/**
	 * returns a color of with a specific brightness at a specific position
	 * 
	 * @param index
	 * @param brightness
	 * @return
	 */
	public static IColor get(int index, EBrightness brightness) {
		if (index < 0 || index >= size())
			throw new IllegalArgumentException("invalid index: " + index);
		switch (brightness) {
		case DARK_AND_BRIGHT:
			return dark_and_bright[index];
		case LIGHT:
			return light[index];
		case MEDIUM:
			return medium[index];
		}
		throw new IllegalStateException();
	}

	/**
	 * returns all colors of the given brightness
	 *
	 * @param brightness
	 * @return
	 */
	public static IColor[] get(EBrightness brightness) {
		switch (brightness) {
		case DARK_AND_BRIGHT:
			return dark_and_bright;
		case LIGHT:
			return light;
		case MEDIUM:
			return medium;
		}
		throw new IllegalStateException();
	}
}
