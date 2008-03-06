package org.geneview.core.view.opengl.util;


public enum EIconTextures
{
	ARROW_LEFT ("resources/icons/go-previous.png"),
	ARROW_RIGHT ("resources/icons/go-next.png"),
	REMOVE ("resources/icons/emblem-unreadable.png"),
	DUPLICATE ("resources/icons/edit-copy.png"),
	POLYLINE_TO_AXIS ("resources/icons/transform-rotate.png"),
	PREVENT_OCCLUSION ("resources/icons/selection-exclude.png"),
	RENDER_SELECTION ("resources/icons/render-selection.png"),
	RESET_SELECTIONS ("resources/icons/view-refresh.png"),
	SEARCH_PATHWAY ("resources/icons/go-next.png");
	
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