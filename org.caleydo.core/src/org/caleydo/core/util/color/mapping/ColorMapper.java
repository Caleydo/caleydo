/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.color.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.util.color.Color;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Display;

/**
 * Color mapping. The class is initialized with a list of inflection points and an associated color. A color mapping for
 * values between 0 and 1 based on the provided points is accessible.
 *
 * @author Alexander Lex
 */
@XmlType
public class ColorMapper {

	private List<Color> colorList;
	private List<ColorMarkerPoint> markerPoints;

	// ColorMappingType colorMappingType;

	private String colorSchemeName = "Unspecified";
	private String colorSchemeDescription = "No description given";

	@XmlTransient
	public static final int COLOR_DEPTH = 256;

	/**
	 * Default no-arg constructor, needed for serialization.
	 */
	public ColorMapper() {
	}

	/**
	 * <p>
	 * Constructor. Provide a list of {@link ColorMarkerPoint} where the first has the smallest value, and each next
	 * point has a bigger value. These color points work as inflection points. Between two adjacent points the colors
	 * are interpolated.
	 * </p>
	 * <p>
	 * Additionally the color marker points have spreads - which define an area of constant color. For example if a
	 * marker point has a value of 0.5 and a left spread of 0.1 and a right spread of 0.2 then the region between 0.4
	 * and 0.7 is in the constant color of the marker point. Only at the end of the spreads the interpolation to the
	 * next color begins.
	 * </p>
	 *
	 * @param markerPoints
	 * @throws IllegalArgumentException
	 *             if values in marker points are not increasing, or if fvalue > 1 || fvalue < 0
	 */
	public ColorMapper(List<ColorMarkerPoint> markerPoints) {
		setMarkerPoints(markerPoints);
	}

	/**
	 * Returns the default two-color color mapper for Caleydo. Default scheme is {@link EDefaultColorSchemes#GREY_RED}
	 */
	public static ColorMapper createDefaultTwoColorMapper() {
		return EDefaultColorSchemes.GREY_RED.getDefaultColorMapper();
	}

	/**
	 * Returns the default two-color color mapper for Caleydo. Default scheme is
	 * {@link EDefaultColorSchemes#BLUE_WHITE_REDD}
	 */
	public static ColorMapper createDefaultThreeColorMapper() {
		return EDefaultColorSchemes.BLUE_WHITE_RED.getDefaultColorMapper();
	}

	/**
	 * Returns a new {@link ColorMapper} for the schema specified
	 *
	 * @param colorSchema
	 * @return
	 */
	public static ColorMapper createDefaultMapper(EDefaultColorSchemes colorSchema) {
		return colorSchema.getDefaultColorMapper();
	}

	/**
	 * @return the colorSchemeName, see {@link #colorSchemeName}
	 */
	public String getColorSchemeName() {
		return colorSchemeName;
	}

	/**
	 * @param colorSchemeName
	 *            setter, see {@link #colorSchemeName}
	 */
	public void setColorSchemeName(String colorSchemeName) {
		this.colorSchemeName = colorSchemeName;
	}

	/**
	 * @return the colorSchemeDescription, see {@link #colorSchemeDescription}
	 */
	public String getColorSchemeDescription() {
		return colorSchemeDescription;
	}

	/**
	 * @param colorSchemeDescription
	 *            setter, see {@link #colorSchemeDescription}
	 */
	public void setColorSchemeDescription(String colorSchemeDescription) {
		this.colorSchemeDescription = colorSchemeDescription;
	}

	private void init() {
		colorList = new ArrayList<Color>(COLOR_DEPTH);
		for (int iCount = 0; iCount < COLOR_DEPTH; iCount++) {
			colorList.add(new Color());
		}
		setUpMapping();
	}

	/**
	 * Initialize the color mapping
	 *
	 * @param markerPoints
	 *            the marker points
	 */
	private void setUpMapping() {
		Collections.sort(markerPoints);
		List<ColorMarkerPoint> finalMarkerPoints = considerSpread();
		// System.out.println(finalMarkerPoints.toString());
		float srcValue, destValue;

		for (int count = 0; count < finalMarkerPoints.size() - 1; count++) {
			srcValue = finalMarkerPoints.get(count).getMappingValue();
			destValue = finalMarkerPoints.get(count + 1).getMappingValue();

			if (destValue < srcValue)
				throw new IllegalArgumentException("Marker points values have to be increasing in size, "
						+ "but this was not the case");

			Color srcColor = finalMarkerPoints.get(count).getColor();
			Color destColor = finalMarkerPoints.get(count + 1).getColor();

			int srcIndex = (int) (srcValue * (COLOR_DEPTH - 1));
			int destIndex = (int) (destValue * (COLOR_DEPTH - 1));

			int colorRange = destIndex - srcIndex;

			for (int innerCount = 0; innerCount <= colorRange; innerCount++) {
				Color color = colorList.get(srcIndex + innerCount);
				float divisor = (float) colorRange / (float) innerCount;
				color.r = srcColor.r + (destColor.r - srcColor.r) / divisor;
				color.g = srcColor.g + (destColor.g - srcColor.g) / divisor;
				color.b = srcColor.b + (destColor.b - srcColor.b) / divisor;
				color.a = srcColor.a + (destColor.a - srcColor.a) / divisor;
			}

		}
		// System.out.println(colorList);
	}

	/**
	 * Return the mapped color as a {@link Color} object
	 *
	 * @param value
	 */
	public Color getColorAsObject(float value) {
		if (Float.isNaN(value))
			return Color.NOT_A_NUMBER_COLOR;

		if (value > 1 || value < 0)
			throw new IllegalArgumentException("Invalid value in fValue. Has to be between 0 and 1 but was: " + value);

		return colorList.get((int) (value * (COLOR_DEPTH - 1)));
	}

	/**
	 * Return the mapped color for any value between 0 and 1
	 *
	 * @param value
	 * @return float array with length 3, RGB
	 * @throws IllegalArgumentException
	 *             if value > 1 || value < 0
	 */
	public float[] getColor(float value) {
		return getColorAsObject(value).getRGBA();
	}

	/**
	 * Get the marker points on which the color mapping is based
	 *
	 * @return the list of marker points
	 */
	public List<ColorMarkerPoint> getMarkerPoints() {
		return markerPoints;
	}

	/**
	 * Returns the list of marker points, but with spread converted to a separate marker point. This means that a marker
	 * point at 0.5 with a left spread of 0.1 will result in two marker points, one with 0.4 and one with 0.5 of the
	 * same color in this list
	 *
	 * @return the list of marker points without spreads but points for spreads
	 */
	public List<ColorMarkerPoint> getConvertedMarkerPoints() {
		return considerSpread();
	}

	public void update() {
		setUpMapping();
	}

	/**
	 * Converts the spread in color marker points to separate color points, which are easier to map later. Does some
	 * checking and error handling.
	 *
	 * @return the list with all the marker points instead of spreads
	 */
	private List<ColorMarkerPoint> considerSpread() {
		List<ColorMarkerPoint> finalColorMarkerPoints = new ArrayList<ColorMarkerPoint>();

		for (ColorMarkerPoint point : markerPoints) {
			if (point.hasLeftSpread()) {
				float fLeftValue = point.getMappingValue() - point.getLeftSpread();
				finalColorMarkerPoints.add(new ColorMarkerPoint(fLeftValue, point.getColor()));
			}
			finalColorMarkerPoints.add(point);
			if (point.hasRightSpread()) {
				float fRightValue = point.getMappingValue() + point.getRightSpread();
				finalColorMarkerPoints.add(new ColorMarkerPoint(fRightValue, point.getColor()));
			}
		}

		return finalColorMarkerPoints;
	}

	public List<Color> getColorList() {
		return colorList;
	}

	public void setColorList(ArrayList<Color> colorList) {
		this.colorList = colorList;
	}

	public Color getNotANumberColor() {
		return Color.NOT_A_NUMBER_COLOR;
	}

	public void setMarkerPoints(List<ColorMarkerPoint> markerPoints) {
		this.markerPoints = markerPoints;
		init();
	}

	@Override
	public String toString() {
		return colorSchemeName;
	}

	public static void createColorMappingPreview(ColorMapper colorMapper, CLabel colorMappingPreview) {

		List<ColorMarkerPoint> markerPoints = colorMapper.getMarkerPoints();

		org.eclipse.swt.graphics.Color[] alColor = new org.eclipse.swt.graphics.Color[markerPoints.size()];
		int[] colorMarkerPoints = new int[markerPoints.size() - 1];
		for (int iCount = 1; iCount <= markerPoints.size(); iCount++) {

			float normalizedValue = markerPoints.get(iCount - 1).getMappingValue();

			int colorMarkerPoint = (int) (100 * normalizedValue);

			// Gradient label does not need the 0 point
			if (colorMarkerPoint != 0) {
				colorMarkerPoints[iCount - 2] = colorMarkerPoint;
			}

			int[] color = markerPoints.get(iCount - 1).getColor().getIntRGBA();

			alColor[iCount - 1] = new org.eclipse.swt.graphics.Color(Display.getDefault(), color[0],
					color[1], color[2]);
		}

		colorMappingPreview.setBackground(alColor, colorMarkerPoints);
		colorMappingPreview.update();
	}
}
