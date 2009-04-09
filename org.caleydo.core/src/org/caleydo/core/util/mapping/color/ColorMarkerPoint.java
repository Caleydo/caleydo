package org.caleydo.core.util.mapping.color;

/**
 * <p>
 * A point that represents an inflection point in a color range. For example, when a color map would go from
 * red at 0 to green at 1 the points 0 and 1 and their associated colors would be a ColorMarkerPoint
 * </p>
 * <p>
 * Works with float[] because this allows the values to be plugged directly into OpenGL calls, without
 * accessing each point separately.
 * 
 * @author Alexander Lex
 */
public class ColorMarkerPoint
	implements Comparable<ColorMarkerPoint> {
	private float fValue;
	private float[] fArColor;

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

	private void init(float fValue, float[] fArColor) {
		if (fArColor.length != 3)
			throw new IllegalArgumentException("Invalid length of color array fColor");

		if (fValue > 1 || fValue < 0)
			throw new IllegalArgumentException("Invalid value for fValue. Has to be between 0 and 1");

		for (float fColorValue : fArColor) {
			if (fColorValue > 1 || fColorValue < 0)
				throw new IllegalArgumentException("Invalid value in fArColor. Has to be between 0 and 1");
		}

		this.fValue = fValue;
		this.fArColor = fArColor;
	}

	@Override
	public int compareTo(ColorMarkerPoint colorMarkerPoint) {
		return new Float(fValue).compareTo(colorMarkerPoint.getValue());
	}
}
