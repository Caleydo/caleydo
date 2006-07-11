/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.collection;

import java.util.Hashtable;

import cerberus.manager.GeneralManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.BaseManagerGroupType;
import cerberus.manager.type.BaseManagerType;
import cerberus.manager.singelton.SingeltonManager;

//import prometheus.manager.SetManager;
//import prometheus.manager.StorageManager;
import cerberus.util.exception.PrometheusRuntimeException;

/**
 * Base interface for all manger objects.
 * 
 * Note: each CollectionManager like SelectionManger, SetManager and StorageManager must register 
 * itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.manager.SelectionManger
 * @see cerberus.manager.SetManager
 * @see cerberus.manager.StorageManager
 * 
 */
public abstract class CollectionManager 
 extends AbstractManagerImpl
 implements GeneralManager {

	
	/**
	 * Holds value of the current collectionId.
	 */
	private int iCurrentCollectionId;
	
	/**
	 * Offset used for seperating manager-type-id from network-application-id.
	 * See constructor for details.
	 */
	private int iUniqueId_Collection_Offset_TypeId = 50;
	
	/**
	 * Contains a lookup of unique Id to Object
	 */
	private Hashtable<Integer,Integer> hashId2IndexLookup;
	
	
	protected CollectionManager( final GeneralManager setSingeltonManager,
			final int iSetInitialCollectionTypeId ) {
		
		super( setSingeltonManager );
		
//		assert iSetInitialCollectionTypeId >= iUniqueId_WorkspaceOffset: 
//			"CollectionManager.CollectionManager() failed due to wrong initialisation prefix: "+ iSetInitialCollectionTypeId;
		
		setUniqueId_TypeParameters( iSetInitialCollectionTypeId );
		
		hashId2IndexLookup = new Hashtable<Integer,Integer>();
	}

	
	private void setUniqueId_TypeParameters( final int iSetuniqueId_TypeOffset) {
		iUniqueId_Collection_Offset_TypeId = iSetuniqueId_TypeOffset;
		
		iCurrentCollectionId = 
			calculateId( iSetuniqueId_TypeOffset, refGeneralManager );
	}
	
	/**
	 * Calculates an initial Id from the pieces of information provided 
	 * by the SingeltonManager and the type-offset information.
	 * 
	 * @param iSetUniqueId_TypeOffset offset per type
	 * @param useRefSingeltonManager reference to singelton using getNetworkPostfix()
	 * @return initial unique Id
	 */
	public final static int calculateId( final int iSetUniqueId_TypeOffset, 
			final GeneralManager useRefSingeltonManager) {
		
		return (iUniqueId_Increment +
			iSetUniqueId_TypeOffset * iUniqueId_WorkspaceOffset +
			useRefSingeltonManager.getSingelton().getNetworkPostfix() );
	}
	
	
	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#hasItem(int)
	 */
	public abstract boolean hasItem( final int iItemId );
	
	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#size()
	 */
	public abstract int size();
	
	/* (non-Javadoc)
	 * @see prometheus.data.manager.GeneralManager#getManagerType()
	 */
	public abstract BaseManagerType getManagerType();
	
	
	/**
	 * Create a new unique collectionId.
	 * 
	 * @return new unique collectionId
	 */
	public final int createNewId( final BaseManagerType setNewBaseType ) {
		
		if (( setNewBaseType.getGroupType() == BaseManagerGroupType.SELECTION )
			||( setNewBaseType.getGroupType() == BaseManagerGroupType.SET )
			||( setNewBaseType.getGroupType() == BaseManagerGroupType.STORAGE )) {
			
			iCurrentCollectionId += iUniqueId_Increment;		
			return iCurrentCollectionId;
		}
		throw new PrometheusRuntimeException("error create a new Id from type " + setNewBaseType.name() );
	}
	
	
	protected boolean unregisterItemCollection( final int iItemId ) {
		
		hashId2IndexLookup.remove( new Integer(iItemId) );
		return true;
		
	}

	protected int getLookupValueById( final int iItemId ) {
		return (int) hashId2IndexLookup.get( new Integer( iItemId )).intValue();
	}
	
	protected boolean hasLookupValueById( final int iItemId ) {
		return hashId2IndexLookup.containsKey( new Integer( iItemId ));
	}
	
	protected boolean registerItemCollection( final int iItemId, 
			final int iValuefromLookup  ) {
		
		try {

			/**
			 * overwrite existing object or
			 * add new item to hashmap..
			 */
			hashId2IndexLookup.put( new Integer(iItemId), 
					new Integer( iValuefromLookup ) );
			
			return true;
			
		}
		catch (NullPointerException npe) {
			return false;
		}
	}

}
