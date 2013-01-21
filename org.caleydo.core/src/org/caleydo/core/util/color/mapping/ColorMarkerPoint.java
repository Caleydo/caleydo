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

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.util.color.Color;

/**
 * <p>
 * A point that represents an inflection point in a color range. The ColorMarkerPoint has three properties:
 * <ol>
 * <li>The color at the inflection point.</li>
 * <li>The mapping value at the inflection point. The color mapping expects to be applied to normalized values in the
 * range of 0 to 1. For example, when a color map goes from red to green, the color map would have a red
 * {@link ColorMarkerPoint} at {@link #mappingValue} 0 and a red one at 1.</li>
 * <li>The spreads - which define an area of constant color. For example if a marker point has a value of 0.5 and a left
 * spread of 0.1 and a right spread of 0.2 then the region between 0.4 and 0.7 is in the constant color of the marker
 * point. Only at the end of the spreads the interpolation to the next color begins.</li>
 * </ol>
 * </p>
 *
 * @author Alexander Lex
 */
@XmlType
public class ColorMarkerPoint implements Comparable<ColorMarkerPoint> {
	/**
	 * The inflection point on the color field in the normalized range of 0 to 1 where the color is pure, not mixed with
	 * another {@link ColorMarkerPoint}
	 */

	private float mappingValue;
	// @XmlElement
	// private float[] color;
	// @XmlElement
	// private int[] intColor;

	private Color color;

	private float leftSpread = 0.0f;

	private float rightSpread = 0.0f;

	/**
	 * Default no-arg constructor, especially needed for xml-serialization.
	 */
	public ColorMarkerPoint() {

	}

	/**
	 * Constructor. To create a new marker point pass two variables, fValue and fArColor.
	 * <p>
	 * mappingValue represents the where on the mapping range the point is situated. Values are considered to be
	 * normalized between 0 and 1.
	 * </p>
	 * <p>
	 * color has to be a float array of length 3, with values representing the red, green and blue component. The values
	 * have to be between 0 and 1
	 * </p>
	 *
	 * @param fValue
	 *            the inflection point on the color field
	 * @param color
	 *            the color array
	 */
	public ColorMarkerPoint(float mappingValue, Color color) {
		init(mappingValue, color);
	}

	// public ColorMarkerPoint(float mappingValue, int[] color) {
	// this.intColor = color;
	// float[] floatColor = new float[color.length];
	//
	// for (int count = 0; count < color.length; count++) {
	// floatColor[count] = ((float) color[count]) / 255;
	// }
	// init(mappingValue, floatColor);
	// }

	/**
	 * <p>
	 * Alternative constructor. See {@link #ColorMarkerPoint(float, float[])}.
	 * </p>
	 * <p>
	 * Values are specified one by one instead of as an array
	 * </p>
	 *
	 * @param mappingValue
	 *            see {@link #mappingValue}
	 * @param red
	 *            red component of the color
	 * @param green
	 *            green component of the color
	 * @param blue
	 *            blue component of the color
	 */
	// public ColorMarkerPoint(float mappingValue, float red, float green, float blue) {
	// float[] color = new float[3];
	// color[0] = red;
	// color[1] = green;
	// color[2] = blue;
	// init(mappingValue, color);
	// }

	/**
	 * Returns the inflection point on the color field
	 *
	 * @return the infleciton point
	 */
	public float getMappingValue() {
		return mappingValue;
	}

	public void setLeftSpread(float leftSpread) {
		this.leftSpread = leftSpread;
	}

	public void setRightSpread(float rightSpread) {
		this.rightSpread = rightSpread;
	}

	/**
	 * @param mappingValue
	 *            setter, see {@link #mappingValue}
	 */
	public void setMappingValue(float mappingValue) {
		this.mappingValue = mappingValue;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns the color associated with the inflection Point
	 *
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}



	public boolean hasLeftSpread() {
		if (leftSpread > 0.0001)
			return true;
		return false;
	}

	public boolean hasRightSpread() {
		if (rightSpread > 0.0001)
			return true;
		return false;
	}

	public float getLeftSpread() {
		return leftSpread;
	}

	public float getRightSpread() {
		return rightSpread;
	}

	private void init(float mappingValue, Color color) {

		if (mappingValue > 1 || mappingValue < 0)
			throw new IllegalArgumentException("Invalid value for mappingValue. Has to be between 0 and 1, but was: "
					+ mappingValue);

		this.mappingValue = mappingValue;
		this.color = color;
	}

	@Override
	public int compareTo(ColorMarkerPoint colorMarkerPoint) {
		return new Float(mappingValue).compareTo(colorMarkerPoint.getMappingValue());
	}
}
