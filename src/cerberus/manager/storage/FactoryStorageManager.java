/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.storage;

import java.util.Vector;

import cerberus.manager.GeneralManager;
import cerberus.manager.StorageManager;
import cerberus.manager.collection.CollectionManager;
import cerberus.manager.type.BaseManagerType;

import cerberus.data.collection.Storage;
//import cerberus.data.collection.StorageType;

/**
 * @author Michael Kalkusch
 *
 */
public class FactoryStorageManager 
extends CollectionManager
implements StorageManager {

	protected Storage refStorage = null;
	
	protected int iInitSizeContainer = 2;
	
	
	/**
	 * Via  ( collectionId MODULO iCollectionId_LowestDigit) you can identify the type of collection.
	 * 
	 * iCollectionId_LowestDigit....1   ==>  Selection
	 * iCollectionId_LowestDigit....3   ==>  Set
	 * iCollectionId_LowestDigit....5   ==>  Storage
	 * iCollectionId_LowestDigit....7   ==>  not used yet
	 * 
	 */
	static final private int iCollectionId_LowestDigit = 5;
	
	
	/**
	 * 
	 */
	public FactoryStorageManager(GeneralManager setGeneralManager,
			final int iSetInitSizeContainer ) {
		
		super( setGeneralManager, iCollectionId_LowestDigit );
		
		assert setGeneralManager != null : "FactoryStorageManager.Constructor  fed with null-pointer to singelton";
		
		iInitSizeContainer = iSetInitSizeContainer;
		
		refGeneralManager.getSingelton().setStorageManager( this );	
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#createStorage(cerberus.data.collection.StorageType)
	 */
	public Storage createStorage(BaseManagerType useStorageType) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(cerberus.data.collection.Storage)
	 */
	public boolean deleteStorage(Storage deleteStorage) {
		if ( refStorage == null ) {
			return false;
		}
		
		refStorage = null;
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#deleteStorage(int)
	 */
	public boolean deleteStorage(int iItemId) {
		if ( refStorage == null ) {
			return false;
		}
		
		refStorage.removeStorage( iItemId );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getItemStorage(int)
	 */
	public Storage getItemStorage(int iItemId) {
		if ( refStorage == null ) {
			return null;
		}
		
		assert iItemId !=0 : "FactoryStorageManager.getItemStorage() index out of bounds!";
		
		return refStorage;
	}
	
	/**
	 *  
	 * @see cerberus.manager.GeneralManager#getItem(int)
	 * @see cerberus.manager.StorageManager#getItemStorage(int)
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public final Object getItem( final int iItemId) {
		return getItemStorage(iItemId);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.StorageManager#getAllStorageItems()
	 */
	public Storage[] getAllStorageItems() {
		if ( refStorage == null ) {
			return null;
		}
		
		Storage[] resultArray = new Storage[1];
		resultArray[0] = refStorage;
		
		return resultArray;
	}

	public Vector<Storage> getAllStorageItemsVector() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#hasItem(int)
	 */
	public boolean hasItem(int iItemId) {
		if ( refStorage == null ) {
			return false;
		}
		if (( iItemId < 0)||(iItemId >= refStorage.getNumberArrays())) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#size()
	 */
	public int size() {
		if ( refStorage == null ) {
			return 0;
		}
		return refStorage.getNumberArrays();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#getManagerType()
	 */
	public BaseManagerType getManagerType() {
		if ( refStorage == null ) {
			return BaseManagerType.FABRIK;
		}
		return BaseManagerType.STORAGE;
	}
	
	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final BaseManagerType type ) {
		assert false:"not supported";
		return false;
	}
	
	public boolean unregisterItem( final int iItemId , 
			final BaseManagerType type ) {
		assert false:"not supported";
		return false;
	}

}
