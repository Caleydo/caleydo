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
package org.caleydo.core.view.opengl.util.text;

import java.awt.Color;

import javax.media.opengl.GL2;

/**
 * basic interface for a class than can render text
 *
 * @author Samuel Gratzl
 *
 */
public interface ITextRenderer {
	/**
	 * set the text color to the specified one
	 *
	 * @param color
	 */
	public void setColor(Color color);

	/**
	 * set the text color to the specified one
	 *
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void setColor(float r, float g, float b, float a);

	public float getTextWidth(String text, float height);

	/**
	 * Render the text at the position specified (lower left corner) within the bounding box The height is scaled to
	 * fit, the string is truncated to fit the width
	 *
	 * @param gl
	 * @param text
	 * @param xPosition
	 *            x of lower left corner
	 * @param yPosition
	 *            y of lower left corner
	 * @param zPositon
	 * @param width
	 *            width fo the bounding box
	 * @param height
	 *            height of the bounding box
	 */
	public void renderTextInBounds(GL2 gl, String text, float x, float y, float z, float w, float h);

	public boolean isOriginTopLeft();
}
