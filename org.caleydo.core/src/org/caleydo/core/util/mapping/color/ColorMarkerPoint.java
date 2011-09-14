package org.caleydo.core.util.mapping.color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * A point that represents an inflection point in a color range. The ColorMarkerPoint has three properties:
 * <ol>
 * <li>The color at the inflection point.</li>
 * <li>The mapping value at the inflection point. The color mapping expects to be applied to normalized values
 * in the range of 0 to 1. For example, when a color map goes from red to green, the color map would have a
 * red {@link ColorMarkerPoint} at {@link #mappingValue} 0 and a red one at 1.</li>
 * <li>The spreads - which define an area of constant color. For example if a marker point has a value of 0.5
 * and a left spread of 0.1 and a right spread of 0.2 then the region between 0.4 and 0.7 is in the constant
 * color of the marker point. Only at the end of the spreads the interpolation to the next color begins.</li>
 * </ol>
 * </p>
 * 
 * @author Alexander Lex
 */
@XmlType
public class ColorMarkerPoint
	implements Comparable<ColorMarkerPoint> {
	/**
	 * The inflection point on the color field in the normalized range of 0 to 1 where the color is pure, not
	 * mixed with another {@link ColorMarkerPoint}
	 */

	private float mappingValue;
	@XmlElement
	private float[] color;
	@XmlElement
	private int[] intColor;

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
	 * mappingValue represents the where on the mapping range the point is situated. Values are considered to
	 * be normalized between 0 and 1.
	 * </p>
	 * <p>
	 * color has to be a float array of length 3, with values representing the red, green and blue component.
	 * The values have to be between 0 and 1
	 * </p>
	 * 
	 * @param fValue
	 *            the inflection point on the color field
	 * @param color
	 *            the color array
	 */
	public ColorMarkerPoint(float mappingValue, float[] color) {
		init(mappingValue, color);
	}

	public ColorMarkerPoint(float mappingValue, int[] color) {
		this.intColor = color;
		float[] floatColor = new float[color.length];

		for (int count = 0; count < color.length; count++) {
			floatColor[count] = ((float) color[count]) / 255;
		}
		init(mappingValue, floatColor);
	}

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
	public ColorMarkerPoint(float mappingValue, float red, float green, float blue) {
		float[] color = new float[3];
		color[0] = red;
		color[1] = green;
		color[2] = blue;
		init(mappingValue, color);
	}

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
	 * Returns the color associated with the inflection Point
	 * 
	 * @return the color
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * @return the intColor, see {@link #intColor}
	 */
	public int[] getIntColor() {
		if (intColor == null) {
			intColor = new int[color.length];
			for (int count = 0; count < color.length; count++)
				intColor[count] = (int) (color[count] * 255);
		}
		return intColor;

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

	private void init(float mappingValue, float[] color) {
		if (color.length != 3)
			throw new IllegalArgumentException("Invalid length of color array fColor");

		if (mappingValue > 1 || mappingValue < 0)
			throw new IllegalArgumentException(
				"Invalid value for fValue. Has to be between 0 and 1, but was: " + mappingValue);

		for (float colorComponent : color) {
			if (colorComponent > 1 || colorComponent < 0)
				throw new IllegalArgumentException(
					"Invalid value in color. Has to be between 0 and 1, but was: " + colorComponent);
		}

		this.mappingValue = mappingValue;
		this.color = color;
	}

	@Override
	public int compareTo(ColorMarkerPoint colorMarkerPoint) {
		return new Float(mappingValue).compareTo(colorMarkerPoint.getMappingValue());
	}
}
