/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.manager.data.selection;

import java.util.Vector;
import java.util.Iterator;

import cerberus.manager.GeneralManager;
import cerberus.manager.SelectionManager;
import cerberus.manager.data.CollectionManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.Selection;
//import cerberus.data.collection.SelectionType;
//import cerberus.data.collection.Storage;
import cerberus.data.collection.selection.SelectionSingleBlock;
import cerberus.data.collection.selection.SelectionMultiBlock;
import cerberus.data.collection.set.SetPlanarSimple;
import cerberus.data.collection.Selection;
import cerberus.data.loader.MicroArrayLoader;
import cerberus.data.xml.MementoXML;



/**
 * @author Michael Kalkusch
 *
 */
public class SelectionManagerSimple 
extends CollectionManager
implements SelectionManager
{
	
	private Selection testSelection;
	
	/**
	 * Vector holds a list of all Selection's
	 */
	protected Vector<Selection> vecSelection;
	

	
	/**
	 * 
	 */
	public SelectionManagerSimple( GeneralManager setGeneralManager,
			final int iInitSizeContainer ) {
		super( setGeneralManager, 
				GeneralManager.iUniqueId_TypeOffset_Selection );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
			
		vecSelection = new Vector< Selection > ( iInitSizeContainer );

		refGeneralManager.getSingelton().setSelectionManager( this );
		
		/**
		 * Test Selection...
		 */
		testSelection = new SelectionMultiBlock( 
				this.createNewId(ManagerObjectType.SELECTION_MULTI_BLOCK),
				refGeneralManager,
				/// pass no CollectionLock 
				null );
		
		this.registerItem( testSelection, testSelection.getId(), ManagerObjectType.SELECTION_MULTI_BLOCK );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg( "SELECTION: testSelection created with Id =[" +
				testSelection.getId() +"]");
		/**
		 * END: Test Selection...
		 */
	}


	
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#createSelection()
	 */
	public MementoXML createSelection( final ManagerObjectType useSelectionType ) {
		
		assert useSelectionType!= null: "can not handle null pointer";
	
		
		switch ( useSelectionType ) {
			case SELECTION_SINGLE_BLOCK:
				return new SelectionSingleBlock( createNewId(ManagerObjectType.SELECTION), this, null );
				
			case SELECTION_MULTI_BLOCK:
				return new SelectionMultiBlock( createNewId(ManagerObjectType.SELECTION), this, null );	
				
			case SELECTION_LOAD_MICROARRAY:
				return new MicroArrayLoader( getGeneralManager() );
				
//			case SELECTION_MULTI_BLOCK_RLE:
//				break;
//				
//			case SELECTION_RANDOM_BLOCK:
//				break;
				
				
			default:
				throw new RuntimeException("SelectionManagerSimple.createSelection() failed due to unhandled type [" +
						useSelectionType.toString() + "]");
		}
		
		//return null;
	}

	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#deleteSelection(cerberus.data.collection.Selection)
	 */
	public boolean deleteSelection(Selection deleteSelection ) {
		return vecSelection.remove( deleteSelection );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#deleteSelection(cerberus.data.collection.Selection)
	 */
	public boolean deleteSelection( final int iItemId ) {
		try {
			vecSelection.remove( iItemId );
			return true;
		}
		catch (ArrayIndexOutOfBoundsException ae) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#getItemSelection(int)
	 */
	public Selection getItemSelection( final int iItemId) {
		
		try {
			return vecSelection.get( getIndexInVector_byUniqueId( iItemId ) );
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
		return getItemSelection(iItemId);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#getAllSelectionItems()
	 */
	public Selection[] getAllSelectionItems() {
		
		Selection[] resultArray = new Selection[ vecSelection.size() ];
		
		Iterator<Selection> iter = vecSelection.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#getAllSelectionItems()
	 */
	public Vector<Selection> getAllSelectionItemsVector() {
		
		return vecSelection;
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
		return vecSelection.size();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManagerInterface#getManagerType()
	 */
	public final ManagerObjectType getManagerType() {		
		return ManagerObjectType.SELECTION;
	}
	
	public boolean unregisterItem( final int iItemId,
			final ManagerObjectType type  ) {
		
		if ( this.hasItem_withUniqueId( iItemId )) {
			vecSelection.remove( 
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
			Selection addItem = (Selection) registerItem;
			
			if ( hasItem_withUniqueId( iItemId ) ) {
				vecSelection.set( getIndexInVector_byUniqueId( iItemId ), addItem );
				return true;
			}
			
			registerItem_byUniqueId_insideCollection( iItemId, vecSelection.size() );
			vecSelection.addElement( addItem );
				
			return true;
		}
		catch ( NullPointerException npe) {
			assert false:"cast of object ot storage falied";
			return false;
		}
	
	}
}
