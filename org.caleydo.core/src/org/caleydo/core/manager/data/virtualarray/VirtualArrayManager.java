package org.caleydo.core.manager.data.virtualarray;

import java.util.Iterator;
import java.util.Vector;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.virtualarray.VirtualArrayMultiBlock;
import org.caleydo.core.data.collection.virtualarray.VirtualArraySingleBlock;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.data.ACollectionManager;
import org.caleydo.core.manager.data.IVirtualArrayManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;

/**
 * Singleton that manages all virtual arrays.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
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
	 * Constructor.
	 */
	public VirtualArrayManager( IGeneralManager setGeneralManager,
			final int iInitSizeContainer ) {
		super( setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_VirtualArray,
				ManagerType.DATA_VIRTUAL_ARRAY );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
//		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
			
		vecVirtualArray = new Vector< IVirtualArray > ( iInitSizeContainer );

		if ( vecVirtualArray == null )
		{
			debugNullPointer();
		}
//		else 
//		{
//			if ( vecVirtualArray.size() != iInitSizeContainer )
//			{
//				generalManager.logMsg("current vecVirtualArray.size()=["
//						+ vecVirtualArray.size() + 
//						"] is != setSize(" + 
//						iInitSizeContainer +")", 
//						LoggerType.MINOR_ERROR_XML);
//			}
//		}
		
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

	private void debugNullPointer() {
//		generalManager.logMsg("current vecVirtualArray == null! ", 
//				LoggerType.MINOR_ERROR_XML);
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SelectionManager#createSelection()
	 */
	public IVirtualArray createVirtualArray( final ManagerObjectType useSelectionType ) {
		
		assert useSelectionType!= null: "can not handle null pointer";
	
		
		switch ( useSelectionType ) {
			case VIRTUAL_ARRAY_SINGLE_BLOCK:
				return new VirtualArraySingleBlock( createId(ManagerObjectType.VIRTUAL_ARRAY), generalManager, null );
				
			case VIRTUAL_ARRAY_MULTI_BLOCK:
				return new VirtualArrayMultiBlock( createId(ManagerObjectType.VIRTUAL_ARRAY), generalManager, null );	
				
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
	 * @see org.caleydo.core.data.manager.SelectionManager#deleteSelection(org.caleydo.core.data.collection.IVirtualArray)
	 */
	public boolean deleteVirtualArray(IVirtualArray deleteSelection ) {
		if ( vecVirtualArray == null ) 
		{
			debugNullPointer();
		}
		return vecVirtualArray.remove( deleteSelection );
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SelectionManager#deleteSelection(org.caleydo.core.data.collection.IVirtualArray)
	 */
	public boolean deleteVirtualArray( final int iItemId ) {
		try {
			if ( vecVirtualArray == null ) 
			{
				debugNullPointer();
			}
			
			vecVirtualArray.remove( iItemId );
			return true;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SelectionManager#getItemSelection(int)
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
			if ( vecVirtualArray == null ) 
			{
				debugNullPointer();
			}
			
			assert false: "SelectionManagerSimple.getItemSelection() uniqueId=[" + iItemId + "] is not in Manager ";
			return null;
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#getItem(int)
	 */
	public final Object getItem( final int iItemId) {
		return getItemVirtualArray(iItemId);
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SelectionManager#getAllSelectionItems()
	 */
	public IVirtualArray[] getAllVirtualArrayItems() {
		
		if ( vecVirtualArray == null ) 
		{
			debugNullPointer();
		}
		
		IVirtualArray[] resultArray = new IVirtualArray[ vecVirtualArray.size() ];
		
		Iterator<IVirtualArray> iter = vecVirtualArray.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.SelectionManager#getAllSelectionItems()
	 */
	public Vector<IVirtualArray> getAllVirtualArrayItemsVector() {
		
		if ( vecVirtualArray == null ) 
		{
			debugNullPointer();
		}
		
		return vecVirtualArray;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManagerInterface#hasItem(int)
	 */
	public final boolean hasItem(int iItemId) {
		return hasItem_withUniqueId( iItemId );
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManagerInterface#size()
	 */
	public final int size() {		
		if ( vecVirtualArray == null ) 
		{
			debugNullPointer();
		}
		
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
