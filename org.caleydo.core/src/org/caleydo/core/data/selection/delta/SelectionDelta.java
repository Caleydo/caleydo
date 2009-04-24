package org.caleydo.core.data.selection.delta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;

/**
 * HashMap based implementation of ISelectionDelta. Therefore all elements are unique and no ordering is
 * preserved.
 * 
 * @author Alexander Lex
 */

public class SelectionDelta
	implements ISelectionDelta, Iterable<SelectionDeltaItem> {
	private HashMap<Integer, SelectionDeltaItem> hashSelectionItems = null;

	private EIDType idType;
	private EIDType secondaryIDType = null;

	public SelectionDelta(EIDType idType) {
		hashSelectionItems = new HashMap<Integer, SelectionDeltaItem>();
		this.idType = idType;
	}

	public SelectionDelta(EIDType idType, EIDType internalIDType) {
		this(idType);
		this.secondaryIDType = internalIDType;
	}

	@Override
	public Collection<SelectionDeltaItem> getAllItems() {
		return hashSelectionItems.values();
	}

	@Override
	public SelectionDeltaItem addSelection(int iSelectionID, ESelectionType selectionType) {

		SelectionDeltaItem item = new SelectionDeltaItem(iSelectionID, selectionType);
		hashSelectionItems.put(iSelectionID, item);
		return item;
		// if (item != null)
		// System.out.println("ID: " + iSelectionID + " Old: " +
		// item.getSelectionType() + " New: " + selectionType);
		//		
	}

	@Override
	public Iterator<SelectionDeltaItem> iterator() {
		return hashSelectionItems.values().iterator();
	}

	@Override
	public SelectionDeltaItem addSelection(int selectionID, ESelectionType selectionType, int iSecondaryID) {
		SelectionDeltaItem item = new SelectionDeltaItem(selectionID, selectionType, iSecondaryID);
		hashSelectionItems.put(selectionID, item);
		return item;
	}

	@Override
	public EIDType getIDType() {
		return idType;
	}

	@Override
	public EIDType getSecondaryIDType() {
		return secondaryIDType;
	}

	@Override
	public int size() {
		return hashSelectionItems.size();
	}

	@Override
	public ISelectionDelta clone() {
		ISelectionDelta newDelta = new SelectionDelta(idType, secondaryIDType);
		for (SelectionDeltaItem item : hashSelectionItems.values()) {
			SelectionDeltaItem newItem =
				newDelta.addSelection(item.getPrimaryID(), item.getSelectionType(), item.getSecondaryID());
			for (Integer iConnetionID : item.getConnectionID()) {
				newItem.setConnectionID(iConnetionID);
			}
		}

		return newDelta;
	}

	public void addConnectionID(int iSelectionID, int iConnectionID) {
		SelectionDeltaItem item = hashSelectionItems.get(iSelectionID);
		if (item == null)
			throw new IllegalStateException("Supplied selection ID is not in delta.");

		item.setConnectionID(iConnectionID);
	}

	@Override
	public void add(SelectionDeltaItem deltaItem) {
		hashSelectionItems.put(deltaItem.getPrimaryID(), deltaItem);
	}

}
