package org.caleydo.core.view.opengl.miniview;

import javax.media.opengl.GL;

import org.caleydo.core.data.view.rep.renderstyle.GeneralRenderStyle;


public class GLParCoordsMiniView 
extends AGLMiniView
{

	public GLParCoordsMiniView()
	{
		fWidth = 0.2f;
		fHeight = 0.2f;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.opengl.miniview.AGLMiniView#render(javax.media.opengl.GL, float, float, float)
	 */
	public void render(GL gl, float fXOrigin, float fYOrigin, float fZOrigin) {
	
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
