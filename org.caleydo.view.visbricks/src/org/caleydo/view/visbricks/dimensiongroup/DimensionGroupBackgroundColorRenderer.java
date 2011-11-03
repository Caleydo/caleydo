package org.caleydo.view.visbricks.dimensiongroup;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.util.ColorRenderer;

public class DimensionGroupBackgroundColorRenderer extends ColorRenderer {

	public DimensionGroupBackgroundColorRenderer(float[] color) {
		super(color);
	}

	@Override
	public void render(GL2 gl) {

		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(color[0], color[1], color[2], 0);
		gl.glVertex3f(-x / 3.0f, 0, 0);
		gl.glColor4f(color[0], color[1], color[2], 1);
		gl.glVertex3f(x / 3.0f, 0, 0);
		gl.glVertex3f(x / 3.0f, y, 0);
		gl.glColor4f(color[0], color[1], color[2], 0);
		gl.glVertex3f(-x / 3.0f, y, 0);

		gl.glColor4f(color[0], color[1], color[2], 1);
		gl.glVertex3f(x / 3.0f, 0, 0);
		gl.glVertex3f(2.0f * x / 3.0f, 0, 0);
		gl.glVertex3f(2.0f * x / 3.0f, y, 0);
		gl.glVertex3f(x / 3.0f, y, 0);

		gl.glVertex3f(2.0f * x / 3.0f, 0, 0);
		gl.glColor4f(color[0], color[1], color[2], 0);
		gl.glVertex3f(x + x / 3.0f, 0, 0);
		gl.glVertex3f(x + x / 3.0f, y, 0);
		gl.glColor4f(color[0], color[1], color[2], 1);
		gl.glVertex3f(2.0f * x / 3.0f, y, 0);

		gl.glEnd();

	}

}
