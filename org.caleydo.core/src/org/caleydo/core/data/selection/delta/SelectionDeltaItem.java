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
	private int secondaryID = -1;
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
	 * Constructor. This constructor allows to specify the optional internal id in the selection
	 * 
	 * @param selectionID
	 *            the id of the selected element
	 * @param selectionType
	 *            the type of the selection
	 * @param internalID
	 *            the internal id which maps to the selectionID
	 */
	public SelectionDeltaItem(int selectionID, SelectionType selectionType, int internalID) {
		this(selectionID, selectionType);
		this.secondaryID = internalID;
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
	public int getPrimaryID() {
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
	 * Returns the internal id, which must not be set. Returns -1 if no internal id was set
	 * 
	 * @return the internal id
	 */
	public int getSecondaryID() {
		return secondaryID;
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
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new IllegalStateException(
				"Something went wrong with the cloning, caught CloneNotSupportedException");
		}
	}

	@Override
	public void setPrimaryID(int iPrimaryID) {
		this.primaryID = iPrimaryID;
	}

	@Override
	public void setSecondaryID(int iSecondaryID) {
		this.secondaryID = iSecondaryID;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public boolean isRemove() {
		return remove;
	}

	@Override
	public String toString() {
		return "[Pri: " + primaryID + " Type: " + selectionType + " Remove: "+ remove + "]";
	}

}