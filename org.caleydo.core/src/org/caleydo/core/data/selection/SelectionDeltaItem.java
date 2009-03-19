package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A SelectionDeltaItem represents one selection in the framework. It holds the id of the selected element,
 * the type of the selection as defined in {@link ESelectionType} and optionally an internal selection ID
 * 
 * @author Alexander
 */
public class SelectionDeltaItem
	implements IDeltaItem {
	private int iPrimaryID = -1;
	private ESelectionType selectionType;
	private int iSecondaryID = -1;
	private ArrayList<Integer> alConnectionID;

	/**
	 * Constructor
	 * 
	 * @param iPrimaryID
	 *            the id of the selected element
	 * @param selectionType
	 *            the type of the selection
	 */
	public SelectionDeltaItem(int iSelectionID, ESelectionType selectionType) {
		this.iPrimaryID = iSelectionID;
		this.selectionType = selectionType;
		alConnectionID = new ArrayList<Integer>();
	}

	/**
	 * Constructor. This constructor allows to specify the optional internal id in the selection
	 * 
	 * @param iPrimaryID
	 *            the id of the selected element
	 * @param selectionType
	 *            the type of the selection
	 * @param iSecondaryID
	 *            the internal id which maps to the selectionID
	 */
	public SelectionDeltaItem(int iSelectionID, ESelectionType selectionType, int iInternalID) {
		this(iSelectionID, selectionType);
		this.iSecondaryID = iInternalID;
		alConnectionID = new ArrayList<Integer>();
	}

	/**
	 * Set a connection ID which is meant to be persistent over conversion steps
	 * 
	 * @param iConnectionID
	 *            the new id
	 */
	public void setConnectionID(int iConnectionID) {
		alConnectionID.add(iConnectionID);
	}

	@Override
	public int getPrimaryID() {
		return iPrimaryID;
	}

	/**
	 * Returns the selection type
	 * 
	 * @return the selection type
	 */
	public ESelectionType getSelectionType() {
		return selectionType;
	}

	/**
	 * Returns the internal id, which must not be set. Returns -1 if no internal id was set
	 * 
	 * @return the internal id
	 */
	public int getSecondaryID() {
		return iSecondaryID;
	}

	/**
	 * Returns the connection ID of the element.
	 * 
	 * @return the connection ID
	 */
	public Collection<Integer> getConnectionID() {
		return alConnectionID;
	}

	/**
	 * Set the selection type
	 * 
	 * @param selectionType
	 *            the selection type
	 */
	public void setSelectionType(ESelectionType selectionType) {
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
		this.iPrimaryID = iPrimaryID;
	}

	@Override
	public void setSecondaryID(int iSecondaryID) {
		this.iSecondaryID = iSecondaryID;
	}
}