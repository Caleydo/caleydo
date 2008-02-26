package org.geneview.core.view.opengl.miniview;

import javax.media.opengl.GL;

import org.geneview.core.data.GeneralRenderStyle;


public class AGLParCoordsMiniView 
extends AGLMiniView
{

	public AGLParCoordsMiniView()
	{
		fWidth = 0.2f;
		fHeight = 0.2f;
	}
	
	public void render(GL gl, float fXOrigin, float fYOrigin)
	{		
		gl.glPushAttrib(GL.GL_CURRENT_BIT);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(fXOrigin, fYOrigin, GeneralRenderStyle.MINIVEW_Z);
		gl.glVertex3f(fXOrigin, fYOrigin + fHeight, GeneralRenderStyle.MINIVEW_Z);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin + fHeight, GeneralRenderStyle.MINIVEW_Z);
		gl.glVertex3f(fXOrigin + fWidth, fYOrigin, GeneralRenderStyle.MINIVEW_Z);
		gl.glEnd();
		gl.glPopAttrib();
	}
	
}
