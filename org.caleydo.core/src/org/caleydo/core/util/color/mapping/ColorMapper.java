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
import java.util.Collections;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.Colors;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.ui.PlatformUI;

/**
 * Color mapping. The class is initialized with a list of inflection points and an associated color. A color mapping for
 * values between 0 and 1 based on the provided points is accessible.
 *
 * @author Alexander Lex
 */
@XmlType
public class ColorMapper {

	private ArrayList<Color> colorList;
	private ArrayList<ColorMarkerPoint> markerPoints;

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
	public ColorMapper(ArrayList<ColorMarkerPoint> markerPoints) {
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
		ArrayList<ColorMarkerPoint> finalMarkerPoints = considerSpread();
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
				Color fColor = colorList.get(srcIndex + innerCount);
				float divisor = (float) colorRange / (float) innerCount;
				fColor.r = srcColor.r + (destColor.r - srcColor.r) / divisor;
				fColor.g = srcColor.g + (destColor.g - srcColor.g) / divisor;
				fColor.b = srcColor.b + (destColor.b - srcColor.b) / divisor;
			}
		}
	}

	/**
	 * Return the mapped color as a {@link Color} object
	 *
	 * @param value
	 */
	public Color getColorAsObject(float value) {
		if (Float.isNaN(value))
			return Colors.NOT_A_NUMBER_COLOR;

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
	public ArrayList<ColorMarkerPoint> getMarkerPoints() {
		return markerPoints;
	}

	/**
	 * Returns the list of marker points, but with spread converted to a separate marker point. This means that a marker
	 * point at 0.5 with a left spread of 0.1 will result in two marker points, one with 0.4 and one with 0.5 of the
	 * same color in this list
	 *
	 * @return the list of marker points without spreads but points for spreads
	 */
	public ArrayList<ColorMarkerPoint> getConvertedMarkerPoints() {
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
	private ArrayList<ColorMarkerPoint> considerSpread() {
		ArrayList<ColorMarkerPoint> finalColorMarkerPoints = new ArrayList<ColorMarkerPoint>();

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

	public ArrayList<Color> getColorList() {
		return colorList;
	}

	public void setColorList(ArrayList<Color> colorList) {
		this.colorList = colorList;
	}

	public Color getNotANumberColor() {
		return Colors.NOT_A_NUMBER_COLOR;
	}

	public void setMarkerPoints(ArrayList<ColorMarkerPoint> markerPoints) {
		this.markerPoints = markerPoints;
		init();
	}

	@Override
	public String toString() {
		return colorSchemeName;
	}

	public static void createColorMappingPreview(ColorMapper colorMapper, CLabel colorMappingPreview) {

		ArrayList<ColorMarkerPoint> markerPoints = colorMapper.getMarkerPoints();

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

			alColor[iCount - 1] = new org.eclipse.swt.graphics.Color(PlatformUI.getWorkbench().getDisplay(), color[0],
					color[1], color[2]);
		}

		colorMappingPreview.setBackground(alColor, colorMarkerPoints);
		colorMappingPreview.update();
	}
}
