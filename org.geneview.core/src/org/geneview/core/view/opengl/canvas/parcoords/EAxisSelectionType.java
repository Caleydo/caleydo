package org.geneview.core.view.opengl.canvas.parcoords;


public enum EAxisSelectionType
{
	NORMAL ("NORMAL"),
	SELECTION ("SELECTION"),
	MOUSE_OVER ("MOUSE_OVER");
	
	private String sType;
	//private static ArrayList<String> alSelectionType;
	
	EAxisSelectionType(String sType)
	{
		this.sType = sType;
	}
	
	public String getString()
	{
		return sType;
	}
	
}
