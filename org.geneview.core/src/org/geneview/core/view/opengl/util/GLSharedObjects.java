package org.geneview.core.view.opengl.util;

import javax.media.opengl.GL;

import org.geneview.core.data.view.camera.IViewFrustum;

/**
 * 
 * Class contains GL commands for rendering 
 * GL objects of common interest (like axis, etc.)
 * 
 * @author Marc Streit
 *
 */
public class GLSharedObjects {
	
	public static void drawAxis(final GL gl) {
		
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
	
	public static void drawViewFrustum(final GL gl, final IViewFrustum viewFrustum)
	{
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getTop(), 0);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getTop(), 0);
		gl.glVertex3f(viewFrustum.getRight(), viewFrustum.getBottom(), 0);
		gl.glVertex3f(viewFrustum.getLeft(), viewFrustum.getBottom(), 0);
		gl.glEnd();
	}
}
