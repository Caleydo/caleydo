package org.caleydo.core.manager.data.set;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.type.EManagerType;

/**
 * Manage the sets
 * 
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
public class SetManager
	extends AManager<ISet>
	implements ISetManager
{

	/**
	 * Constructor.
	 * 
	 * @param the general manager
	 */
	public SetManager(IGeneralManager setSingelton)
	{
		super(setSingelton, IGeneralManager.iUniqueId_TypeOffset_Set, EManagerType.DATA_SET);

	
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.ISetManager#createSet(org.caleydo.core.
	 * data.collection.ESetType)
	 */
	@Override
	public ISet createSet(final ESetType setType)
	{
		return new Set(4, generalManager);
	}


	
	

	
}
