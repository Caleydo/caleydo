/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data.virtualarray;

import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.IGeneralManager;
//import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.ACollectionManager;
import cerberus.manager.data.IVirtualArrayManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
//import cerberus.parser.handler.importer.ascii.MicroArrayLoader1Storage;

import cerberus.data.collection.IVirtualArray;
//import cerberus.data.collection.set.SetPlanarSimple;
import cerberus.data.collection.virtualarray.VirtualArrayMultiBlock;
import cerberus.data.collection.virtualarray.VirtualArraySingleBlock;
//import cerberus.data.collection.IVirtualArray;

/**
 * @author Michael Kalkusch
 *
 */
public class VirtualArrayManager 
extends ACollectionManager
implements IVirtualArrayManager
{	
	/**
	 * Vector holds a list of all IVirtualArray's
	 */
	protected Vector<IVirtualArray> vecVirtualArray;
	
	/**
	 * 
	 */
	public VirtualArrayManager( IGeneralManager setGeneralManager,
			final int iInitSizeContainer ) {
		super( setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_VirtualArray,
				ManagerType.DATA_VIRTUAL_ARRAY );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
			
		vecVirtualArray = new Vector< IVirtualArray > ( iInitSizeContainer );

		refGeneralManager.getSingelton().setVirtualArrayManager( this );
		
//		/**
//		 * Test IVirtualArray...
//		 */
//		testSelection = new VirtualArrayMultiBlock( 
//				this.createNewId(ManagerObjectType.VIRTUAL_ARRAY_MULTI_BLOCK),
//				refGeneralManager,
//				/// pass no ICollectionLock 
//				null );
//		
//		this.registerItem( testSelection, testSelection.getId(), ManagerObjectType.VIRTUAL_ARRAYMULTI_BLOCK );
//		
//		refGeneralManager.getSingelton().logMsg( 
//				"VIRTUAL_ARRAY: testSelection created with Id =[" +
//				testSelection.getId() +"]", logLevel);
//		/**
//		 * END: Test IVirtualArray...
//		 */
	}


	
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#createSelection()
	 */
	public IVirtualArray createVirtualArray( final ManagerObjectType useSelectionType ) {
		
		assert useSelectionType!= null: "can not handle null pointer";
	
		
		switch ( useSelectionType ) {
			case VIRTUAL_ARRAY_SINGLE_BLOCK:
				return new VirtualArraySingleBlock( createId(ManagerObjectType.VIRTUAL_ARRAY), this, null );
				
			case VIRTUAL_ARRAY_MULTI_BLOCK:
				return new VirtualArrayMultiBlock( createId(ManagerObjectType.VIRTUAL_ARRAY), this, null );	
				
//			case VIRTUAL_ARRAY_LOAD_MICROARRAY:
//				System.err.println("ISelectionManager.createSelection() VIRTUAL_ARRAY_LOAD_MICROARRAY is deprecated!");
				//return new MicroArrayLoader1Storage( getGeneralManager() );
				
//			case VIRTUAL_ARRAY_MULTI_BLOCK_RLE:
//				break;
//				
//			case VIRTUAL_ARRAY_RANDOM_BLOCK:
//				break;
				
				
			default:
				throw new RuntimeException("SelectionManagerSimple.createSelection() failed due to unhandled type [" +
						useSelectionType.toString() + "]");
		}
		
		//return null;
	}

	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#deleteSelection(cerberus.data.collection.IVirtualArray)
	 */
	public boolean deleteVirtualArray(IVirtualArray deleteSelection ) {
		return vecVirtualArray.remove( deleteSelection );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#deleteSelection(cerberus.data.collection.IVirtualArray)
	 */
	public boolean deleteVirtualArray( final int iItemId ) {
		try {
			vecVirtualArray.remove( iItemId );
			return true;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#getItemSelection(int)
	 */
	public IVirtualArray getItemVirtualArray( final int iItemId) {
		
		try {
			return vecVirtualArray.get( getIndexInVector_byUniqueId( iItemId ) );
		} 
		catch (ArrayIndexOutOfBoundsException ae) {
			assert false: "SelectionManagerSimple.getItemSelection() ArrayIndexOutOfBoundsException ";
			return null;
		}
		catch (NullPointerException npe) {
			assert false: "SelectionManagerSimple.getItemSelection() uniqueId=[" + iItemId + "] is not in Manager ";
			return null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		return getItemVirtualArray(iItemId);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#getAllSelectionItems()
	 */
	public IVirtualArray[] getAllVirtualArrayItems() {
		
		IVirtualArray[] resultArray = new IVirtualArray[ vecVirtualArray.size() ];
		
		Iterator<IVirtualArray> iter = vecVirtualArray.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#getAllSelectionItems()
	 */
	public Vector<IVirtualArray> getAllVirtualArrayItemsVector() {
		
		return vecVirtualArray;
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
		return vecVirtualArray.size();
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		if ( this.hasItem_withUniqueId( iItemId )) {
			vecVirtualArray.remove( 
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
			IVirtualArray addItem = (IVirtualArray) registerItem;
			
			if ( hasItem_withUniqueId( iItemId ) ) {
				vecVirtualArray.set( getIndexInVector_byUniqueId( iItemId ), addItem );
				return true;
			}
			
			registerItem_byUniqueId_insideCollection( iItemId, vecVirtualArray.size() );
			vecVirtualArray.addElement( addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
	
	}
}
