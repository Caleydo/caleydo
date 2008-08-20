package org.caleydo.core.manager.data.storage;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.data.IStorageManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * @author Michael Kalkusch TODO: review
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
				throw new CaleydoRuntimeException(
						"Ffailed due to unhandled type ["
								+ type.toString() + "]");
		}
	}
	
	
}
