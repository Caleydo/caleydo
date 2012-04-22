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
package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;

/**
 * Simple renderer for a colored rectangle.
 * 
 * @author Christian Partl
 */
public class ColorRenderer extends APickableLayoutRenderer {

	protected float[] color;
	protected float[] borderColor;
	protected int borderWidth;
	protected boolean drawBorder;
	protected IColorProvider colorProvider;

	/**
	 * Constructor.
	 * 
	 * @param color
	 *            Color of the rendered rectangle. The array must have a length
	 *            of 4 specifying the RGBA values of the color.
	 */
	public ColorRenderer(float[] color) {
		this.color = color;
		borderColor = color;
		drawBorder = false;
	}

	/**
	 * Constructor.
	 * 
	 * @param color
	 *            Color of the rendered rectangle. The array must have a length
	 *            of 4 specifying the RGBA values of the color.
	 * @param borderColor
	 *            Color of the rendered rectangle's border. The array must have
	 *            a length of 4 specifying the RGBA values of the color.
	 * @param borderWidth
	 *            Width of the rendered rectangle's border.
	 */
	public ColorRenderer(float[] color, float[] borderColor, int borderWidth) {
		this.color = color;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		drawBorder = true;
	}

	public ColorRenderer(IColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}

	@Override
	public void render(GL2 gl) {

		pushNames(gl);

		gl.glColor4fv(colorProvider == null ? color : colorProvider.getColor(), 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(borderWidth);

		// gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glColor4fv(borderColor, 0);
		gl.glBegin(GL2.GL_LINES);

		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);

		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, y, 0);

		gl.glVertex3f(0, y, 0);
		gl.glVertex3f(x, y, 0);

		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);

		gl.glEnd();

		gl.glPopAttrib();

		popNames(gl);

	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	public void setBorderColor(float[] borderColor) {
		this.borderColor = borderColor;
	}

	public float[] getBorderColor() {
		return borderColor;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setDrawBorder(boolean drawBorder) {
		this.drawBorder = drawBorder;
	}

	public boolean isDrawBorder() {
		return drawBorder;
	}
}
