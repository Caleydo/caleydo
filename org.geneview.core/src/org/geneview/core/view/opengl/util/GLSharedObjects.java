package org.geneview.core.view.opengl.util;

import javax.media.opengl.GL;

/**
 * 
 * Class contains GL commands for rendering 
 * GL objects of common interest (like axis, etc.)
 * 
 * @author Marc Streit
 *
 */
public class GLSharedObjects {
	
	private static void drawAxis(final GL gl) {
		
		gl.glLineWidth(10);
	    gl.glBegin(GL.GL_LINES);
	    gl.glColor4f(1, 0, 0, 1);
	    gl.glVertex3f(0,  0,  0);
	    gl.glVertex3f(1,  0,  0);
	    gl.glColor4f(0, 1, 0, 1);
	    gl.glVertex3f( 0,  0,  0);
	    gl.glVertex3f( 0, 1,  0);
	    gl.glColor4f(0, 0, 1, 1);
	    gl.glVertex3f( 0,  0,  0);
	    gl.glVertex3f( 0,  0, 1);
	    gl.glEnd();
	}
}
