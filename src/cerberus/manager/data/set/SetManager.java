/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data.set;

import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.IGeneralManager;
import cerberus.manager.data.ICollectionManager;
import cerberus.manager.data.ISetManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.IStorage;
import cerberus.data.collection.ISelection;
import cerberus.data.collection.ISet;
//import cerberus.data.collection.SetType;
import cerberus.data.collection.set.SetFlatSimple;
import cerberus.data.collection.set.SetPlanarSimple;


/**
 * @author Michael Kalkusch
 *
 */
public class SetManager 
extends ICollectionManager
implements ISetManager {

	/**
	 * Reference to the singelton manager.
	 * 
	 * Note: See "Design Pattern" ISingelton
	 */
	//protected IGeneralManager refGeneralManager = null;
	
	/**
	 * Vector holds a list of all ISet's
	 */
	protected Vector<ISet> vecSets;
	
	private ISet testSet;
	
	/**
	 * 
	 */
	public SetManager( IGeneralManager setSingelton,
			final int iInitSizeContainer ) {
		super( setSingelton , 
				IGeneralManager.iUniqueId_TypeOffset_Set );

		assert setSingelton != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
		
		vecSets = new Vector< ISet > ( iInitSizeContainer );
		
		refGeneralManager.getSingelton().setSetManager( this );		
		
		
	}
	
	/**
	 * Create a test ISet.
	 */
	public void initManager() {
		/**
		 * Test ISet...
		 */
//		testSet = new SetPlanarSimple( this.createNewId(ManagerObjectType.SET_PLANAR ),
//				refGeneralManager );
//		
//		this.registerItem( testSet, testSet.getId(), ManagerObjectType.SET_PLANAR );
//		
//		refGeneralManager.getSingelton().getLoggerManager().logMsg( "SET: testSet created with Id =[" +
//				testSet.getId() +"]");
//		
//		ISelection getSelectionById = (ISelection) refGeneralManager.getItem( 15201 );
//		IStorage getStorageById = (IStorage) refGeneralManager.getItem( 15301 );
//		
//		/* register ISelection & IStorage to ISet ... */
//		testSet.setSelectionByDimAndIndex( getSelectionById, 0, 0 );
//		testSet.setStorageByDimAndIndex( getStorageById, 0, 0 );
//		
//		ISet testMySet = (ISet) refGeneralManager.getItem( 15101 );
//		
//		refGeneralManager.getSingelton().getLoggerManager().logMsg( "SET: testSet get ISet by Id; [" +
//				testSet.getId() +"] == [" + testMySet.getId() + "]_(test)");
		/**
		 * END: Test ISet...
		 */
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#createSet()
	 */
	public ISet createSet( final ManagerObjectType useStorageType ) {
			
		switch ( useStorageType ) {
			case SET_LINEAR:
				return new SetFlatSimple(4,getGeneralManager());
				
			case SET_PLANAR:
				return new SetPlanarSimple(4,getGeneralManager());
			
			case SET_CUBIC:
				break;
				
			case SET_MULTI_DIM:
				
			default:
				throw new RuntimeException("SetManagerSimple.createSet() failed due to unhandled type [" +
						useStorageType.toString() + "]");
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#deleteSet(cerberus.data.collection.ISet)
	 */
	public boolean deleteSet(ISet deleteSet ) {
		return vecSets.remove( deleteSet );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#deleteSet(cerberus.data.collection.ISet)
	 */
	public boolean deleteSet( final int iItemId ) {
		try {
			vecSets.remove( iItemId );
			return true;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#getItemSet(int)
	 */
	public ISet getItemSet( final int iItemId) {
		
		try {
			return vecSets.get( getIndexInVector_byUniqueId( iItemId ) );
		} 
		catch (ArrayIndexOutOfBoundsException ae) {
			assert false: "SetManagerSimple.getItemSet() ArrayIndexOutOfBoundsException ";
			return null;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		return getItemSet(iItemId);
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SetManager#getAllSetItems()
	 */
	public ISet[] getAllSetItems() {
		
		ISet[] resultArray = new ISet[ vecSets.size() ];
		
		Iterator<ISet> iter = vecSets.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
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
		return vecSets.size();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#getManagerType()
	 */
	public final ManagerObjectType getManagerType() {		
		return ManagerObjectType.SET;
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		if ( this.hasItem_withUniqueId( iItemId )) {
			unregisterItem_byUniqueId_insideCollection( iItemId );
			return true;
		}
		return false;
	}

	public boolean registerItem( final Object registerItem, 
			final int iItemId , 
			final ManagerObjectType type ) {
		
		
		try {
			ISet addItem = (ISet) registerItem;
			
			if ( hasItem_withUniqueId( iItemId ) ) {
				vecSets.set( getIndexInVector_byUniqueId( iItemId ), addItem );
				return true;
			}
			
			registerItem_byUniqueId_insideCollection( iItemId, vecSets.size() );
			vecSets.addElement( addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
	
	}
}
