package org.geneview.core.view.opengl.miniview;

import javax.media.opengl.GL;


public abstract class AGLMiniView 
{
	protected float fHeight;
	protected float fWidth;
	
	public abstract void render(GL gl, float fXOrigin, float fYOrigin);
	
	public final float getWidth()
	{
		return fWidth;
	}
	
	public final float getHeight()
	{
		return fHeight;
	}
	
}
