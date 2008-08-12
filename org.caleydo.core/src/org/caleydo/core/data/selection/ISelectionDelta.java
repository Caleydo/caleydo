package org.caleydo.core.data.selection;

import java.util.ArrayList;

/**
 * Interface for Selection Deltas as they are beeing sent between views
 * 
 * @author Alexander Lex
 */
public interface ISelectionDelta
	extends Iterable<SelectionItem>
{
	public ArrayList<SelectionItem> getSelectionData();

	public void addSelection(int iSelectionID, int iSelectionType);

}
