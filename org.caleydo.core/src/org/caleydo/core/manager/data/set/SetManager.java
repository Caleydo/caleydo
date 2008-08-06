package org.caleydo.core.manager.data.set;

import java.util.Collection;
import java.util.Hashtable;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.type.EManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Manage the sets
 * 
 * @author Michael Kalkusch
 * @author Alexander Lex
 */
public class SetManager
	extends AManager
	implements ISetManager
{

	protected Hashtable<Integer, ISet> hashId2Set;

	/**
	 * Constructor.
	 * 
	 * @param the general manager
	 */
	public SetManager(IGeneralManager setSingelton)
	{
		super(setSingelton, IGeneralManager.iUniqueId_TypeOffset_Set, EManagerType.DATA_SET);

		hashId2Set = new Hashtable<Integer, ISet>();
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

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.data.ISetManager#removeSet(org.caleydo.core.
	 * data.collection.ISet)
	 */
	@Override
	public boolean removeSet(ISet deleteSet)
	{
		throw new RuntimeException("not impelemtned!");
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.ISetManager#removeSet(int)
	 */
	@Override
	public boolean removeSet(final int iItemId)
	{
		ISet removedObj = hashId2Set.remove(iItemId);

		if (removedObj == null)
		{
			// generalManager.logMsg(
			// "deleteSet(" +
			// iItemId + ") falied, because Set was not registered!",
			// LoggerType.STATUS );
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.ISetManager#getSet(int)
	 */
	@Override
	public ISet getSet(final int iItemId)
	{
		return hashId2Set.get(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#getItem(int)
	 */
	@Override
	public final Object getItem(final int iItemId)
	{
		return getSet(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.data.ISetManager#getAllSets()
	 */
	@Override
	public Collection<ISet> getAllSets()
	{
		return hashId2Set.values();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#hasItem(int)
	 */
	@Override
	public final boolean hasItem(int iItemId)
	{
		return hashId2Set.containsKey(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#size()
	 */
	@Override
	public final int size()
	{
		return hashId2Set.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#unregisterItem(int)
	 */
	@Override
	public boolean unregisterItem(final int iItemId)
	{
		ISet buffer = hashId2Set.remove(iItemId);

		if (buffer == null)
		{
			// this.generalManager.logMsg(
			// "unregisterItem(" +
			// iItemId + ") failed because Set was not registered!",
			// LoggerType.STATUS );
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IManager#registerItem(java.lang.Object,
	 * int)
	 */
	@Override
	public boolean registerItem(final Object registerItem, final int iItemId)
	{
		ISet addItem = (ISet) registerItem;

		if (this.hashId2Set.containsKey(iItemId))
		{
			return false;
		}

		hashId2Set.put(iItemId, addItem);

		return true;
	}
}
