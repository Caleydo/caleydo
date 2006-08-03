/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data;

import java.util.Hashtable;

import cerberus.manager.GeneralManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerType;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.singelton.SingeltonManager;

//import prometheus.manager.SetManager;
//import prometheus.manager.StorageManager;
import cerberus.util.exception.CerberusRuntimeException;

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
	 * Contains a lookup of unique Id to Object
	 */
	private Hashtable<Integer,Integer> hashId2IndexLookup;
	
	
	protected CollectionManager( final GeneralManager setSingeltonManager,
			final int iUniqueId_type_offset ) {
		
		super( setSingeltonManager, iUniqueId_type_offset );		
		
		hashId2IndexLookup = new Hashtable<Integer,Integer>();
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
	public abstract ManagerObjectType getManagerType();
	
	
//	/**
//	 * Create a new unique collectionId.
//	 * 
//	 * @return new unique collectionId
//	 */
//	public final int createNewId( final ManagerObjectType setNewBaseType ) {
//		
//		if (( setNewBaseType.getGroupType() == ManagerType.SELECTION )
//			||( setNewBaseType.getGroupType() == ManagerType.SET )
//			||( setNewBaseType.getGroupType() == ManagerType.STORAGE )) {
//			
//			iCurrentCollectionId += iUniqueId_Increment;		
//			return iCurrentCollectionId;
//		}
//		throw new CerberusRuntimeException("error create a new Id from type " + setNewBaseType.name() );
//	}
	
	
	protected boolean unregisterItem_byUniqueId_insideCollection( final int iItemId ) {
		
		System.out.println("remove PRE : " + this.hashId2IndexLookup.toString() );
		
		hashId2IndexLookup.remove( new Integer(iItemId) );
		
		System.out.println("remove POST: " + this.hashId2IndexLookup.toString() );
		
		return true;
		
	}

	protected int getIndexInVector_byUniqueId( final int iItemId ) {
		return (int) hashId2IndexLookup.get( new Integer( iItemId )).intValue();
	}
	
	protected boolean hasItem_withUniqueId( final int iItemId ) {
		return hashId2IndexLookup.containsKey( new Integer( iItemId ));
	}
	
	protected boolean registerItem_byUniqueId_insideCollection( final int iItemId, 
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
