package org.caleydo.core.data.selection;

import java.util.ArrayList;
import org.caleydo.core.data.mapping.EIDType;

/**
 * Interface for Selection Deltas as they are being sent between views
 * 
 * @author Alexander Lex
 */
public interface ISelectionDelta
	extends Iterable<SelectionItem>
{
	/**
	 * Return an array list of {@link SelectionItem}. This contains data on what
	 * selections have changed
	 * 
	 * @return
	 */
	public ArrayList<SelectionItem> getSelectionData();

	/**
	 * Add a new selection to the delta
	 * 
	 * @param iSelectionID the selection id
	 * @param selectionType the selection type
	 */
	public void addSelection(int iSelectionID, ESelectionType selectionType);

	/**
	 * Add a new selection to the delta, including the optional internal id
	 * 
	 * @param iSelectionID the selection id
	 * @param selectionType the selection type
	 * @param iInternalID the internal id
	 */
	public void addSelection(int iSelectionID, ESelectionType selectionType, int iInternalID);

	/**
	 * Get the type of the id, which has to be listed in {@link EIDType}
	 * 
	 * @return the type of the id
	 */
	public EIDType getIDType();

	/**
	 * Get the type of the internal ID, which has to be listed in
	 * {@link EIDType}. Returns null if no internal ID type was set
	 * 
	 * @return the type of the internal id
	 */
	public EIDType getInternalIDType();
	
	/**
	 * Returns the number of elements in the selection delta
	 * @return the size
	 */
	public int size();
	
	/**
	 * Returns a deep copy of the selection delta
	 * @return the new selection delta, copy of the old one
	 */
	public ISelectionDelta clone();

}
