package org.caleydo.core.data.selection.delta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.mapping.EIDType;
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

	private EIDType idType;
	private EIDType secondaryIDType = null;

	/**
	 * Default Constructor.
	 */
	public SelectionDelta() {

	}

	public SelectionDelta(EIDType idType) {
		if (idType == null)
			throw new IllegalArgumentException("idType was null");
		selectionItems = new HashMap<Integer, SelectionDeltaItem>();
		this.idType = idType;
	}

	public SelectionDelta(EIDType idType, EIDType internalIDType) {
		this(idType);

		this.secondaryIDType = internalIDType;
	}

	@Override
	public Collection<SelectionDeltaItem> getAllItems() {
		return selectionItems.values();
	}

	@Override
	public SelectionDeltaItem addSelection(int iSelectionID, SelectionType selectionType) {

		SelectionDeltaItem item = new SelectionDeltaItem(iSelectionID, selectionType);
		selectionItems.put(iSelectionID, item);
		return item;
		// if (item != null)
		// System.out.println("ID: " + iSelectionID + " Old: " +
		// item.getSelectionType() + " New: " + selectionType);
		//		
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
	public EIDType getIDType() {
		return idType;
	}

	public void setIDType(EIDType idType) {
		this.idType = idType;
	}

	@Override
	public EIDType getSecondaryIDType() {
		return secondaryIDType;
	}

	public void setSecondaryIDType(EIDType secondaryIDType) {
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
