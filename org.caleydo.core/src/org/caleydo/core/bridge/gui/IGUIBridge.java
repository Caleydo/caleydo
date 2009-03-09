package org.caleydo.core.bridge.gui;

public interface IGUIBridge {
	public void closeApplication();

	// public void setActiveGLView();

	// public void setActiveGLSubView(AGLEventListener parentGLEventListener,
	// AGLEventListener subGLEventListener);

	public void setShortInfo(String sMessage);

	public void setFileNameCurrentDataSet(String sFileName);

	public String getFileNameCurrentDataSet();
}
