/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data;

import java.util.LinkedList;

import cerberus.data.collection.IStorage;
//import cerberus.data.collection.StorageType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

/**
 * Manages all IStorage's.
 * 
 * Note: the IStorageManager must register itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IStorageManager
extends IGeneralManager
{
	
	public IStorage createStorage( final ManagerObjectType useStorageType );
	
	public boolean deleteStorage( IStorage deleteStorage );
	
	public boolean deleteStorage( final int iItemId );
	
	public IStorage getItemStorage( final int iItemId );
	
	public IStorage[] getAllStorageItems();
	
	public LinkedList<IStorage> getAllStorageItemsVector();
	
	//public ManagerObjectType getManagerType();
	
}
