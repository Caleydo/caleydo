/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager;

import java.util.Vector;

import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.Storage;

/**
 * Manages all Storage's.
 * 
 * Note: the StorageManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface StorageManager
extends GeneralManager
{
	
	public Storage createStorage( final ManagerObjectType useStorageType );
	
	public boolean deleteStorage( Storage deleteStorage );
	
	public boolean deleteStorage( final int iItemId );
	
	public Storage getItemStorage( final int iItemId );
	
	public Storage[] getAllStorageItems();
	
	public Vector<Storage> getAllStorageItemsVector();
	
	//public ManagerObjectType getManagerType();
	
}
