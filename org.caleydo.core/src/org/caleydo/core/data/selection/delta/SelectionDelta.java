/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.selection.delta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDType;

/**
 * HashMap based implementation of IDelta for selections. All elements are unique and no ordering is
 * preserved.
 * 
 * @author Alexander Lex
 */
@XmlType(name = "SelectionDelta")
public class SelectionDelta
	implements IDelta<SelectionDeltaItem>, Iterable<SelectionDeltaItem> {

	private HashMap<Integer, SelectionDeltaItem> selectionItems = null;

	private IDType idType;

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

	@Override
	public Collection<SelectionDeltaItem> getAllItems() {
		return selectionItems.values();
	}

	/**
	 * Add a new selection to the delta. Notice that a selection delta allows only unique ids.
	 * 
	 * @param iSelectionID
	 *            the selection id
	 * @param selectionType
	 *            the selection type
	 */
	public SelectionDeltaItem addSelection(Integer selectionID, SelectionType selectionType) {

		if (selectionType.equals(SelectionType.NORMAL)) {
			throw new IllegalStateException("Cann not add a selection to NORMAL");
		}
		SelectionDeltaItem item = new SelectionDeltaItem(selectionID, selectionType);
		selectionItems.put(selectionID, item);
		return item;
	}

	/**
	 * Stores a selectionDeltaItem in the delta that triggers a removal of a particular element from a
	 * selection type in the receiving selection manager
	 * 
	 * @param selectionID
	 *            the element to be removed
	 * @param selectionType
	 *            the selection type from which the element should be removed
	 * @return the {@link SelectionDeltaItem} that reflects this operation
	 */
	public SelectionDeltaItem removeSelection(Integer selectionID, SelectionType selectionType) {
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
	public int size() {
		return selectionItems.size();
	}

	@Override
	public SelectionDelta clone() {
		SelectionDelta newDelta = new SelectionDelta(idType);
		for (SelectionDeltaItem item : selectionItems.values()) {
			SelectionDeltaItem newItem = newDelta.addSelection(item.getID(), item.getSelectionType());
			for (Integer iConnetionID : item.getConnectionIDs()) {
				newItem.addConnectionID(iConnetionID);
			}
		}

		return newDelta;
	}

	/**
	 * Add an ID for connections to the delta, based on a selection id that is already stored in a delta. This
	 * id is meant to be persistent across conversion processes
	 * 
	 * @param selectionID
	 *            the original selection id
	 * @param connectionID
	 *            the connection id
	 */
	public void addConnectionID(Integer selectionID, Integer connectionID) {
		SelectionDeltaItem item = selectionItems.get(selectionID);
		if (item == null)
			throw new IllegalStateException("Supplied selection ID is not in delta.");

		item.addConnectionID(connectionID);
	}

	/**
	 * Does the same as {@link #addConnectionID(Integer, Integer)} but for a bunch of connection ids at a
	 * time.
	 * 
	 * @param selectionID
	 * @param connectionIDs
	 */
	public void addConnectionIDs(Integer selectionID, Collection<Integer> connectionIDs) {
		SelectionDeltaItem item = selectionItems.get(selectionID);
		if (item == null)
			throw new IllegalStateException("Supplied selection ID is not in delta.");

		for (Integer connectionID : connectionIDs) {
			item.addConnectionID(connectionID);
		}
	}

	@Override
	public void add(SelectionDeltaItem deltaItem) {
		selectionItems.put(deltaItem.getID(), deltaItem);
	}

	@Override
	public IDType getIDType() {
		return idType;
	}

	@Override
	public void tableIDType(IDType idType) {
		this.idType = idType;
	}

	public HashMap<Integer, SelectionDeltaItem> getSelectionItems() {
		return selectionItems;
	}

	public void setSelectionItems(HashMap<Integer, SelectionDeltaItem> selectionItems) {
		this.selectionItems = selectionItems;
	}

	@Override
	public String toString() {

		String output = "Delta for " + idType + ", elements: ";
		for (Integer key : selectionItems.keySet()) {
			output = output + selectionItems.get(key);
		}

		return output;
	}

}
