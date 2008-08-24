package org.caleydo.core.bridge.gui;

import org.caleydo.core.view.opengl.canvas.AGLEventListener;


public interface IGUIBridge
{
	public void closeApplication();
	
//	public void setActiveGLView();
	
	public void setActiveGLSubView(AGLEventListener parentGLEventListener,
			AGLEventListener subGLEventListener);
}
