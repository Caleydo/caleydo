package org.caleydo.core.manager.data;

import java.util.Set;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.type.EManagerObjectType;

/**
 * Manages all IStorages.
 * 
 * @author Michael Kalkusch
 */
public interface IStorageManager
	extends IManager
{

	public IStorage createStorage(final EManagerObjectType useStorageType);

	public void removeStorage(IStorage deleteStorage);

	public void removeStorage(final int iItemId);

	public IStorage getStorage(final int iItemId);

	public Set<IStorage> getAllStorages();

}
