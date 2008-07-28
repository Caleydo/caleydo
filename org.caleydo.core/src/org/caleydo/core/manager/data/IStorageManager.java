package org.caleydo.core.manager.data;

import java.util.Set;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.type.ManagerObjectType;

/**
 * Manages all IStorages.
 * 
 * @author Michael Kalkusch
 * 
 * TODO: Comments
 */
public interface IStorageManager
extends IManager
{
	public IStorage createStorage(final ManagerObjectType useStorageType);
	
	public void removeStorage(IStorage deleteStorage);
	
	public void removeStorage(final int iItemId);
	
	public IStorage getStorage(final int iItemId);
	
	public Set<IStorage> getAllStorages();

}
