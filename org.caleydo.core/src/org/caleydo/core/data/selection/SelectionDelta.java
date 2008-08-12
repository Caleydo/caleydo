package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Iterator;

public class SelectionDelta
	implements ISelectionDelta, Iterable<SelectionItem>
{
	private ArrayList<SelectionItem> alSelectionItems = null;

	public SelectionDelta()
	{
		alSelectionItems = new ArrayList<SelectionItem>();
	}

	@Override
	public ArrayList<SelectionItem> getSelectionData()
	{
		return alSelectionItems;
	}

	@Override
	public void addSelection(int iSelectionID, int iSelectionType)
	{
		alSelectionItems.add(new SelectionItem(iSelectionID, iSelectionType));
	}

	@Override
	public Iterator<SelectionItem> iterator()
	{
		return alSelectionItems.iterator();
	}

}
