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

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.AGLView;

/**
 * Simple renderer for a colored rectangle exactly of the size of the layout.
 *
 * @author Christian Partl
 */
public class ColorRenderer extends APickableLayoutRenderer {

	public static final float[] DEFAULT_COLOR = new float[] { 0, 0, 0, 1 };

	protected float[] color = DEFAULT_COLOR;
	/**
	 * A second color that is used to display a gradient together with
	 * {@link #color}.
	 */
	protected float[] gradientColor = DEFAULT_COLOR;
	protected float[] borderColor = DEFAULT_COLOR;
	protected int borderWidth;
	protected boolean drawBorder;
	protected IColorProvider colorProvider;
	/**
	 * Determines whether a gradient shall be shown.
	 */
	protected boolean useGradient = false;
	/**
	 * Determines whether the gradient is horizontal.
	 */
	protected boolean isHorizontalGradient = false;

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

	public ColorRenderer(float[] color, AGLView view) {
		this.view = view;
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

	public ColorRenderer(float[] color, float[] borderColor, int borderWidth,
			float[] gradientColor, boolean isHorizontalGradient) {
		this.color = color;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		drawBorder = true;
		this.gradientColor = gradientColor;
		useGradient = true;
	}

	public ColorRenderer(IColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
		setDisplayListDirty();
	}

	public void setBorderColor(float[] borderColor) {
		this.borderColor = borderColor;
		setDisplayListDirty();
	}

	public float[] getBorderColor() {
		return borderColor;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		setDisplayListDirty();
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setDrawBorder(boolean drawBorder) {
		this.drawBorder = drawBorder;
		setDisplayListDirty();
	}

	public boolean isDrawBorder() {
		return drawBorder;
	}

	/**
	 * @param gradientColor
	 *            setter, see {@link #gradientColor}
	 */
	public void setGradientColor(float[] gradientColor) {
		this.gradientColor = gradientColor;
		setDisplayListDirty();
	}

	/**
	 * @return the gradientColor, see {@link #gradientColor}
	 */
	public float[] getGradientColor() {
		return gradientColor;
	}

	/**
	 * @param isHorizontalGradient
	 *            setter, see {@link #isHorizontalGradient}
	 */
	public void setHorizontalGradient(boolean isHorizontalGradient) {
		this.isHorizontalGradient = isHorizontalGradient;
		setDisplayListDirty();
	}

	/**
	 * @return the isHorizontalGradient, see {@link #isHorizontalGradient}
	 */
	public boolean isHorizontalGradient() {
		return isHorizontalGradient;
	}

	/**
	 * @param showGradient
	 *            setter, see {@link #useGradient}
	 */
	public void setUseGradient(boolean useGradient) {
		this.useGradient = useGradient;
		setDisplayListDirty();
	}

	/**
	 * @return the showGradient, see {@link #useGradient}
	 */
	public boolean useGradient() {
		return useGradient;
	}

	@Override
	protected void renderContent(GL2 gl) {
		// TODO: make display lists usable.
		if (colorProvider != null) {
			color = colorProvider.getColor();
			useGradient = colorProvider.useGradient();
			gradientColor = colorProvider.getGradientColor();
			isHorizontalGradient = colorProvider.isHorizontalGradient();

		}

		pushNames(gl);

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4fv(color, 0);
		gl.glVertex3f(0, 0, 0);
		if (useGradient && isHorizontalGradient)
			gl.glColor4fv(gradientColor, 0);
		gl.glVertex3f(x, 0, 0);
		if (useGradient)
			gl.glColor4fv(gradientColor, 0);
		gl.glVertex3f(x, y, 0);
		if (useGradient && !isHorizontalGradient)
			gl.glColor4fv(gradientColor, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

		gl.glPushAttrib(GL2.GL_LINE_BIT);
		gl.glLineWidth(borderWidth);

		// gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glColor4fv(borderColor, 0);
		gl.glBegin(GL.GL_LINES);

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

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
