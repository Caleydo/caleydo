package org.caleydo.core.view.opengl.util;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class GLProjectionUtils {

	public static void orthogonalStart(final GL2 gl, final float fWindowWidth, final float fWindowHeight) {

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		GLU glu = new GLU();
		glu.gluOrtho2D(0, fWindowWidth, 0, fWindowHeight);
		gl.glScalef(1, -1, 1);
		gl.glTranslatef(0, -fWindowHeight, 0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	public static void orthogonalEnd(final GL2 gl) {

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}
}
