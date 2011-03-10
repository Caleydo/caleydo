package org.caleydo.view.visbricks.brick;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public class BackGroundRenderer extends LayoutRenderer {

	@Override
	public void render(GL2 gl) {
		
		gl.glColor3f(1, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(x, 0, 0);
		gl.glVertex3f(x, y, 0);
		gl.glVertex3f(0, y, 0);
		gl.glEnd();

	}
}
