/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.color;

import javax.xml.bind.annotation.XmlType;

/**
 * Class representing a color using RGBA values.
 * 
 * @author Partl
 */
@XmlType
public final class Color {
	public float r;
	public float g;
	public float b;
	public float a;

	public Color() {

	}

	public Color(float r, float g, float b, float a) {
		setRGBA(r, g, b, a);
	}

	public Color(float r, float g, float b) {
		setRGBA(r, g, b, 1);
	}

	public void setRGBA(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(float[] rgba) {
		setRGBA(rgba);
	}

	public void setRGBA(float[] rgba) {
		setRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public float[] getRGB() {
		return new float[] { r, g, b };
	}

	public float[] getRGBA() {
		return new float[] { r, g, b, a };
	}

	public Color getColorWithSpecificBrighness(float brightness) {

		float[] hsb = new float[3];
		java.awt.Color.RGBtoHSB((int) (r * 255f), (int) (g * 255f), (int) (b * 255f), hsb);
		hsb[2] = brightness;
		java.awt.Color convertedColor = java.awt.Color.getHSBColor(hsb[0], hsb[1], brightness);

		return new Color(convertedColor.getRed() / 255f, convertedColor.getGreen() / 255f,
				convertedColor.getBlue() / 255f);
	}

}
