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
package org.caleydo.core.util.color;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.swt.graphics.Device;

/**
 * Class representing a color using RGBA values.
 *
 * @author Partl
 * @author Alexander Lex
 */
@XmlType
public class Color implements IColor {

	public static final Color TRANSPARENT = new Color(1f, 1, 1, 0);
	public static final Color RED = new Color(1f, 0, 0, 1);
	public static final Color GREEN = new Color(0f, 1, 0, 1);
	public static final Color BLUE = new Color(0f, 0, 1, 1);
	public static final Color YELLOW = new Color(1f, 1, 0, 1);
	public static final Color ORANGE = new Color(255, 127, 0, 1);
	public static final Color MAGENTA = new Color(202, 31, 123);
	public static final Color CYAN = new Color(0, 255, 255);

	public static final Color SELECTION_ORANGE = new Color(236, 112, 20);
	public static final Color MOUSE_OVER_ORANGE = new Color(249, 196, 79);

	public static final Color BLACK = new Color(0f, 0f, 0f, 1);
	public static final Color WHITE = new Color(1f, 1f, 1f, 1);

	public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
	public static final Color DARK_GRAY = new Color(0.2f, 0.2f, 0.2f);
	public static final Color LIGHT_GRAY = new Color(0.7f, 0.7f, 0.7f);

	public static final Color NEUTRAL_GREY = new Color(220, 220, 220);
	public static final Color NOT_A_NUMBER_COLOR = new Color(0.3f, 0.3f, 0.3f);

	@XmlElement
	public float r = 1;
	@XmlElement
	public float g = 1;
	@XmlElement
	public float b = 1;
	@XmlElement
	public float a = 1;

	private int[] intColor = null;

	private static final float colorDepth = 255;

	public Color() {

	}

	/**
	 * RGB Constructor taking ints in the range of 0 - {@link #colorDepth} (255).
	 *
	 */
	public Color(String hexColor) {
		if (hexColor.length() == 7 && hexColor.startsWith("#"))
			hexColor = hexColor.substring(1);
		if (hexColor.length() != 6)
			throw new IllegalStateException("Illegal string for hex color: " + hexColor);

		Integer r = Integer.parseInt(hexColor.substring(0, 2), 16);
		Integer g = Integer.parseInt(hexColor.substring(2, 4), 16);
		Integer b = Integer.parseInt(hexColor.substring(4, 6), 16);
		setRGBA(r, g, b, 255);
	}

	public Color(float r, float g, float b, float a) {
		setRGBA(r, g, b, a);
	}

	public Color(float r, float g, float b) {
		setRGBA(r, g, b, 1);
	}

	/**
	 * RGBA Constructor taking ints in the range of 0 - {@link #colorDepth} (255).
	 *
	 */
	public Color(int r, int g, int b, int a) {
		setRGBA(r, g, b, a);
	}

	/**
	 * RGB Constructor taking ints in the range of 0 - {@link #colorDepth} (255).
	 *
	 */
	public Color(int r, int g, int b) {

		this(r, g, b, 255);
	}

	public void setRGBA(float r, float g, float b, float a) {
		if (r > 1 || g > 1 || b > 1 || a > 1 || r < 0 || g < 0 || b < 0 || a < 0) {
			System.out.println("One color value was < 0 or > 1" + r + g + b + a);
			throw new IllegalArgumentException("One color value was < 0 or > 1: " + r + ";" + g + ";" + b + ";" + a);
		}
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(float[] rgba) {
		setRGBA(rgba);
	}

	public void setRGBA(float[] rgba) {
		if (rgba.length != 4)
			throw new IllegalArgumentException("Invalid length of color array rgba");

		setRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	private void setRGBA(int r, int g, int b, int a) {
		intColor = new int[4];
		intColor[0] = r;
		intColor[1] = g;
		intColor[2] = b;
		intColor[3] = a;
		setRGBA(r / colorDepth, g / colorDepth, b / colorDepth, a / colorDepth);
	}

	@Override
	public float[] getRGB() {
		return new float[] { r, g, b };
	}

	@Override
	public float[] getRGBA() {
		return new float[] { r, g, b, a };
	}

	/**
	 * Returns the rgb component of this color combined with the specified alpha value in a float array of length 4. The
	 * internal alpha value is ignored
	 *
	 * @param a
	 * @return
	 */
	public float[] getRGBA(float a) {
		return new float[] { r, g, b, a };
	}

	@Override
	public String getHEX() {
		String hexColor = Integer.toHexString(new java.awt.Color((int) (r * 255f), (int) (g * 255f), (int) (b * 255f))
				.getRGB());
		hexColor = hexColor.substring(2, hexColor.length());
		return hexColor;
	}

	@Override
	public Color getColorWithSpecificBrighness(float brightness) {

		float[] hsb = new float[3];
		java.awt.Color.RGBtoHSB((int) (r * 255f), (int) (g * 255f), (int) (b * 255f), hsb);
		hsb[2] = brightness;
		java.awt.Color convertedColor = java.awt.Color.getHSBColor(hsb[0], hsb[1], brightness);

		return new Color(convertedColor.getRed() / 255f, convertedColor.getGreen() / 255f,
				convertedColor.getBlue() / 255f);
	}

	public Color darker() {
		return new Color(getAWTColor().darker().getComponents(null));
	}

	/**
	 * Gets a brighter version of this color.
	 *
	 * @return
	 */
	public Color brighter() {
		return new Color(getAWTColor().brighter().getComponents(null));
	}

	@Override
	public int[] getIntRGBA() {
		if (intColor == null) {
			intColor = new int[4];
			intColor[0] = Math.round(r * colorDepth);
			intColor[1] = Math.round(g * colorDepth);
			intColor[2] = Math.round(b * colorDepth);
			intColor[3] = Math.round(a * colorDepth);
		}
		return intColor;
	}

	@Override
	public java.awt.Color getAWTColor() {
		return new java.awt.Color(r, g, b);
	}

	public org.eclipse.swt.graphics.Color getSWTColor(Device device) {
		int[] intColor = getIntRGBA();
		return new org.eclipse.swt.graphics.Color(device, intColor[0], intColor[1], intColor[2]);
	}

	@Override
	public String toString() {
		return "Color [" + r + "," + g + "," + b + "," + a + "]";
	}

	/** Returns a new color equal to the provided SWT color */
	public static Color fromSWTColor(org.eclipse.swt.graphics.Color swtColor) {
		return new Color(swtColor.getRed(), swtColor.getGreen(), swtColor.getBlue());
	}

}
