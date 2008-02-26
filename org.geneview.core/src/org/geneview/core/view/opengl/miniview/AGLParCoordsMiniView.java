package org.geneview.core.view.opengl.miniview;

import javax.media.opengl.GL;


public class AGLParCoordsMiniView 
extends AGLMiniView
{

	public void render(GL gl, float fXOrigin, float fYOrigin)
	{
		fWidth = 0.5f;
		fHeight = 0.5f;
		
		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXOrigin, fYOrigin, 0);
		gl.glVertex3f(fXOrigin, fYOrigin + 0.5f, 0);
		gl.glVertex3f(fXOrigin + 0.5f, fYOrigin + 0.5f, 0);
		gl.glVertex3f(fXOrigin + 0.5f, fYOrigin, 0);
		gl.glEnd();
		gl.glPopAttrib();
	}
	
}
