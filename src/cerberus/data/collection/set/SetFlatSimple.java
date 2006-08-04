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

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.CollectionMetaData;
import cerberus.data.collection.Selection;
import cerberus.data.collection.Storage;
import cerberus.data.collection.Set;
import cerberus.data.collection.parser.CollectionSetParseSaxHandler;
import cerberus.data.collection.selection.iterator.SelectionIterator;
//import cerberus.data.collection.selection.iterator.SelectionVectorIterator;
import cerberus.data.collection.set.SetSimpleBase;
import cerberus.xml.parser.DParseSaxHandler;

/**
 * Threadsafe set.
 * 
 * @author Michael Kalkusch
 * 
 * @deprecated Use SetFlatThreadSimple
 */
public class SetFlatSimple 
extends SetSimpleBase
implements Set {

	/**
	 * Since only one Selections is stored only one 
	 * MetaData object is needed. 
	 */
	protected CollectionMetaData refMetaDataAllAndAny = null;
	
	/**
	 * Store reference to the Selection.
	 */
	protected Selection[] refFlatSelection = null;
	
	/**
	 * Store reference to the Storages.
	 */
	protected Storage[] refFlatStorage = null;
	
	/**
	 * Variable for the dimension of this set.
	 */
	protected int iSizeDimension = 0;
	

	/**
	 * 
	 */
	public SetFlatSimple( int iSetCollectionId, GeneralManager setGeneralManager) {

		super( iSetCollectionId, setGeneralManager );
		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#setSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean setSelectionByDim(Selection[] addSelection, int iAtDimension) {
		if ( refFlatSelection != null ) {
			refFlatSelection = null;
		}
		
		//FIXME Test Range!
		refFlatSelection = addSelection;
		
		return true;
	}
	
	public boolean setSelectionByDimAndIndex( final Selection addSelection, 
			final int iAtDimension, 
			final int iAtIndex ) {
	
		refFlatSelection[iAtIndex] = addSelection;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#removeSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean removeSelection( final Selection[] removeSelection, final int iFromDimension) {
		
		for ( int i=0; i< removeSelection.length; i++ ) {
			if ( refFlatSelection[iFromDimension] == removeSelection[i] ) {
				refFlatSelection[iFromDimension] = null;
				return true;
			}
		}
		return false;		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean hasSelection(Selection testSelection, int iAtDimension) {
		
		assert testSelection != null: "SetFlatSimple.hasSelection() test with null pointer!";
		
		//FIXME add range check...
		if ( refFlatSelection[iAtDimension] ==  testSelection ) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasSelectionInSet(cerberus.data.collection.Selection)
	 */
	public boolean hasSelectionInSet(Selection testSelection) {
		
		//FIXME return index instead of true or false
		
		for ( int iIndex=0; iIndex < this.refFlatSelection.length ; iIndex++ ) {
			if ( refFlatSelection[iIndex] ==  testSelection)
				return true;
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
		//FIXME what shall that function do?
		return iSizeDimension;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getDimensions()
	 */
	public int getDimensions() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#setDimensionSize(int, int)
	 */
	public void setDimensionSize(int iIndexDimension, int iValueDimensionSize) {
		//FIXME what shall that function do?
		iSizeDimension = iValueDimensionSize;
	}





	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getSelection(int)
	 */
	public Selection[] getSelectionByDim(int iAtDimension) {
		//FIXME add range check..
		return refFlatSelection;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#getSelectionByDimAndIndex(int, int)
	 */
	public Selection getSelectionByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		return refFlatSelection[iAtIndex];
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
		
		try {
			CollectionSetParseSaxHandler parser = 
				(CollectionSetParseSaxHandler) refSaxHandler;
			
			if ( parser.getDim() < 0 ) {
				assert false:"Parser does not return informations neede";
				return false;
			}
			/**
			 * Stores the a list of Selections and Storages used by this set.
			 */
			final int[] iListOfSellectionId = parser.getSelectByDim( 0 );
			final int[] iListOfStorageId = parser.getStorageByDim( 0 );
			
			
			/**
			 * Store reference to the Selections.
			 */
			refFlatSelection = new Selection[iListOfSellectionId.length];
			
			for ( int iIndex=0; iIndex< iListOfSellectionId.length ; iIndex++ ) {
				
				try {					
					Object buffer = getManager().getItem( iListOfSellectionId[iIndex] );
					refFlatSelection[iIndex] = (Selection) buffer;
				}
				catch ( NullPointerException npe) {
					npe.printStackTrace();
					throw npe; 
				}
			}
			
			/**
			 * Store reference to the Storages.
			 */
			refFlatStorage = new Storage[iListOfStorageId.length];
			
			for ( int iIndex=0; iIndex< iListOfStorageId.length ; iIndex++ ) {
				
				try {					
					Object buffer = getManager().getItem( iListOfStorageId[iIndex] );
					refFlatStorage[iIndex] = (Storage) buffer;
				}
				catch ( NullPointerException npe) {
					npe.printStackTrace();
					throw npe; 
				}
			}
			
			getManager().unregisterItem( getId(), 
					ManagerObjectType.SET_LINEAR );
			
			getManager().registerItem( this, 
					parser.getXML_DataComponent_Id(), 
					ManagerObjectType.SET_LINEAR );
			
			setId( parser.getXML_DataComponent_Id() );		
			
			return true;
		}
		catch (NullPointerException npe) {
			return false;
		}
		
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
		return ManagerObjectType.SET_LINEAR;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getStorage()
	 */
	public final Storage[] getStorageByDim( final int iAtDimension ) {
		return refFlatStorage;
	}
	
	public final Storage getStorageByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		if ( refFlatStorage != null ) 
			return refFlatStorage[iAtIndex];
		return null;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#setStorage(cerberus.data.collection.Storage)
	 */
	public final void setStorageByDim(Storage[] setStorage, final int iAtDimension ) {
		
		assert setStorage != null: "setStorage() with null-pointer";
		
		refFlatStorage = setStorage;
	}
	
	public boolean setStorageByDimAndIndex( final Storage addStorage, 
			final int iAtDimension, 
			final int iAtIndex ) {
	
		this.refFlatStorage[iAtIndex] = addStorage;
		
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.CollectionInterface#getCacheId()
	 */
	public int getCacheId() {
		
		assert false : "not implemented update yet";
	
//		if ( this.refFlatSelection != null ) {
//			for ( int i=0; i<refFlatSelection.length;i++) {
//				setCacheIdCompared( refFlatSelection[i].getCacheId());
//			}
//		}
//		
//		if ( this.refFlatStorage != null ) {
//			for ( int i=0; i<refFlatStorage.length;i++) {
//				setCacheIdCompared( refFlatStorage[i].getCacheId());
//			}
//		}
		
		return this.iCacheId;
	}
	
	public Iterator<Selection> iteratorSelection() {
		
		Vector<Selection> vec_Selection = new Vector<Selection> (refFlatSelection.length);
		
		for ( int i=0; i <refFlatSelection.length; i++ ) {					
			vec_Selection.addElement( refFlatSelection[i] );
		}
		
		return vec_Selection.iterator();
	}
	
	public Iterator<Storage> iteratorStorage() {
		
		Vector<Storage> vec_Storage = new Vector<Storage> (refFlatStorage.length);
		
		for ( int i=0; i <refFlatStorage.length; i++ ) {					
			vec_Storage.addElement( refFlatStorage[i] );
		}
		
		return vec_Storage.iterator();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#iteratorStorageByDim(int)
	 */
	public Iterator<Storage> iteratorStorageByDim( final int iAtDimension ) {
		
		return null;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#iteratorSelectionByDim(int)
	 */
	public SelectionIterator iteratorSelectionByDim( final int iAtDimension ) {
		
		return null;
	}
	
	public boolean setSelectionByDim( final Vector<Selection> addSelection, 
			final int iAtDimension ) {
		
		Selection [] setNewArray = (Selection []) addSelection.toArray();
		
		refFlatSelection = setNewArray;
		
		return true;
	}
	
	public boolean setStorageByDim( final Vector<Storage> addStorage, 
			final int iAtDimension ) {
		
		Storage [] setNewArray = (Storage []) addStorage.toArray();
		
		refFlatStorage = setNewArray;
		
		return true;
	}
	
	public Vector<Storage> getStorageVectorByDim( final int iAtDimension ) {
		 int iLengthStorage = refFlatStorage.length;
		 
		 Vector<Storage> vecResult = new Vector<Storage> (iLengthStorage);
		 
		 for ( int i=0; i< iLengthStorage; i++) {
			 vecResult.addElement( refFlatStorage[i] );
		 }
		 
		 return vecResult;
	}

	public Vector<Selection> getSelectionVectorByDim( final int iAtDimension ) {
		int iLengthSelection = refFlatSelection.length;
		
		Vector<Selection> vecResult = new Vector<Selection> (iLengthSelection);
		 
		for ( int i=0; i< iLengthSelection; i++) {
			vecResult.addElement( refFlatSelection[i] );
			}
 
		 return vecResult;
	}

	
}
