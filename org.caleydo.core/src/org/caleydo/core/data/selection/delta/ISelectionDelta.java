package org.caleydo.core.data.selection.delta;

import java.util.Collection;

import org.caleydo.core.data.selection.SelectionType;

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
	 *            the selection id
	 * @param selectionType
	 *            the selection type
	 */
	public SelectionDeltaItem addSelection(int iSelectionID, SelectionType selectionType);

	/**
	 * Add a new selection to the delta, including the optional internal id
	 * 
	 * @param iSelectionID
	 *            the selection id
	 * @param selectionType
	 *            the selection type
	 * @param iInternalID
	 *            the internal id
	 * @return the {@link SelectionDeltaItem} that reflects this operation
	 */
	public SelectionDeltaItem addSelection(int iSelectionID, SelectionType selectionType, int iInternalID);

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
	public SelectionDeltaItem removeSelection(int selectionID, SelectionType selectionType);

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
	 *            the original selection id
	 * @param iConnectionID
	 *            the connection id
	 */
	public void addConnectionID(int iSelectionID, int iConnectionID);

	/**
	 * Does the same as {@link #addConnectionID(int, int)} but for a bunch of connection ids at a time.
	 * 
	 * @param iSelectionID
	 * @param connectionIDs
	 */
	public void addConnectionIDs(int iSelectionID, Collection<Integer> connectionIDs);

}
