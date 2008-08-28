package org.caleydo.core.manager.data;

import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Manage all IVirtualArray's.
 * 
 * @author Alexander Lex
 * 
 */
public interface IVirtualArrayManager
	extends IManager<IVirtualArray>
{
	public IVirtualArray createVirtualArray(final EManagedObjectType useSelectionType);

}
