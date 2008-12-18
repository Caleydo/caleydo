package org.caleydo.core.data.selection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.caleydo.core.data.mapping.EIDType;

/**
 * HashMap based implementation of ISelectionDelta
 * 
 * @author Alexander Lex
 * 
 */

public class SelectionDelta
	implements ISelectionDelta, Iterable<SelectionItem>
{
	private HashMap<Integer, SelectionItem> hashSelectionItems = null;

	private EIDType idType;
	private EIDType internalIDType = null;

	public SelectionDelta(EIDType idType)
	{
		hashSelectionItems = new HashMap<Integer, SelectionItem>();
		this.idType = idType;
	}

	public SelectionDelta(EIDType idType, EIDType internalIDType)
	{
		this(idType);
		this.internalIDType = internalIDType;
	}

	@Override
	public Collection<SelectionItem> getSelectionData()
	{
		return hashSelectionItems.values();
	}

	@Override
	public SelectionItem addSelection(int iSelectionID, ESelectionType selectionType)
	{

		SelectionItem item = new SelectionItem(iSelectionID, selectionType);
		hashSelectionItems.put(iSelectionID, item);
		return item;
		// if (item != null)
		// System.out.println("ID: " + iSelectionID + " Old: " +
		// item.getSelectionType() + " New: " + selectionType);
		//		
	}

	@Override
	public Iterator<SelectionItem> iterator()
	{
		return hashSelectionItems.values().iterator();
	}

	@Override
	public SelectionItem addSelection(int selectionID, ESelectionType selectionType,
			int iInternalID)
	{
		SelectionItem item = new SelectionItem(selectionID, selectionType, iInternalID);
		hashSelectionItems.put(selectionID, item);
		return item;
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
		return hashSelectionItems.size();
	}

	@Override
	public ISelectionDelta clone()
	{
		ISelectionDelta newDelta = new SelectionDelta(idType, internalIDType);
		for (SelectionItem item : hashSelectionItems.values())
		{
			SelectionItem newItem = newDelta.addSelection(item.getSelectionID(), item
					.getSelectionType(), item.getInternalID());
			for (Integer iConnetionID : item.getConnectionID())
			{
				newItem.setConnectionID(iConnetionID);
			}
		}

		return newDelta;
	}

	public void addConnectionID(int iSelectionID, int iConnectionID)
	{
		SelectionItem item = hashSelectionItems.get(iSelectionID);
		if (item == null)
			throw new IllegalStateException("Supplied selection ID is not in delta.");

		item.setConnectionID(iConnectionID);
	}

}
