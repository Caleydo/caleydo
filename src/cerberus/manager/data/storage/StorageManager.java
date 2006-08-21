/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data.storage;

import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.data.ICollectionManager;
import cerberus.manager.data.IStorageManager;
import cerberus.manager.type.ManagerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.singelton.SingeltonManager;
//import java.util.Hashtable;

import cerberus.data.collection.IStorage;
//import cerberus.data.collection.StorageType;
import cerberus.data.collection.set.SetPlanarSimple;
import cerberus.data.collection.storage.FlatThreadStorageSimple;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public class StorageManager 
extends ICollectionManager
implements IStorageManager {
	
	private IStorage testStorage;
	/**
	 * Vector holds a list of all IStorage's
	 */
	protected Vector<IStorage> vecStorage;
	
	/**
	 * 
	 */
	public StorageManager( IGeneralManager setGeneralManager,
			final int iInitSizeContainer ) {
		
		super( setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Storage );
		
		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
			
		vecStorage = new Vector< IStorage > ( iInitSizeContainer );
		
		refGeneralManager.getSingelton().setStorageManager( this );
		
		/**
		 * Test IStorage...
		 */
		testStorage = new FlatThreadStorageSimple( this.createNewId(ManagerObjectType.SET_PLANAR ),
				refGeneralManager,
				/// pass no ICollectionLock 
				null);
		
		this.registerItem( testStorage, testStorage.getId(), ManagerObjectType.STORAGE_FLAT );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg( "STORAGE: testStorage created with Id =[" +
				testStorage.getId() +"]");
		/**
		 * END: Test IStorage...
		 */
			
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#createStorage()
	 */
	public IStorage createStorage( final ManagerObjectType useStorageType ) {
		
		if ( useStorageType.getGroupType() != ManagerType.STORAGE ) {
			throw new CerberusRuntimeException("try to create object with wrong type " + useStorageType.name() );
		}

		
		final int iNewId = this.createNewId( useStorageType );
		
		switch ( useStorageType ) {
			case STORAGE:
			case STORAGE_FLAT:
				return new FlatThreadStorageSimple( iNewId, refGeneralManager, null );				
				
			default:
				throw new CerberusRuntimeException("StorageManagerSimple.createStorage() failed due to unhandled type [" +
						useStorageType.toString() + "]");
		}
		
		//registerItem( newStorage, iNewId, useStorageType );
		
		//return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(cerberus.data.collection.IStorage)
	 */
	public boolean deleteStorage(IStorage deleteStorage ) {
		return vecStorage.remove( deleteStorage );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(cerberus.data.collection.IStorage)
	 */
	public boolean deleteStorage( final int iItemId ) {
		try {
			unregisterItem( iItemId, null );
			
			vecStorage.remove( iItemId );
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
		
		try {
			return vecStorage.get( getIndexInVector_byUniqueId(iItemId) );
		} 
		catch (NullPointerException npe) {
			assert false:"uniqueId was not found";
			return null;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			assert false: "StorageManagerSimple.getItemStorage() ArrayIndexOutOfBoundsException ";
			return null;
		}
	}

	/**
	 *  
	 * @see cerberus.manager.IGeneralManager#getItem(int)
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public final Object getItem( final int iItemId) {
		return getItemStorage(iItemId);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getAllStorageItems()
	 */
	public IStorage[] getAllStorageItems() {
		
		IStorage[] resultArray = new IStorage[ vecStorage.size() ];
		
		Iterator<IStorage> iter = vecStorage.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getAllStorageItemsVector()
	 */
	public Vector<IStorage> getAllStorageItemsVector() {
		return vecStorage;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#hasItem(int)
	 */
	public final boolean hasItem(int iItemId) {
		return hasItem_withUniqueId( iItemId );	
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#size()
	 */
	public final int size() {		
		return vecStorage.size();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#getManagerType()
	 */
	public final ManagerObjectType getManagerType() {		
		return ManagerObjectType.STORAGE_FLAT;
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		if ( this.hasItem_withUniqueId( iItemId )) {
			vecStorage.remove( 
					(int) getIndexInVector_byUniqueId( iItemId ));
			unregisterItem_byUniqueId_insideCollection( iItemId );
			return true;
		}
		return false;
	}

	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type ) {
		
		
		try {
			
			IStorage addItem = (IStorage) registerItem;
			
			if ( hasItem_withUniqueId( iItemId ) ) {
				vecStorage.set( getIndexInVector_byUniqueId( iItemId ), addItem );
				return true;
			}
			
			registerItem_byUniqueId_insideCollection( iItemId, vecStorage.size() );
			vecStorage.addElement( addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
		
				
	}

}
