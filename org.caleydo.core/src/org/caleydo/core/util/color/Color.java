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

/**
 * Class representing a color using RGBA values.
 *
 * @author Partl
 * @author Alexander Lex
 */
@XmlType
public final class Color implements IColor {
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
		intColor = new int[4];
		intColor[0] = r;
		intColor[1] = g;
		intColor[2] = b;
		intColor[3] = a;
		setRGBA(r / colorDepth, g / colorDepth, b / colorDepth, a / colorDepth);
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
			throw new IllegalArgumentException("One color value was < 0 or > 1" + r + g + b + a);
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

	public float[] getRGB() {
		return new float[] { r, g, b };
	}

	@Override
	public float[] getRGBA() {
		return new float[] { r, g, b, a };
	}

	public String getHEX() {
		String hexColor = Integer.toHexString(new java.awt.Color((int) (r * 255f), (int) (g * 255f), (int) (b * 255f))
				.getRGB());
		hexColor = hexColor.substring(2, hexColor.length());
		return hexColor;
	}

	/**
	 *
	 * @param brightness
	 *            1 is bright, 0 is black
	 * @return
	 */
	public Color getColorWithSpecificBrighness(float brightness) {

		float[] hsb = new float[3];
		java.awt.Color.RGBtoHSB((int) (r * 255f), (int) (g * 255f), (int) (b * 255f), hsb);
		hsb[2] = brightness;
		java.awt.Color convertedColor = java.awt.Color.getHSBColor(hsb[0], hsb[1], brightness);

		return new Color(convertedColor.getRed() / 255f, convertedColor.getGreen() / 255f,
				convertedColor.getBlue() / 255f);
	}

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
	public String toString() {
		return "Color [" + r + "," + g + "," + b + "," + a + "]";
	}

}
