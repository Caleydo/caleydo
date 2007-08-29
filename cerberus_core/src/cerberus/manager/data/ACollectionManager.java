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

import cerberus.manager.IGeneralManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerType;

//import prometheus.manager.SetManager;
//import prometheus.manager.StorageManager;
//import cerberus.util.exception.GeneViewRuntimeException;

/**
 * Base interface for all manger objects.
 * 
 * Note: each ACollectionManager like SelectionManger, ISetManager and IStorageManager must register 
 * itself to the singelton prometheus.app.SingeltonManager
 * 
 * @author Michael Kalkusch
 *
 * @see prometheus.data.manager.SelectionManger
 * @see cerberus.manager.data.ISetManager
 * @see cerberus.manager.data.IStorageManager
 * 
 */
public abstract class ACollectionManager 
 extends AAbstractManager
 implements IGeneralManager {

	
	/**
	 * Contains a lookup of unique Id to Object
	 */
	private Hashtable<Integer,Integer> hashId2IndexLookup;
	
	
	protected ACollectionManager( final IGeneralManager setSingeltonManager,
			final int iUniqueId_type_offset,
			final ManagerType setManagerType ) {
		
		super( setSingeltonManager, 
				iUniqueId_type_offset,
				setManagerType);		
		
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
			final IGeneralManager useRefSingeltonManager) {
		
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
	
	
	protected boolean unregisterItem_byUniqueId_insideCollection( final int iItemId ) {
		
		System.out.println("ACollectionManager.unregisterItem_byUniqueId_insideCollection() remove PRE : " + this.hashId2IndexLookup.toString() );
		
		hashId2IndexLookup.remove( new Integer(iItemId) );
		
		System.out.println("ACollectionManager.unregisterItem_byUniqueId_insideCollection() remove POST: " + this.hashId2IndexLookup.toString() );
		
		return true;
		
	}

	protected int getIndexInVector_byUniqueId( final int iItemId ) {
		Integer buffer = hashId2IndexLookup.get( new Integer( iItemId ));
		return buffer.intValue();
		
		//return (int) hashId2IndexLookup.get( new Integer( iItemId )).intValue();
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
