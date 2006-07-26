/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.set;

import java.util.Iterator;
import java.util.Vector;
//import java.util.Iterator;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.CollectionMetaData;
import cerberus.data.collection.Selection;
import cerberus.data.collection.Storage;
import cerberus.data.collection.Set;
//import cerberus.data.collection.parser.CollectionSetParseSaxHandler;
import cerberus.xml.parser.DParseSaxHandler;
import cerberus.data.collection.thread.impl.CollectionThreadItem;
import cerberus.data.collection.thread.lock.CollectionLock;
import cerberus.data.collection.selection.iterator.SelectionVectorIterator;
import cerberus.data.collection.selection.iterator.SelectionIterator;

/**
 * @author Michael Kalkusch
 *
 */
public class SetMultiDim 
extends CollectionThreadItem
implements Set {

	/**
	 * Since only one Selections is stored only one 
	 * MetaData object is needed. 
	 */
	protected CollectionMetaData refMetaDataAllAndAny = null;
	
	protected Vector< Vector<Selection> > vecSelectionDim;
	
	protected Vector< Vector<Storage> > vecStorageDim;
	
	/**
	 * Store reference to the Selection.
	 */
	//protected Selection[] refFlatSelection = null;
	
	/**
	 * Store reference to the Storages.
	 */
	//protected Storage[] refFlatStorage = null;
	
	/**
	 * Variable for the dimension of this set.
	 */
	protected int iSizeDimension = 0;

	/**
	 * 
	 */
	public SetMultiDim( int iSetCollectionId, 
			GeneralManager setGeneralManager,
			CollectionLock setCollectionLock,
			final int iSetDimension ) {

		super( iSetCollectionId, setGeneralManager, setCollectionLock );
		
		iSizeDimension = iSetDimension;
		
		vecSelectionDim = new Vector< Vector<Selection> > (iSetDimension);
		vecStorageDim = new Vector< Vector<Storage> > (iSetDimension);
		
		for (int i=0; i<iSetDimension; i++) {
			vecSelectionDim.addElement( new Vector<Selection> (2) );
			vecStorageDim.addElement( new Vector<Storage> (2) );
		}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#setSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean setSelectionByDim(Selection[] addSelection, int iAtDimension) {		
		
		assert addSelection != null: "setStorage() with null-pointer";
		
		Vector <Selection> bufferInsertVector = 
			new Vector <Selection> (addSelection.length);
		
		for ( int i=0; i < addSelection.length; i++ ) {
			bufferInsertVector.addElement( addSelection[i] );
		}
		
		setSelectionVectorByDim( bufferInsertVector , iAtDimension );
		
		return true;
	}
	
	public boolean setSelectionByDimAndIndex( final Selection addSelection, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		vecSelectionDim.get( iAtDimension ).setElementAt( addSelection, iAtIndex );
		
		return true;
	}
	
	public boolean addSelectionByDim( final Selection addSelection, 
			final int iAtDimension ) {
		
		vecSelectionDim.get( iAtDimension ).addElement( addSelection );
		
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#setStorageByDimAndIndex(cerberus.data.collection.Storage, int, int)
	 */
	public boolean setStorageByDimAndIndex( final Storage addStorage, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		vecStorageDim.get( iAtDimension ).setElementAt( addStorage, iAtIndex );
		
		return true;
	}
	
	public boolean addStorageByDim( final Storage addStorage, 
			final int iAtDimension ) {
		
		Vector <Storage> buffer = vecStorageDim.get( iAtDimension );
		
		buffer.addElement( addStorage );
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#removeSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean removeSelection( final Selection removeSelection, final int iFromDimension) {
		
		Vector <Selection> bufferVectorSelection = vecSelectionDim.get( iFromDimension );
		
		return bufferVectorSelection.removeElement( removeSelection );		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean hasSelection(Selection testSelection, int iAtDimension) {
		
		assert testSelection != null: "SetFlatSimple.hasSelection() test with null pointer!";
		
		return vecSelectionDim.get( iAtDimension ).contains( testSelection );	
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasSelectionInSet(cerberus.data.collection.Selection)
	 */
	public boolean hasSelectionInSet(Selection testSelection) {
		
		Iterator <Vector <Selection> > iterSelection = vecSelectionDim.iterator();
		
		while ( iterSelection.hasNext() ) {		
						
			Vector <Selection> vecInnerSelection = iterSelection.next();
			
			if ( vecInnerSelection.contains( testSelection ) ) {
				return true;
			}
		
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getDimensionSizeForAllSelections()
	 */
	public int[] getDimensionSizeForAllSelections() {
		
		//FIXME what shall that function do?
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getDimensionSize(int)
	 */
	public int getDimensionSize(int iAtDimension) {
		
		Iterator <Selection> iter = 
			vecSelectionDim.get( iAtDimension ).iterator();
		
		int iLength = 0;
		while ( iter.hasNext() ) {
			iLength += iter.next().length();
		}
		
		return iLength;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getDimensions()
	 */
	public int getDimensions() {
		return this.vecSelectionDim.size();
	}

//	/* (non-Javadoc)
//	 * @see cerberus.data.collection.Set#setDimensionSize(int, int)
//	 */
//	public void setDimensionSize(int iIndexDimension, int iValueDimensionSize) {
//		//FIXME what shall that function do?
//		iSizeDimension = iValueDimensionSize;
//	}





	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getSelection(int)
	 */
	public Selection[] getSelectionByDim(int iAtDimension) {
		
		Vector <Selection> buffer = vecSelectionDim.get( iAtDimension );
		Iterator <Selection> iter = buffer.iterator();
		
		Selection[] resultBuffer = new Selection[buffer.size()];
		
		for ( int i=0; iter.hasNext(); i++ ) {
			resultBuffer[i] = iter.next();
		}
		return resultBuffer;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#getSelectionByDimAndIndex(int, int)
	 */
	public Selection getSelectionByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		
		return this.vecSelectionDim.get(iAtDimension).get(iAtIndex);
	}
	


	/**
	 * Since only one selection is stored only one MetaData obejct is used.
	 * 
	 * @param setMetaData sets the meta data
	 * @see cerberus.data.collection.CollectionMetaDataInterface#setMetaData(cerberus.data.collection.CollectionMetaData)
	 * 
	 */
	public void setMetaData(CollectionMetaData setMetaData) {
		
		assert setMetaData != null :"setMetaData() with null-pointer.";
		
		refMetaDataAllAndAny = setMetaData;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.CollectionMetaDataInterface#getMetaData()
	 */
	public CollectionMetaData getMetaData() {
		return refMetaDataAllAndAny;
	}
	
	/**
	 * Since only one selection is stored only one MetaData obejct is used.
	 * 
	 * @return curretn meta data
	 * 
	 * @see cerberus.data.collection.MetaDataSetInterface#getMetaDataAny()
	 */
	public CollectionMetaData getMetaDataAny() {
		return refMetaDataAllAndAny;
	}

	/**
	 * Since only one selection is stored only one MetaData obejct is used.
	 * 
	 * @param sets the meta data
	 * 
	 * @see cerberus.data.collection.MetaDataSetInterface#setMetaDataAny(cerberus.data.collection.CollectionMetaData)
	 */
	public void setMetaDataAny(CollectionMetaData setMetaData) {
		setMetaData( setMetaData );
	}

	/**
	 * No subsets are available.
	 * 
	 * @see cerberus.data.collection.SubSet#getSubSets()
	 */
	public Set[] getSubSets() {
		
		assert false: "SetFlatSimple.getSubSets() SetFlatSimple does not supper SubSet's.";
	
		return null;
	}

	/**
	 * No subsets are available.
	 * 
	 * @see cerberus.data.collection.SubSet#hasSubSets()
	 */
	public boolean hasSubSets() {
		return false;
	}

	/**
	 * No subsets are available.
	 * 
	 * @see cerberus.data.collection.SubSet#addSubSet(cerberus.data.collection.Set)
	 */
	public boolean addSubSet(Set addSet) {
		throw new RuntimeException("SetFlatSimple.addSubSet() SetFlatSimple does not supper SubSet's.");
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.SubSet#swapSubSet(cerberus.data.collection.Set, cerberus.data.collection.Set)
	 */
	public boolean swapSubSet(Set fromSet, Set toSet) {
		
		assert false: "SetFlatSimple.swapSubSet() SetFlatSimple does not supper SubSet's.";
	
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.SubSet#removeSubSet(cerberus.data.collection.Set)
	 */
	public boolean removeSubSet(Set addSet) {

		assert false: "SetFlatSimple.removeSubSet() SetFlatSimnple does not supper SubSet's.";
	
		return false;
	}
	
	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final DParseSaxHandler refSaxHandler ) {
		
		return false;
		
//		try {
//			CollectionSetParseSaxHandler parser = 
//				(CollectionSetParseSaxHandler) refSaxHandler;
//			
//			if ( parser.getDim() < 0 ) {
//				assert false:"Parser does not return informations neede";
//				return false;
//			}
//			/**
//			 * Stores the a list of Selections and Storages used by this set.
//			 */
//			final int[] iListOfSellectionId = parser.getSelectByDim( 0 );
//			final int[] iListOfStorageId = parser.getStorageByDim( 0 );
//			
//			
//			/**
//			 * Store reference to the Selections.
//			 */
//			refFlatSelection = new Selection[iListOfSellectionId.length];
//			
//			for ( int iIndex=0; iIndex< iListOfSellectionId.length ; iIndex++ ) {
//				
//				try {					
//					Object buffer = getManager().getItem( iListOfSellectionId[iIndex] );
//					refFlatSelection[iIndex] = (Selection) buffer;
//				}
//				catch ( NullPointerException npe) {
//					npe.printStackTrace();
//					throw npe; 
//				}
//			}
//			
//			/**
//			 * Store reference to the Storages.
//			 */
//			refFlatStorage = new Storage[iListOfStorageId.length];
//			
//			for ( int iIndex=0; iIndex< iListOfStorageId.length ; iIndex++ ) {
//				
//				try {					
//					Object buffer = getManager().getItem( iListOfStorageId[iIndex] );
//					refFlatStorage[iIndex] = (Storage) buffer;
//				}
//				catch ( NullPointerException npe) {
//					npe.printStackTrace();
//					throw npe; 
//				}
//			}
//			
//			getManager().unregisterItem( getId(), 
//					ManagerObjectType.SET_LINEAR );
//			
//			getManager().registerItem( this, 
//					parser.getXML_DataComponent_Id(), 
//					ManagerObjectType.SET_LINEAR );
//			
//			setId( parser.getXML_DataComponent_Id() );		
//			
//			return true;
//		}
//		catch (NullPointerException npe) {
//			return false;
//		}
		
	}
	
	/**
	 * @see cerberus.data.xml.MementoXML#createMementoXML()
	 * @return String containing all information on the state 
	 * of the object in XML form with out a header.
	 */
	public String createMementoXML() {
		
//		final String openDetail = "<DataComponentItemDetails type=\"";
//		final String closeDetail = "</DataComponentItemDetails>\n";
		
		//FIXME Memento is not created yet!
		
		assert false:"Memento of Set is not created yet!";
		
		return createMementoXML_Intro(
				ManagerObjectType.SELECTION_SINGLE_BLOCK.name())
			+ "</DataComponentItem>\n";
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.SET_MULTI_DIM;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getStorage()
	 */
	public final Storage[] getStorageByDim( final int iAtDimension ) {
		return null;
	}
	
	protected final Vector<Storage> getStorageVectorByDim( final int iAtDimension ) {
		return vecStorageDim.get( iAtDimension );
	}
	
	protected final Vector<Selection> getSelectionVectorByDim( final int iAtDimension ) {
		return vecSelectionDim.get( iAtDimension );
	}
	
	/**
	 * Test is a certain index to address a Storage is avlid.
	 * 
	 * @param iAtDimension Dimension of Storage
	 * @param iAtIndex index inside Dimension
	 * 
	 * @return TRUE if a Storage is present at this index.
	 */
	public final boolean hasStorageByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		if (( iAtDimension >= 0 )&&
				( iAtIndex >= 0 )&&
				( iAtDimension < vecStorageDim.size() )) {
			
			if ( iAtIndex < vecStorageDim.get(iAtDimension).size() ) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Test is a certain index to address a Selection is avlid.
	 * 
	 * @param iAtDimension Dimension of Selection
	 * @param iAtIndex index inside Dimension
	 * 
	 * @return TRUE if a Selection is present at this index.
	 */
	public final boolean hasSelectionByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		if (( iAtDimension >= 0 )&&
				( iAtIndex >= 0 )&&
				( iAtDimension < vecSelectionDim.size() )) {
			
			if ( iAtIndex < vecSelectionDim.get(iAtDimension).size() ) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public final Storage getStorageByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		return vecStorageDim.get( iAtDimension ).get( iAtIndex );
	}
	
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#setStorage(cerberus.data.collection.Storage)
	 */
	public final void setStorageByDim(Storage[] setStorage, final int iAtDimension ) {
		
		assert setStorage != null: "setStorage() with null-pointer";
		
		Vector <Storage> bufferInsertVector = new Vector <Storage> (setStorage.length);
		
		for ( int i=0; i < setStorage.length; i++ ) {
			bufferInsertVector.addElement( setStorage[i] );
		}
		
		this.setStorageVectorByDim( bufferInsertVector , iAtDimension );
	}
	
	protected final void setStorageVectorByDim( Vector <Storage> setVecStorage, final int iAtDimension ) {
		
		assert setVecStorage != null: "setStorageVectorByDim() with null-pointer";
		
		this.vecStorageDim.setElementAt( setVecStorage, iAtDimension );

	}
	
	protected final void setSelectionVectorByDim( Vector <Selection> setVecSelection, final int iAtDimension ) {
		
		assert setVecSelection != null: "setStorageVectorByDim() with null-pointer";
		
		this.vecSelectionDim.setElementAt( setVecSelection, iAtDimension );

	}
	
	/**
	 * Create "Header" for all Selections.
	 * 
	 * @see cerberus.data.xml.MementoXML#createMementoXML()
	 * 
	 * @return String containign the XML-header for this selection
	 */
	protected final String createMementoXML_Intro( 
			final String sSelectionType ) {		
		
		return "<DataComponentItem data_Id=\""
			+ getId() + 
			"\" type=\"" +
			sSelectionType + "\">\n";
	}
	
	/**
	 * Sets internal CacheId.
	 * 
	 * @see getCacheId()
	 * 
	 * @param iCompareAndSet cacheID to be compared with internal cacheId
	 */
	private void setInternalCacheId( final int iCompareAndSet ) {
		if ( iCacheId < iCompareAndSet ) {
			iCacheId = iCompareAndSet;
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.CollectionInterface#getCacheId()
	 */
	public int getCacheId() {
		
		Iterator <Vector <Selection>> iterSelection = vecSelectionDim.iterator();		
		while ( iterSelection.hasNext() ) {
			
			Iterator <Selection> iterInnerSelect = iterSelection.next().iterator();
			
			while ( iterInnerSelect.hasNext() ) {
				setInternalCacheId( iterInnerSelect.next().getCacheId() );
			}
		}
		
		
		Iterator <Vector <Storage>> iterStorage = vecStorageDim.iterator();
		while ( iterStorage.hasNext() ) {
			
			Iterator <Storage> iterInnerStore = iterStorage.next().iterator();
			
			while ( iterInnerStore.hasNext() ) {
				setInternalCacheId( iterInnerStore.next().getCacheId() );
			}
		}
		
		return this.iCacheId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.CollectionThreadObject#hasCacheChanged(int)
	 */
	public boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId < this.getCacheId());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasCacheChangedReadOnly(int)
	 */
	public final boolean hasCacheChangedReadOnly( final int iCompareCacheId ) {
		return (iCompareCacheId > this.iCacheId);
	}
	

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#iteratorSelection()
	 */
	public SelectionIterator iteratorSelectionByDim( final int iAtDimension ) {
		
//		Vector<Selection> bufferVecSelection = 
//			vecSelectionDim.get( iAtDimension );
		
		SelectionVectorIterator iterator = 
			new SelectionVectorIterator();
		
		iterator.addSelectionVector( vecSelectionDim.get( iAtDimension ) );
		
		return iterator;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#iteratorStorage()
	 */
	public Iterator<Storage> iteratorStorageByDim( final int iAtDimension ) {
			
		Vector<Storage> vec_Storage = vecStorageDim.get( iAtDimension );
		
		return vec_Storage.iterator();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#removeSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean removeSelection( final Selection[] removeSelection, final int iFromDimension) {
		
		Vector <Selection> bufferVecSelection = vecSelectionDim.get( iFromDimension );
		
		boolean bAllElementsRemoved = true;
		
		for ( int i=0; i< removeSelection.length; i++ ) {
			
			if ( ! bufferVecSelection.removeElement(  removeSelection[iFromDimension] )) {
				bAllElementsRemoved = false;
			}
		}
		
		return bAllElementsRemoved;		
	}
}
