/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.manager.data.storage;

import java.util.Enumeration;
import java.util.Hashtable;
//import java.util.Iterator;
import java.util.LinkedList;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.manager.data.ACollectionManager;
import org.geneview.core.manager.data.IStorageManager;
import org.geneview.core.manager.type.ManagerType;
import org.geneview.core.manager.type.ManagerObjectType;

import org.geneview.core.data.collection.IStorage;
//import org.geneview.core.data.collection.StorageType;
//import org.geneview.core.data.collection.set.SetPlanarSimple;
import org.geneview.core.data.collection.storage.FlatThreadStorageSimple;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class StorageManager 
extends ACollectionManager
implements IStorageManager {
	
	/**
	 * Hashtable holds a list of all IStorage's.
	 */
	protected Hashtable<Integer,IStorage> vecStorage;
	
	/**
	 * Hashtable holds a list of all IStorage's for reverse lookup.
	 */
	protected Hashtable<IStorage,Integer> vecStorage_reverse;
	
	/**
	 * 
	 */
	public StorageManager( IGeneralManager setGeneralManager,
			final int iInitSizeContainer ) {
		
		super( setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Storage,
				ManagerType.DATA_STORAGE);
		
		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
			
		vecStorage = new Hashtable<Integer,IStorage> ( iInitSizeContainer );
		vecStorage_reverse = new Hashtable<IStorage,Integer> ( iInitSizeContainer );
		
		refGeneralManager.getSingelton().setStorageManager( this );		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#createStorage()
	 */
	public IStorage createStorage( final ManagerObjectType useStorageType ) {
		
		if ( useStorageType.getGroupType() != ManagerType.DATA_STORAGE ) {
			throw new GeneViewRuntimeException("try to create object with wrong type " + useStorageType.name() );
		}

		
		final int iNewId = this.createId( useStorageType );
		
		switch ( useStorageType ) {
			case STORAGE:
			case STORAGE_FLAT:
				return new FlatThreadStorageSimple( iNewId, refGeneralManager, null );				
				
			default:
				throw new GeneViewRuntimeException("StorageManagerSimple.createStorage() failed due to unhandled type [" +
						useStorageType.toString() + "]");
		}
		
		//registerItem( newStorage, iNewId, useStorageType );
		
		//return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(cerberus.data.collection.IStorage)
	 */
	public boolean deleteStorage(IStorage deleteStorage ) {
		int iIndexStorage = 
			vecStorage_reverse.remove( deleteStorage );
		vecStorage.remove( iIndexStorage );
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(cerberus.data.collection.IStorage)
	 */
	public boolean deleteStorage( final int iItemId ) {
		try {
			IStorage buffer = vecStorage.remove( iItemId );
			vecStorage.remove( buffer );
			
			return true;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getItemStorage(int)
	 */
	public IStorage getItemStorage( final int iItemId) {		
		return vecStorage.get( iItemId );		
	}

	/**
	 *  
	 * @see cerberus.manager.IGeneralManager#getItem(int)
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public final Object getItem( final int iItemId) {
		return vecStorage.get( iItemId );	
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getAllStorageItems()
	 */
	public IStorage[] getAllStorageItems() {
		
		IStorage[] resultArray = new IStorage[ vecStorage.size() ];
		
		Enumeration <IStorage> enumIter = vecStorage_reverse.keys();
		
		for ( int i=0 ; enumIter.hasMoreElements() ; i++ ) {
			resultArray[i] = enumIter.nextElement();
		}
		
		return resultArray;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getAllStorageItemsVector()
	 */
	public LinkedList<IStorage> getAllStorageItemsVector() {
		
		LinkedList <IStorage> resultArray = 
			new LinkedList <IStorage> ();
		
		Enumeration <IStorage> enumIter = vecStorage_reverse.keys();
		
		for ( int i=0 ; enumIter.hasMoreElements() ; i++ ) {
			resultArray.addLast( enumIter.nextElement() );
		}
		
		return resultArray;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#hasItem(int)
	 */
	public final boolean hasItem(int iItemId) {
		return vecStorage.containsKey( iItemId );	
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#size()
	 */
	public final int size() {		
		return vecStorage.size();
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		return deleteStorage( iItemId );
	}

	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type ) {
		
		if ( vecStorage.containsKey( iItemId ) ) 
		{
			refGeneralManager.getSingelton().logMsg(
					"try to register id that was already used!",
					LoggerType.ERROR_ONLY );
			
			return false;
		}
		
		try {
			
			IStorage addItem = (IStorage) registerItem;
			
			vecStorage.put( iItemId, addItem );
			vecStorage_reverse.put( addItem, iItemId );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
		
				
	}

}
