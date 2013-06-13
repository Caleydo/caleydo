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

/**
 * read only version of a color
 *
 * @author Samuel Gratzl
 * @author Alexander Lex
 *
 */
public interface IColor {


	public float[] getRGB();
	public float[] getRGBA();

	/** Returns the hexadecimal representation of the color */
	public String getHEX();

	/**
	 *
	 * @param brightness
	 *            1 is bright, 0 is black
	 * @return
	 */
	public Color getColorWithSpecificBrighness(float brightness);

	/**
	 * Get the color as an int RGBA array with a range of 0-255
	 * @return
	 */
	public int[] getIntRGBA();

	/**
	 * Returns the equivalent AWT color
	 * 
	 * @return
	 */
	public java.awt.Color getAWTColor();
}
