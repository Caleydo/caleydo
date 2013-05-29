/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.color.mapping;

import java.util.ArrayList;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.Colors;

/**
 * <p>
 * Default color schemes for the ColorMapper. They can be used by calling the
 * {@link ColorMapper#createDefaultMapper(EDefaultColorSchemes)} mapper with any of the values.
 * </p>
 * <p>
 * The color schemes defined here are with the exception of the traditional Green-Black-Red scheme all taken from
 * www.colorbrewer.org.
 * </p>
 * <p>
 * A flag marks whether the colors should be discrete or interpolated (f
 * </p>
 *
 * @author Alexander Lex
 */

public enum EDefaultColorSchemes {

	GREEN_BLACK_RED(
			"Green-Black-Red",
			"Traditional 3-class bioinformatics heat map colors. Not color-blind friendly!",
			new Color(0, 255, 0),
			new Color(0, 0, 0),
			new Color(255, 0, 0)),

	BLUE_WHITE_RED(
			"Blue-White-Red",
			"3-class Red-Blue diverging color scheme from colorbrewer.org. Color-blind and print friendly.",
			new Color(5, 113, 176),
			// new Color( 247, 247, 247 ),
			Colors.NEUTRAL_GREY,
			new Color(202, 0, 32)),
	GREEN_WHITE_BROWN(
			"Green-White-Brown",
			"3-class Brown-Blue-Green diverging color scheme from colorbrewer.org. Color-blind and print friendly.",
			new Color(1, 133, 113),
			Colors.NEUTRAL_GREY,
			new Color(166, 97, 26)),
	GREEN_WHITE_PURPLE(
			"Green-White-Purple",
			"3-class Purple-Green diverging color scheme from colorbrewer.org. Color-blind and print friendly. May cause issues with bad LCDs",
			new Color(0, 136, 55),
			Colors.NEUTRAL_GREY,
			new Color(123, 50, 148)),
	GREY_WHITE_RED(
			"Grey-White-Red",
			"3-class Red-Grey diverging color scheme from colorbrewer.org. Print friendly. May cause issues with color-blind users and bad LCDs",
			new Color(64, 64, 64),
			Colors.NEUTRAL_GREY,
			new Color(202, 0, 32)),

	BLUE_RED_YELLOW(
			"Blue-Red-Yellow",
			"3-class Spectral diverging color scheme from colorbrewer.org. Print friendly. May cause issues with color-blind users",
			new Color(43, 131, 186),
			new Color(215, 25, 28),
			new Color(253, 255, 191)),
	GREEN_WHITE(
			"Green-White",
			"2-class Sequential Blue-Green color scheme from colorbrewer.org. Color-blind friendly. May cause issues with printing and bad LCDs",
			new Color(215, 25, 28),
			new Color(253, 255, 191),
			new Color(43, 131, 186)),
	RED_YELLOW_BLUE_DIVERGING(
			"Blue-Red-Yellow",
			"5-class Red-Yellow-Blue diverging color scheme from colorbrewer.org. Print friendly. May cause issues with color-blind users",
			new Color(215, 25, 28),
			new Color(253, 174, 97),
			new Color(255, 255, 191),
			new Color(171, 217, 233),
			new Color(44, 123, 182)),

	GREY_RED("Grey-Red", "2-class Red-Grey  color scheme Print friendly.", Colors.NEUTRAL_GREY, new Color(202, 0, 32)),
	// -------- DISCRETE COLOR SCHEMES ---------

	WHITE_RED(
			"White-Red",
			"2-class White-Blue diverging color scheme from colorbrewer.org. Color-blind and print friendly.",
			true,
			// new Color( 247, 247, 247 },
			Colors.NEUTRAL_GREY,
			new Color(202, 0, 32));

	private ArrayList<ColorMarkerPoint> colorMarkerPoints;
	/**
	 * The name of the color scheme, human readable, to, e.g., be displayed in choosing dialogs.
	 */
	private String colorSchemeName;
	/**
	 * The description of the color scheme, including a brief statement of the source, whether or not the scheme is
	 * color-blind friendly, causes issues with printing or LCDs.
	 */
	private String colorSchemeDescription;

	/**
	 * Determines whether the color scheme uses discrete colors.
	 */
	private boolean isDiscrete;

	/**
	 *
	 */

	private EDefaultColorSchemes(String colorSchemeName, String colorShemeDescription, Color... colors) {
		initialize(colorSchemeName, colorShemeDescription, false, colors);
	}

	private EDefaultColorSchemes(String colorSchemeName, String colorShemeDescription, boolean isDiscrete,
			Color... colors) {

		initialize(colorSchemeName, colorShemeDescription, isDiscrete, colors);
	}

	private void initialize(String colorSchemeName, String colorShemeDescription, boolean isDiscrete, Color[] colors) {
		this.colorSchemeName = colorSchemeName;
		this.colorSchemeDescription = colorShemeDescription;
		this.isDiscrete = isDiscrete;
		if (colors.length < 2)
			throw new IllegalStateException("At least two values required");
		colorMarkerPoints = new ArrayList<ColorMarkerPoint>();

		int colorCount = 0;
		ColorMarkerPoint point;
		double mappingValueDistance = 1d / (colors.length - 1);
		float nextMappingVlaue = 0;

		float spread = 0.05f;
		if (isDiscrete)
			spread = 1.0f / colors.length;
		for (Color color : colors) {
			point = new ColorMarkerPoint(nextMappingVlaue, color);

			// set spread only for first and last
			if (colorCount == 0 || (isDiscrete && colorCount != colors.length - 1)) {
				point.setRightSpread(spread);
			}
			if (colorCount == colors.length - 1) {
				point.setLeftSpread(spread);

			}
			colorMarkerPoints.add(point);
			nextMappingVlaue += mappingValueDistance;
			colorCount++;
		}

	}

	/**
	 * @return the colorSchemeName, see {@link #colorSchemeName}
	 */
	public String getColorSchemeName() {
		return colorSchemeName;
	}

	/**
	 * @return the colorSchemeDescription, see {@link #colorSchemeDescription}
	 */
	public String getColorSchemeDescription() {
		return colorSchemeDescription;
	}

	/**
	 * @return the colorMarkerPoints, see {@link #colorMarkerPoints}
	 */
	public ArrayList<ColorMarkerPoint> getColorMarkerPoints() {
		return colorMarkerPoints;
	}

	ColorMapper getDefaultColorMapper() {
		ColorMapper colorMapper = new ColorMapper(colorMarkerPoints);
		colorMapper.setColorSchemeName(colorSchemeName);
		colorMapper.setColorSchemeDescription(colorSchemeDescription);
		return colorMapper;
	}

	/**
	 * @return the isDiscrete, see {@link #isDiscrete}
	 */
	public boolean isDiscrete() {
		return isDiscrete;
	}
}
