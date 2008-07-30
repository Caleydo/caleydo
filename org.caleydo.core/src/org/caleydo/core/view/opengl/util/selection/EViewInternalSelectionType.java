package org.caleydo.core.view.opengl.util.selection;

public enum EViewInternalSelectionType
{
	NORMAL("NORMAL"), SELECTION("SELECTION"), MOUSE_OVER("MOUSE_OVER"), DESELECTED(
			"DESELECTED");

	private String sType;

	// private static ArrayList<String> alSelectionType;

	EViewInternalSelectionType(String sType)
	{

		this.sType = sType;
	}

	public String getString()
	{

		return sType;
	}

}
