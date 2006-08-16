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

import cerberus.data.collection.IStorage;

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
	
	public Vector<IStorage> getAllStorageItemsVector();
	
	//public ManagerObjectType getManagerType();
	
}
