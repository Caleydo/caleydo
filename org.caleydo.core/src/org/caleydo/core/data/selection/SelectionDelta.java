package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Iterator;
import org.caleydo.core.data.mapping.EIDType;

public class SelectionDelta
	implements ISelectionDelta, Iterable<SelectionItem>
{
	private ArrayList<SelectionItem> alSelectionItems = null;

	private EIDType idType;
	private EIDType internalIDType = null;

	public SelectionDelta(EIDType idType)
	{
		alSelectionItems = new ArrayList<SelectionItem>();
		this.idType = idType;
	}

	public SelectionDelta(EIDType idType, EIDType internalIDType)
	{
		this(idType);
		this.internalIDType = internalIDType;
	}

	@Override
	public ArrayList<SelectionItem> getSelectionData()
	{
		return alSelectionItems;
	}

	@Override
	public void addSelection(int iSelectionID, ESelectionType selectionType)
	{
		alSelectionItems.add(new SelectionItem(iSelectionID, selectionType));
	}

	@Override
	public Iterator<SelectionItem> iterator()
	{
		return alSelectionItems.iterator();
	}

	@Override
	public void addSelection(int selectionID, ESelectionType selectionType, int internalID)
	{
		alSelectionItems.add(new SelectionItem(selectionID, selectionType, internalID));
	}

	@Override
	public EIDType getIDType()
	{
		return idType;
	}

	@Override
	public EIDType getInternalIDType()
	{
		return internalIDType;
	}

	@Override
	public int size()
	{
		return alSelectionItems.size();
	}

	@Override
	public ISelectionDelta clone()
	{
		ISelectionDelta newDelta = new SelectionDelta(idType, internalIDType);
		for (SelectionItem item : alSelectionItems)
		{
			newDelta.addSelection(item.getSelectionID(), item.getSelectionType(), item
					.getInternalID());
		}
		return newDelta;
	}

}
