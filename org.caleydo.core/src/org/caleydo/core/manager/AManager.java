package org.caleydo.core.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Base class for manager classes, that connect to the IGeneralManager.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 */
public abstract class AManager<T>
	implements IManager<T>, Serializable
{

	private static final long serialVersionUID = 1L;

	protected final transient IGeneralManager generalManager;

	protected final EManagerType managerType;

	protected int iUniqueId_current;
	
	protected HashMap<Integer, T> hashItems;

	/**
	 * Constructor.
	 */
	protected AManager(final IGeneralManager generalManager, final int iUniqueId_type_offset,
			final EManagerType managerType)
	{
		if (generalManager == null)
			throw new CaleydoRuntimeException(
					"Constructor with null-pointer to general manager",
					CaleydoRuntimeExceptionType.MANAGER);

		this.generalManager = generalManager;
		this.managerType = managerType;

		iUniqueId_current = calculateInitialUniqueId(iUniqueId_type_offset);
		
		hashItems = new HashMap<Integer, T>();
	}

	public int calculateInitialUniqueId(final int iUniqueId_type_offset)
	{
		return iUniqueId_type_offset * IGeneralManager.iUniqueId_TypeOffsetMultiplyer;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.data.manager.GeneralManager#createNewId(org.caleydo.
	 * core.data.manager.BaseManagerType)
	 */
	public int createId(EManagerObjectType setNewBaseType)
	{

		iUniqueId_current += IGeneralManager.iUniqueId_Increment;

		return iUniqueId_current;
	}

	/**
	 * Set a new
	 * 
	 * @param setNewBaseType
	 * @param iCurrentId
	 * @return
	 */
	public boolean setCreateNewId(EManagerType setNewBaseType, final int iCurrentId)
	{

		if (iCurrentId < iUniqueId_current)
		{
			return false;
		}

		iUniqueId_current = iCurrentId;

		return true;
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
