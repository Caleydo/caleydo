package org.caleydo.core.manager.data;

import java.util.LinkedList;

import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.manager.IManager;
import org.caleydo.core.manager.type.ManagerObjectType;

/**
 * Manages all IStorage's.
 * 
 * @author Michael Kalkusch
 */
public interface IStorageManager
extends IManager
{
	public IStorage createStorage( final ManagerObjectType useStorageType );
	
	public boolean deleteStorage( IStorage deleteStorage );
	
	public boolean deleteStorage( final int iItemId );
	
	public IStorage getItemStorage( final int iItemId );
	
	public IStorage[] getAllStorageItems();
	
	public LinkedList<IStorage> getAllStorageItemsVector();
}
