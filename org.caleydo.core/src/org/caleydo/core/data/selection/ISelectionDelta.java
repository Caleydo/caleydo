package org.caleydo.core.data.selection;

/**
 * Interface for Selection Deltas as they are being sent between views
 * 
 * @author Alexander Lex
 */
public interface ISelectionDelta
	extends IDelta<SelectionDeltaItem> {

	/**
	 * Add a new selection to the delta
	 * 
	 * @param iSelectionID
	 *          the selection id
	 * @param selectionType
	 *          the selection type
	 */
	public SelectionDeltaItem addSelection(int iSelectionID, ESelectionType selectionType);

	/**
	 * Add a new selection to the delta, including the optional internal id
	 * 
	 * @param iSelectionID
	 *          the selection id
	 * @param selectionType
	 *          the selection type
	 * @param iInternalID
	 *          the internal id
	 */
	public SelectionDeltaItem addSelection(int iSelectionID, ESelectionType selectionType, int iInternalID);

	/**
	 * Returns a deep copy of the selection delta
	 * 
	 * @return the new selection delta, copy of the old one
	 */
	public ISelectionDelta clone();

	/**
	 * Add an ID for connections to the delta, based on a selection id that is already stored in a delta. This
	 * id is meant to be persistent across conversion processes
	 * 
	 * @param iSelectionID
	 *          the original selection id
	 * @param iConnectionID
	 *          the connection id
	 */
	public void addConnectionID(int iSelectionID, int iConnectionID);

}
