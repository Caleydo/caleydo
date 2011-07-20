package org.caleydo.core.manager.data.storage;

import org.caleydo.core.data.collection.storage.AStorage;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.AManager;

/**
 * Manager for storage objects.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class StorageManager
	extends AManager<AStorage> {

	public AStorage createStorage(final ManagedObjectType type) {
		switch (type) {
			case STORAGE_NUMERICAL:
				return new NumericalStorage();
			case STORAGE_NOMINAL:
				return new NominalStorage<String>();

			default:
				throw new IllegalStateException("Failed due to unhandled type [" + type.toString() + "]");
		}
	}

}
