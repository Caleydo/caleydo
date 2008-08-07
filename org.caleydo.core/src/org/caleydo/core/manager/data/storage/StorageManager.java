package org.caleydo.core.manager.data.storage;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * @author Michael Kalkusch TODO: review
 */
public class StorageManager
	extends AManager<IStorage>
	implements IStorageManager
{

	//private HashMap<IStorage, Integer> hashStorageToStorageID;

	/**
	 * Constructor.
	 */
	public StorageManager(IGeneralManager setGeneralManager)
	{

		super(setGeneralManager, IGeneralManager.iUniqueId_TypeOffset_Storage,
				EManagerType.DATA_STORAGE);

//		hashStorageIDToStorage = new HashMap<Integer, IStorage>();
//		hashStorageToStorageID = new HashMap<IStorage, Integer>();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.manager.StorageManager#createStorage()
	 */
	public IStorage createStorage(final EManagerObjectType useStorageType)
	{

		if (useStorageType.getGroupType() != EManagerType.DATA_STORAGE)
		{
			throw new CaleydoRuntimeException("try to create object with wrong type "
					+ useStorageType.name());
		}

		final int iNewId = this.createId(useStorageType);

		switch (useStorageType)
		{
			case STORAGE_NUMERICAL:
				return new NumericalStorage(iNewId, generalManager);
			case STORAGE_NOMINAL:
				return new NominalStorage<String>(iNewId, generalManager);

			default:
				throw new CaleydoRuntimeException(
						"StorageManagerSimple.createStorage() failed due to unhandled type ["
								+ useStorageType.toString() + "]");
		}
	}
}
