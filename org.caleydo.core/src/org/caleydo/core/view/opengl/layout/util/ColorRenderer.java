package org.caleydo.core.view.opengl.layout.util;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;

/**
 * Simple renderer for a colored rectangle.
 * 
 * @author Christian Partl
 */
public class ColorRenderer
	extends LayoutRenderer {

	protected float[] color;
	protected float[] borderColor;
	protected int borderWidth;
	protected boolean drawBorder;

	/**
	 * Constructor.
	 * 
	 * @param color
	 *            Color of the rendered rectangle. The array must have a length of 4 specifying the RGBA
	 *            values of the color.
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
	 *            Color of the rendered rectangle. The array must have a length of 4 specifying the RGBA
	 *            values of the color.
	 * @param borderColor
	 *            Color of the rendered rectangle's border. The array must have a length of 4 specifying the
	 *            RGBA values of the color.
	 * @param borderWidth
	 *            Width of the rendered rectangle's border.
	 */
	public ColorRenderer(float[] color, float[] borderColor, int borderWidth) {
		this.color = color;
		this.borderColor = borderColor;
		this.borderWidth = borderWidth;
		drawBorder = true;
	}

	@Override
	public void render(GL2 gl) {

		gl.glColor4fv(color, 0);
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
