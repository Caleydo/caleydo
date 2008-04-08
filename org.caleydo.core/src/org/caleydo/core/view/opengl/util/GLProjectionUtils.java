package org.caleydo.core.view.opengl.util;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


public class GLProjectionUtils {

	public static void orthogonalStart(final GL gl, 
			final float fWindowWidth, final float fWindowHeight) {

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		GLU glu = new GLU();
		glu.gluOrtho2D(0, fWindowWidth, 0, fWindowHeight);
		gl.glScalef(1, -1, 1);
		gl.glTranslatef(0, -fWindowHeight, 0);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	public static void orthogonalEnd(final GL gl) {
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}
}
