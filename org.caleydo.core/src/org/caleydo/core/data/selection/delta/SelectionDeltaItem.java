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

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.selection.SelectionType;

/**
 * A SelectionDeltaItem represents one selection in the framework. It holds the id of the selected element,
 * the type of the selection as defined in {@link SelectionType} and optionally an internal selection ID
 * 
 * @author Alexander
 */
@XmlType(name = "SelectionDeltaItem")
public class SelectionDeltaItem
	implements IDeltaItem {

	private int primaryID = -1;
	private SelectionType selectionType;
	private boolean remove = false;

	private ArrayList<Integer> connectionIDs;

	/**
	 * Default Constructor.
	 */
	public SelectionDeltaItem() {

	}

	/**
	 * Constructor
	 * 
	 * @param selectionID
	 *            the id of the selected element
	 * @param selectionType
	 *            the type of the selection
	 */
	public SelectionDeltaItem(int selectionID, SelectionType selectionType) {
		this.primaryID = selectionID;
		this.selectionType = selectionType;
		connectionIDs = new ArrayList<Integer>();
	}

	/**
	 * Set a connection ID which is meant to be persistent over conversion steps
	 * 
	 * @param iConnectionID
	 *            the new id
	 */
	public void addConnectionID(int iConnectionID) {
		connectionIDs.add(iConnectionID);
	}

	@Override
	public int getID() {
		return primaryID;
	}

	/**
	 * Returns the selection type
	 * 
	 * @return the selection type
	 */
	public SelectionType getSelectionType() {
		return selectionType;
	}

	/**
	 * Returns the connection ID of the element.
	 * 
	 * @return the connection ID
	 */
	@XmlElementWrapper
	public ArrayList<Integer> getConnectionIDs() {
		return connectionIDs;
	}

	public void setConnectionIDs(ArrayList<Integer> connectionIDs) {
		this.connectionIDs = connectionIDs;
	}

	/**
	 * Set the selection type
	 * 
	 * @param selectionType
	 *            the selection type
	 */
	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	@Override
	public SelectionDeltaItem clone() {
		try {
			return (SelectionDeltaItem) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new IllegalStateException(
				"Something went wrong with the cloning, caught CloneNotSupportedException");
		}
	}

	@Override
	public void setID(Integer iPrimaryID) {
		this.primaryID = iPrimaryID;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public boolean isRemove() {
		return remove;
	}

	@Override
	public String toString() {
		return "[Pri: " + primaryID + " Type: " + selectionType + " Remove: " + remove + "]";
	}

}