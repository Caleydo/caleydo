package org.geneview.core.view.opengl.util;


public enum EIconTextures
{
	ARROW_LEFT ("resources/icons/arrow-left.png"),
	ARROW_RIGHT ("resources/icons/arrow-right.png"),
	ARROW_UP ("resources/icons/arrow-up.png"),
	ARROW_DOWN ("resources/icons/arrow-down.png"),
	REMOVE ("resources/icons/remove.png"),
	LOCK ("resources/icons/lock.png"),
	DUPLICATE ("resources/icons/edit-copy.png"),
	POLYLINE_TO_AXIS ("resources/icons/transform-rotate.png"),
	PREVENT_OCCLUSION ("resources/icons/selection-exclude.png"),
	RENDER_SELECTION ("resources/icons/render-selection.png"),
	RESET_SELECTIONS ("resources/icons/edit-delete.png"),
	SEARCH_PATHWAY ("resources/icons/arrow-right.png"),
	SAVE_SELECTIONS ("resources/icons/document-save.png"),
	ANGULAR_BRUSHING ("resources/icons/angle.png");
	
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