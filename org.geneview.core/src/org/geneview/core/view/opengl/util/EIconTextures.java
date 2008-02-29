package org.geneview.core.view.opengl.util;


public enum EIconTextures
{
	MOVE_AXIS_LEFT ("resources/icons/go-previous.png"),
	MOVE_AXIS_RIGHT ("resources/icons/go-next.png"),
	REMOVE_AXIS ("resources/icons/emblem-unreadable.png"),
	DUPLICATE_AXIS ("resources/icons/edit-copy.png");
	
	private String sFileName;
	
	EIconTextures(String sFileName)
	{
		this.sFileName = sFileName;		
	
	}
	
	public String getFileName()
	{
		return sFileName;
	}
}