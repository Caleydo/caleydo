package org.caleydo.core.util.mapping.color;

import java.util.ArrayList;

/**
 * <p>
 * Default color schemes for the ColorMapper. They can be used by calling the
 * {@link ColorMapper#createDefaultMapper(EDefaultColorSchemes)} mapper with any of the values.
 * </p>
 * <p>
 * The color schemes defined here are with the exception of the traditional Green-Black-Red scheme all taken
 * from www.colorbrewer.org.
 * </p>
 * 
 * @author Alexander Lex
 */
public enum EDefaultColorSchemes {
	GREEN_BLACK_RED(
		"Green-Black-Red",
		"Traditional 3-class bioinformatics heat map colors. Not color-blind friendly!",
		new int[] { 0, 255, 0 },
		new int[] { 0, 0, 0 },
		new int[] { 255, 0, 0 }),
	BLUE_WHITE_RED(
		"Blue-White-Red",
		"3-class Red-Blue diverging color scheme from colorbrewer.org. Color-blind and print friendly.",
		new int[] { 5, 113, 176 },
		new int[] { 247, 247, 247 },
		new int[] { 202, 0, 32 }),
	GREEN_WHITE_BROWN(
		"Green-White-Brown",
		"3-class Brown-Blue-Green diverging color scheme from colorbrewer.org. Color-blind and print friendly.",
		new int[] { 1, 133, 113 },
		new int[] { 245, 245, 245 },
		new int[] { 166, 97, 26 }),
	GREEN_WHITE_PURPLE(
		"Green-White-Purple",
		"3-class Purple-Green diverging color scheme from colorbrewer.org. Colorblind and print friendly. May cause issues with bad LCDs",
		new int[] { 0, 136, 55 },
		new int[] { 247, 247, 247 },
		new int[] { 123, 50, 148 }),
	GREY_WHITE_RED(
		"Grey-White-Red",
		"3-class Red-Grey diverging color scheme from colorbrewer.org. Print friendly. May cause issues with color-blind users and bad LCDs",
		new int[] { 64, 64, 64 },
		new int[] { 255, 255, 255 },
		new int[] { 202, 0, 32 }),
	BLUE_RED_YELLOW(
		"Blue-Red-Yellow",
		"3-class Spectral diverging color scheme from colorbrewer.org. Print friendly. May cause issues with color-blind users",
		new int[] { 43, 131, 186 },
		new int[] { 215, 25, 28 },
		new int[] { 253, 255, 191 }),
	GREEN_WHITE(
		"Green-White",
		"2-class Sequential Blue-Green color scheme from colorbrewer.org. Color-blind friendly. May cause issues with printing and bad LCDs",
		new int[] { 215, 25, 28 },
		new int[] { 253, 255, 191 },
		new int[] { 43, 131, 186 });

	private ArrayList<ColorMarkerPoint> colorMarkerPoints;
	/** The name of the color scheme, human readable, to, e.g., be displayed in choosing dialogs. */
	private String colorSchemeName;
	/**
	 * The description of the color scheme, including a brief statement of the source, whether or not the
	 * scheme is color-blind freindly, causes issues with printing or LCDs.
	 */
	private String colorSchemeDescription;

	private EDefaultColorSchemes(String colorSchemeName, String colorShemeDescription, int[]... colors) {
		this.colorSchemeName = colorSchemeName;
		this.colorSchemeDescription = colorShemeDescription;
		if (colors.length < 2)
			throw new IllegalStateException("At least two values required");
		colorMarkerPoints = new ArrayList<ColorMarkerPoint>();

		int colorCount = 0;
		ColorMarkerPoint point;
		double mappingValueDistance = 1d / (colors.length - 1);
		float nextMappingVlaue = 0;
		float spread = 0.05f;
		for (int[] color : colors) {
			point = new ColorMarkerPoint(nextMappingVlaue, color);

			// no spread for first and last element to the right resp. to the left
			if (colorCount != 0) {
				point.setLeftSpread(spread);
			}
			if (colorCount != colors.length - 1) {
				point.setRightSpread(spread);
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

	ColorMapper getDefaultColorMapper() {
		ColorMapper colorMapper = new ColorMapper(colorMarkerPoints);
		colorMapper.setColorSchemeName(colorSchemeName);
		colorMapper.setColorSchemeDescription(colorSchemeDescription);
		return colorMapper;
	}
}
