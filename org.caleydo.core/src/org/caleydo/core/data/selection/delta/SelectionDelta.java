package org.caleydo.core.data.selection.delta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectionType;

/**
 * HashMap based implementation of ISelectionDelta. Therefore all elements are unique and no ordering is
 * preserved.
 * 
 * @author Alexander Lex
 */
@XmlType(name = "SelectionDelta")
public class SelectionDelta
	implements ISelectionDelta, Iterable<SelectionDeltaItem> {

	private HashMap<Integer, SelectionDeltaItem> selectionItems = null;

	private IDType idType;
	private IDType secondaryIDType = null;

	/**
	 * Default Constructor.
	 */
	public SelectionDelta() {

	}

	public SelectionDelta(IDType idType) {
		if (idType == null)
			throw new IllegalArgumentException("idType was null");
		selectionItems = new HashMap<Integer, SelectionDeltaItem>();
		this.idType = idType;
	}

	public SelectionDelta(IDType idType, IDType internalIDType) {
		this(idType);

		this.secondaryIDType = internalIDType;
	}

	@Override
	public Collection<SelectionDeltaItem> getAllItems() {
		return selectionItems.values();
	}

	@Override
	public SelectionDeltaItem addSelection(int selectionID, SelectionType selectionType) {

		if (selectionType.equals(SelectionType.NORMAL)) {
			throw new IllegalStateException("Cann not add a selection to NORMAL");
		}
		SelectionDeltaItem item = new SelectionDeltaItem(selectionID, selectionType);
		selectionItems.put(selectionID, item);
		return item;
		// if (item != null)
		// System.out.println("ID: " + iSelectionID + " Old: " +
		// item.getSelectionType() + " New: " + selectionType);
		//
	}

	@Override
	public SelectionDeltaItem removeSelection(int selectionID, SelectionType selectionType) {
		SelectionDeltaItem item = new SelectionDeltaItem(selectionID, selectionType);
		item.setRemove(true);
		selectionItems.put(selectionID, item);
		return item;
	}

	@Override
	public Iterator<SelectionDeltaItem> iterator() {
		return selectionItems.values().iterator();
	}

	@Override
	public SelectionDeltaItem addSelection(int selectionID, SelectionType selectionType, int iSecondaryID) {
		SelectionDeltaItem item = new SelectionDeltaItem(selectionID, selectionType, iSecondaryID);
		selectionItems.put(selectionID, item);
		return item;
	}

	@Override
	public int size() {
		return selectionItems.size();
	}

	@Override
	public SelectionDelta clone() {
		SelectionDelta newDelta = new SelectionDelta(idType, secondaryIDType);
		for (SelectionDeltaItem item : selectionItems.values()) {
			SelectionDeltaItem newItem =
				newDelta.addSelection(item.getPrimaryID(), item.getSelectionType(), item.getSecondaryID());
			for (Integer iConnetionID : item.getConnectionIDs()) {
				newItem.addConnectionID(iConnetionID);
			}
		}

		return newDelta;
	}

	public void addConnectionID(int iSelectionID, int iConnectionID) {
		SelectionDeltaItem item = selectionItems.get(iSelectionID);
		if (item == null)
			throw new IllegalStateException("Supplied selection ID is not in delta.");

		item.addConnectionID(iConnectionID);
	}

	@Override
	public void addConnectionIDs(int iSelectionID, Collection<Integer> connectionIDs) {
		SelectionDeltaItem item = selectionItems.get(iSelectionID);
		if (item == null)
			throw new IllegalStateException("Supplied selection ID is not in delta.");

		for (Integer connectionID : connectionIDs) {
			item.addConnectionID(connectionID);
		}
	}

	@Override
	public void add(SelectionDeltaItem deltaItem) {
		selectionItems.put(deltaItem.getPrimaryID(), deltaItem);
	}

	@Override
	public IDType getIDType() {
		return idType;
	}

	public void setIDType(IDType idType) {
		this.idType = idType;
	}

	@Override
	public IDType getSecondaryIDType() {
		return secondaryIDType;
	}

	public void setSecondaryIDType(IDType secondaryIDType) {
		this.secondaryIDType = secondaryIDType;
	}

	public HashMap<Integer, SelectionDeltaItem> getSelectionItems() {
		return selectionItems;
	}

	public void setSelectionItems(HashMap<Integer, SelectionDeltaItem> selectionItems) {
		this.selectionItems = selectionItems;
	}

	@Override
	public String toString() {
		String output = "";
		for (Integer key : selectionItems.keySet()) {
			output = output + selectionItems.get(key);
		}

		return output;
	}

}
