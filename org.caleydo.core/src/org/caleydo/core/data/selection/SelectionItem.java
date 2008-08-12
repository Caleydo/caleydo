package org.caleydo.core.data.selection;

/**
 * Simple mapping of two ints (like C++ STL Pairs) in this case for a selection
 * id and a selection type.
 * 
 * @author Alexander
 * 
 */
public class SelectionItem
{
	private int iSelectionID = 0;
	private int iSelectionType = 0;

	public SelectionItem(int iSelectionID, int iSelectionType)
	{
		this.iSelectionID = iSelectionID;
		this.iSelectionType = iSelectionType;
	}

	public int getSelectionID()
	{
		return iSelectionID;
	}

	public int getSelectionType()
	{
		return iSelectionType;
	}

//	public void setSelectionType(int iSelectionType)
//	{
//		this.iSelectionType = iSelectionType;
//	}

}
