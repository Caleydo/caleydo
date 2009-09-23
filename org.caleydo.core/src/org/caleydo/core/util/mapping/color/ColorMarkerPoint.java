package org.caleydo.core.util.mapping.color;

/**
 * <p>
 * A point that represents an inflection point in a color range. For example, when a color map would go from
 * red at 0 to green at 1 the points 0 and 1 and their associated colors would be a ColorMarkerPoint
 * </p>
 * <p>
 * Works with float[] because this allows the values to be plugged directly into OpenGL calls, without
 * accessing each point separately.
 * </p>
 * <p>
 * Additionally the color marker points have spreads - which signal an area of constant color. For example if
 * a marker point has a value of 0.5 and a left spread of 0.1 and a right spread of 0.2 then the region
 * between 0.4 and 0.7 is in the constant color of the marker point. Only at the end of the spreads the
 * interpolation to the next color begins.
 * </p>
 * 
 * @author Alexander Lex
 */
public class ColorMarkerPoint
	implements Comparable<ColorMarkerPoint> {
	private float fValue;
	private float[] fArColor;
	private float fLeftSpread = 0.0f;
	private float fRightSpread = 0.0f;

	/**
	 * Default no-arg constructor, especially needed for xml-serialization.
	 */
	public ColorMarkerPoint() {

	}

	/**
	 * Constructor. To create a new marker point pass two variables, fValue and fArColor.
	 * <p>
	 * fValue represents the where on the mapping range the point is situated. Values are considered to be
	 * normalized between 0 and 1.
	 * </p>
	 * <p>
	 * fArColor has to be a float array of length 3, with values representing the red, green and blue
	 * component. The values have to be between 0 and 1
	 * </p>
	 * 
	 * @param fValue
	 *            the inflection point on the color field
	 * @param fArColor
	 *            the color array
	 */
	public ColorMarkerPoint(float fValue, float[] fArColor) {
		init(fValue, fArColor);
	}

	/**
	 * <p>
	 * Alternative constructor. See {@link #ColorMarkerPoint(float, float[])}.
	 * </p>
	 * <p>
	 * Values are specified one by one instead of as an array
	 * </p>
	 * 
	 * @param fValue
	 *            the inflection point on the color field
	 * @param fRed
	 *            red component of the color
	 * @param fGreen
	 *            green component of the color
	 * @param fBlue
	 *            blue component of the color
	 */
	public ColorMarkerPoint(float fValue, float fRed, float fGreen, float fBlue) {
		float[] fArColor = new float[3];
		fArColor[0] = fRed;
		fArColor[1] = fGreen;
		fArColor[2] = fBlue;
		init(fValue, fArColor);
	}

	/**
	 * Returns the inflection point on the color field
	 * 
	 * @return the infleciton point
	 */
	public float getValue() {
		return fValue;
	}

	public void setLeftSpread(float fSpreadLeft) {
		this.fLeftSpread = fSpreadLeft;
	}

	public void setRightSpread(float fSpreadRight) {
		this.fRightSpread = fSpreadRight;
	}

	/**
	 * Set a value for the inflection point for later modification
	 * 
	 * @param fValue
	 *            the new inflection point value
	 */
	public void setValue(float fValue) {
		this.fValue = fValue;
	}

	/**
	 * Returns the color associated with the inflection Point
	 * 
	 * @return the color
	 */
	public float[] getColor() {
		return fArColor;
	}

	public boolean hasLeftSpread() {
		if (fLeftSpread > 0.0001)
			return true;
		return false;
	}

	public boolean hasRightSpread() {
		if (fRightSpread > 0.0001)
			return true;
		return false;
	}

	public float getLeftSpread() {
		return fLeftSpread;
	}

	public float getRightSpread() {
		return fRightSpread;
	}

	private void init(float fValue, float[] fArColor) {
		if (fArColor.length != 3)
			throw new IllegalArgumentException("Invalid length of color array fColor");

		if (fValue > 1 || fValue < 0)
			throw new IllegalArgumentException(
				"Invalid value for fValue. Has to be between 0 and 1, but was: " + fValue);

		for (float fColorValue : fArColor) {
			if (fColorValue > 1 || fColorValue < 0)
				throw new IllegalArgumentException(
					"Invalid value in fArColor. Has to be between 0 and 1, but was: " + fColorValue);
		}

		this.fValue = fValue;
		this.fArColor = fArColor;
	}

	@Override
	public int compareTo(ColorMarkerPoint colorMarkerPoint) {
		return new Float(fValue).compareTo(colorMarkerPoint.getValue());
	}
}
