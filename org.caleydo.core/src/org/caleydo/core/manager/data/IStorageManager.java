package org.caleydo.core.manager.data;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.id.EManagedObjectType;

/**
 * Manages all IStorages.
 * 
 * @author Alexander Lex
 */
public interface IStorageManager
	extends IManager<IStorage>
{
	public IStorage createStorage(final EManagedObjectType useStorageType);
}
