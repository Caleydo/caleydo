package org.caleydo.core.data.selection;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A SelectionItem represents one selection in the framework. It holds the id of
 * the selected element, the type of the selection as defined in
 * {@link ESelectionType} and optionally an internal selection ID
 * 
 * @author Alexander
 * 
 */
public class SelectionItem
{
	private int iSelectionID = -1;
	private ESelectionType selectionType;
	private int iInternalID = -1;
	private ArrayList<Integer> alConnectionID;

	/**
	 * Constructor
	 * 
	 * @param iSelectionID the id of the selected element
	 * @param selectionType the type of the selection
	 */
	public SelectionItem(int iSelectionID, ESelectionType selectionType)
	{
		this.iSelectionID = iSelectionID;
		this.selectionType = selectionType;
		alConnectionID = new ArrayList<Integer>();
	}

	/**
	 * Constructor. This constructor allows to specify the optional internal id
	 * in the selection
	 * 
	 * @param iSelectionID the id of the selected element
	 * @param selectionType the type of the selection
	 * @param iInternalID the internal id which maps to the selectionID
	 */
	public SelectionItem(int iSelectionID, ESelectionType selectionType, int iInternalID)
	{
		this(iSelectionID, selectionType);
		this.iInternalID = iInternalID;
		alConnectionID = new ArrayList<Integer>();
	}

	/**
	 * Set a connection ID which is meant to be persistent over conversion steps
	 * 
	 * @param iConnectionID the new id
	 */
	public void setConnectionID(int iConnectionID)
	{
		alConnectionID.add(iConnectionID);
	}

	/**
	 * Returns the selection ID
	 * 
	 * @return the selection ID
	 */
	public int getSelectionID()
	{
		return iSelectionID;
	}

	/**
	 * Returns the selection type
	 * 
	 * @return the selection type
	 */
	public ESelectionType getSelectionType()
	{
		return selectionType;
	}

	/**
	 * Returns the internal id, which must not be set. Returns -1 if no internal
	 * id was set
	 * 
	 * @return the internal id
	 */
	public int getInternalID()
	{
		return iInternalID;
	}

	/**
	 * Returns the connection ID of the element.
	 * 
	 * @return the connection ID
	 */
	public Collection<Integer> getConnectionID()
	{
		return alConnectionID;
	}

	/**
	 * Set the selection type
	 * 
	 * @param selectionType the selection type
	 */
	public void setSelectionType(ESelectionType selectionType)
	{
		this.selectionType = selectionType;
	}
}