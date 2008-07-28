package org.caleydo.core.manager.data.storage;

import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ACollectionManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * @author Michael Kalkusch
 * 
 * 
 *         TODO: review
 */
public class StorageManager extends ACollectionManager
		implements IStorageManager {

	private HashMap<Integer, IStorage> hashStorageIDToStorage;

	private HashMap<IStorage, Integer> hashStorageToStorageID;

	/**
	 * Constructor.
	 */
	public StorageManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager, IGeneralManager.iUniqueId_TypeOffset_Storage,
				ManagerType.DATA_STORAGE);

		hashStorageIDToStorage = new HashMap<Integer, IStorage>();
		hashStorageToStorageID = new HashMap<IStorage, Integer>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.data.manager.StorageManager#createStorage()
	 */
	public IStorage createStorage(final ManagerObjectType useStorageType) {

		if (useStorageType.getGroupType() != ManagerType.DATA_STORAGE)
		{
			throw new CaleydoRuntimeException(
					"try to create object with wrong type "
							+ useStorageType.name());
		}

		final int iNewId = this.createId(useStorageType);

		switch (useStorageType)
		{
		case STORAGE:
		case STORAGE_FLAT:
			return new NumericalStorage(iNewId, generalManager);

		default:
			throw new CaleydoRuntimeException(
					"StorageManagerSimple.createStorage() failed due to unhandled type ["
							+ useStorageType.toString() + "]");
		}

		// registerItem( newStorage, iNewId, useStorageType );

		// return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.caleydo.core.data.manager.StorageManager#deleteStorage(org.caleydo
	 * .core.data.collection.IStorage)
	 */
	public void removeStorage(IStorage deleteStorage) {

		int iIndexStorage = hashStorageToStorageID.remove(deleteStorage);
		hashStorageIDToStorage.remove(iIndexStorage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.caleydo.core.data.manager.StorageManager#deleteStorage(org.caleydo
	 * .core.data.collection.IStorage)
	 */
	public void removeStorage(final int iItemId) 
	{
		IStorage buffer = hashStorageIDToStorage.remove(iItemId);
		hashStorageIDToStorage.remove(buffer);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.data.manager.StorageManager#getItemStorage(int)
	 */
	public IStorage getStorage(final int iItemId) {

		return hashStorageIDToStorage.get(iItemId);
	}

	/**
	 * 
	 * @see org.caleydo.core.manager.IGeneralManager#getItem(int)
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public final Object getItem(final int iItemId) {

		return hashStorageIDToStorage.get(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.data.manager.StorageManager#getAllStorageItems()
	 */
	public Set<IStorage> getAllStorages ()
	{
		return hashStorageToStorageID.keySet();		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.data.manager.GeneralManagerInterface#hasItem(int)
	 */
	public final boolean hasItem(int iItemId) {

		return hashStorageIDToStorage.containsKey(iItemId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.data.manager.GeneralManagerInterface#size()
	 */
	public final int size() {

		return hashStorageIDToStorage.size();
	}

	public boolean unregisterItem(final int iItemId) {
		// TODO bad hack, manager interface
		removeStorage(iItemId);
		return true;
	}

	public boolean registerItem(final Object registerItem, final int iItemId) {

		if (hashStorageIDToStorage.containsKey(iItemId))
		{
			// generalManager.logMsg(
			// "try to register id that was already used!",
			// LoggerType.ERROR );

			return false;
		}

		try
		{

			IStorage addItem = (IStorage) registerItem;

			hashStorageIDToStorage.put(iItemId, addItem);
			hashStorageToStorageID.put(addItem, iItemId);

			return true;
		} catch (NullPointerException npe)
		{
			assert false : "cast of object ot storage falied";
			return false;
		}

	}



}
