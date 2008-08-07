package org.caleydo.core.manager.data.virtualarray;

import org.caleydo.core.data.selection.IVirtualArray;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;

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

	public VirtualArrayManager(final IGeneralManager generalManager, final int iUniqueId_type_offset,
			final EManagerType managerType)
	{
		super(generalManager, iUniqueId_type_offset, managerType);
	}

	@Override
	public IVirtualArray createVirtualArray(
			EManagerObjectType useSelectionType)
	{
		// TODO Auto-generated method stub
		return null;
	}


}
