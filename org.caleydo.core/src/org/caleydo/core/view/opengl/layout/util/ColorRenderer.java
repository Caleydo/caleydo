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

	private float[] color;

	/**
	 * Constructor.
	 * 
	 * @param color
	 *            Color of the rendered rectangle. The array must have a length of 4 specifying the RGBA
	 *            values of the color.
	 */
	public ColorRenderer(float[] color) {
		this.color = color;
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

	}
}
