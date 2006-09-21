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

import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.manager.data.ICollectionManager;
import cerberus.manager.data.ISelectionManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.ISelection;
//import cerberus.data.collection.SelectionType;
//import cerberus.data.collection.Storage;
import cerberus.data.collection.selection.SelectionSingleBlock;
import cerberus.data.collection.selection.SelectionMultiBlock;
import cerberus.data.collection.set.SetPlanarSimple;
import cerberus.data.collection.ISelection;
import cerberus.data.loader.MicroArrayLoader;



/**
 * @author Michael Kalkusch
 *
 */
public class SelectionManager 
extends ICollectionManager
implements ISelectionManager
{
	
	private final short iLogLevel = LoggerType.VERBOSE.getLevel();
	
	private ISelection testSelection;
	
	/**
	 * Vector holds a list of all ISelection's
	 */
	protected Vector<ISelection> vecSelection;
	

	
	/**
	 * 
	 */
	public SelectionManager( IGeneralManager setGeneralManager,
			final int iInitSizeContainer ) {
		super( setGeneralManager, 
				IGeneralManager.iUniqueId_TypeOffset_Selection );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		assert iInitSizeContainer > 0 : "Constructor with iInitSizeContainer < 1";
			
		vecSelection = new Vector< ISelection > ( iInitSizeContainer );

		refGeneralManager.getSingelton().setSelectionManager( this );
		
		/**
		 * Test ISelection...
		 */
		testSelection = new SelectionMultiBlock( 
				this.createNewId(ManagerObjectType.SELECTION_MULTI_BLOCK),
				refGeneralManager,
				/// pass no ICollectionLock 
				null );
		
		this.registerItem( testSelection, testSelection.getId(), ManagerObjectType.SELECTION_MULTI_BLOCK );
		
		refGeneralManager.getSingelton().getLoggerManager().logMsg( 
				"SELECTION: testSelection created with Id =[" +
				testSelection.getId() +"]", iLogLevel);
		/**
		 * END: Test ISelection...
		 */
	}


	
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#createSelection()
	 */
	public ISelection createSelection( final ManagerObjectType useSelectionType ) {
		
		assert useSelectionType!= null: "can not handle null pointer";
	
		
		switch ( useSelectionType ) {
			case SELECTION_SINGLE_BLOCK:
				return new SelectionSingleBlock( createNewId(ManagerObjectType.SELECTION), this, null );
				
			case SELECTION_MULTI_BLOCK:
				return new SelectionMultiBlock( createNewId(ManagerObjectType.SELECTION), this, null );	
				
			case SELECTION_LOAD_MICROARRAY:
				System.err.println("ISelectionManager.createSelection() SELECTION_LOAD_MICROARRAY is deprecated!");
				//return new MicroArrayLoader( getGeneralManager() );
				
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
	 * @see cerberus.data.manager.SelectionManager#deleteSelection(cerberus.data.collection.ISelection)
	 */
	public boolean deleteSelection(ISelection deleteSelection ) {
		return vecSelection.remove( deleteSelection );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#deleteSelection(cerberus.data.collection.ISelection)
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
	public ISelection getItemSelection( final int iItemId) {
		
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
	public ISelection[] getAllSelectionItems() {
		
		ISelection[] resultArray = new ISelection[ vecSelection.size() ];
		
		Iterator<ISelection> iter = vecSelection.iterator();
		for ( int i=0 ; iter.hasNext() ; i++ ) {
			resultArray[i] = iter.next();
		}
		
		return resultArray;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.manager.SelectionManager#getAllSelectionItems()
	 */
	public Vector<ISelection> getAllSelectionItemsVector() {
		
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
			ISelection addItem = (ISelection) registerItem;
			
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
