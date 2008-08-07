package org.caleydo.core.manager.data.virtualarray;

import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Singleton that manages all virtual arrays.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public class VirtualArrayManager 
extends AManager<IVirtualArray>
implements IVirtualArrayManager
{
	@Override
	public IVirtualArray createVirtualArray(
			EManagedObjectType useSelectionType)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
