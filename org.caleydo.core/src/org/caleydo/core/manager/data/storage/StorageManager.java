package org.caleydo.core.manager.data.storage;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Manager for storage objects.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class StorageManager
	extends AManager<IStorage>
	implements IStorageManager
{
	@Override
	public IStorage createStorage(final EManagedObjectType type)
	{
		switch (type)
		{
			case STORAGE_NUMERICAL:
				return new NumericalStorage();
			case STORAGE_NOMINAL:
				return new NominalStorage<String>();

			default:
				throw new IllegalStateException("Failed due to unhandled type ["
						+ type.toString() + "]");
		}
	}

}
