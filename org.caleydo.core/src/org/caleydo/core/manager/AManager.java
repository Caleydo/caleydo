package org.caleydo.core.manager;

import java.util.Collection;
import java.util.HashMap;
import org.caleydo.core.manager.general.GeneralManager;

/**
 * Base class for manager classes.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AManager<T>
	implements IManager<T>
{
	protected IGeneralManager generalManager;
	
	protected HashMap<Integer, T> hashItems;

	/**
	 * Constructor.
	 */
	protected AManager()
	{
		generalManager = GeneralManager.get();
		
		hashItems = new HashMap<Integer, T>();
	}
	
	@Override
	public T getItem(int iItemID)
	{
		return hashItems.get(iItemID);
	}

	@Override
	public boolean hasItem(int iItemID)
	{
		return hashItems.containsKey(iItemID);
	}

	public void registerItem(final T item, final int iItemID)
	{
		hashItems.put(iItemID, item);
	}

	@Override
	public int size()
	{
		return hashItems.size();
	}

	@Override
	public void unregisterItem(int iItemID)
	{
		hashItems.remove(iItemID);		
	}
	
	@Override
	public Collection<T> getAllItems()
	{
		return hashItems.values();
	}
	
}
