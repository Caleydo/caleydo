package org.caleydo.core.data.selection;

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

}
