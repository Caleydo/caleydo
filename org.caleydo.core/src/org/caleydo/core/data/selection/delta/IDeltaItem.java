package org.caleydo.core.data.selection.delta;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.virtualarray.VirtualArray;

/**
 * Interface for deltas, which describe changes made to either a {@link SelectionManager} or a
 * {@link VirtualArray}
 * 
 * @author Alexander Lex
 */
public interface IDeltaItem
	extends Cloneable {
	/**
	 * Returns the id of the item
	 */
	public int getID();

	public void setID(Integer elementID);

	public IDeltaItem clone();

}
