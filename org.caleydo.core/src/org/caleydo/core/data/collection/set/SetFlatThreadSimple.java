package org.caleydo.core.data.collection.set;

import java.util.Iterator;
import java.util.Vector;

import org.caleydo.core.data.collection.IMetaData;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.collection.parser.CollectionSetSaxParserHandler;
import org.caleydo.core.data.collection.thread.lock.ICollectionLock;
import org.caleydo.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import org.caleydo.core.data.collection.virtualarray.iterator.VirtualArrayVectorIterator;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.xml.sax.ISaxParserHandler;

/**
 * @author Michael Kalkusch
 *
 */
public class SetFlatThreadSimple 
extends ASetRawData
implements ISet {

	/**
	 * Since only one Selections is stored only one 
	 * MetaData object is needed. 
	 */
	protected IMetaData metaDataAllAndAny = null;
	
	/**
	 * Store reference to the IVirtualArray.
	 */
	protected IVirtualArray[] flatSelection = null;
	
	/**
	 * Store reference to the Storages.
	 */
	protected IStorage[] flatStorage = null;
	
	/**
	 * Variable for the dimension of this set.
	 */
	protected int iSizeDimension = 0;

	/**
	 * Constructor.
	 */
	public SetFlatThreadSimple( int iSetCollectionId, 
			final IGeneralManager setGeneralManager,
			ICollectionLock setCollectionLock,
			final SetType setType) {

		super( iSetCollectionId, 
				setGeneralManager, 
				setCollectionLock,
				setType);
		
		flatSelection = new IVirtualArray[1];
		
		flatStorage = new IStorage[1];
	}


	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setSelection(org.caleydo.core.data.collection.IVirtualArray, int)
	 */
	public boolean setVirtualArrayByDim(IVirtualArray[] addVirtualArray, int iAtDimension) {		
		
		if ( iAtDimension >= flatSelection.length) {
			assert false :"Can not address dimension != 0";
		}
		//FIXME Test Range!
		flatSelection[iAtDimension] = addVirtualArray[0];
		
		return true;
	}
	
	public boolean setVirtualArrayByDimAndIndex( final IVirtualArray addVirtualArray, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		if ( iAtDimension >= flatSelection.length) {
			assert false :"Can not address dimension != 0";
		}
		
		if ( flatSelection.length < iAtIndex) {
			/* create a new IVirtualArray[] and copy to new IVirtualArray[]... */
			IVirtualArray[] copyToNewSelectionArray = new IVirtualArray[iAtIndex+1];
			
			for ( int i=0; i < flatSelection.length; i++ ) {				
				copyToNewSelectionArray[i] = flatSelection[i];
			}
			flatSelection = copyToNewSelectionArray;
		}
		
		flatSelection[iAtIndex] = addVirtualArray;
		
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setStorageByDimAndIndex(org.caleydo.core.data.collection.IStorage, int, int)
	 */
	public boolean setStorageByDimAndIndex( final IStorage addStorage, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		if ( iAtDimension >= flatStorage.length) {
			assert false :"Can not address dimension != 0";
		}
		
		if ( flatStorage.length < iAtIndex) {
			/* create a new IVirtualArray[] and copy to new IVirtualArray[]... */
			IStorage[] copyToNewSelectionArray = new IStorage[iAtIndex+1];
			
			for ( int i=0; i < flatStorage.length; i++ ) {				
				copyToNewSelectionArray[i] = flatStorage[i];
			}
			flatStorage = copyToNewSelectionArray;
		}
		
		flatStorage[iAtIndex] = addStorage;
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#removeSelection(org.caleydo.core.data.collection.IVirtualArray, int)
	 */
	public boolean removeVirtualArray( final IVirtualArray[] removeVirtualArray, final int iFromDimension) {
		
		for ( int i=0; i< removeVirtualArray.length; i++ ) {
			if ( flatSelection[iFromDimension] == removeVirtualArray[i] ) {
				flatSelection[iFromDimension] = null;
				return true;
			}
		}
		return false;		
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#hasSelection(org.caleydo.core.data.collection.IVirtualArray, int)
	 */
	public boolean hasVirtualArray(IVirtualArray testVirtualArray, int iAtDimension) {
		
		assert testVirtualArray != null: "SetFlatSimple.hasSelection() test with null pointer!";
		
		//FIXME add range check...
		if ( flatSelection[iAtDimension] ==  testVirtualArray ) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#hasSelectionInSet(org.caleydo.core.data.collection.IVirtualArray)
	 */
	public boolean hasVirtualArrayInSet(IVirtualArray testVirtualArray) {
		
		//FIXME return index instead of true or false
		
		for ( int iIndex=0; iIndex < this.flatSelection.length ; iIndex++ ) {
			if ( flatSelection[iIndex] ==  testVirtualArray)
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getDimensionSizeForAllSelections()
	 */
	public int[] getDimensionSizeForAllVirtualArrays() {
		
		//FIXME what shall that function do?
		return null;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getDimensionSize(int)
	 */
	public int getDimensionSize(int iAtDimension) {
		//FIXME what shall that function do?
		assert flatSelection != null : "no IVirtualArray[] is set";
		assert flatSelection.length > 0 : "no IVirtualArray is set";
		
		return flatSelection[0].length();
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getDimensions()
	 */
	public int getDimensions() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setDimensionSize(int, int)
	 */
	public void setDimensionSize(int iIndexDimension, int iValueDimensionSize) {
		//FIXME what shall that function do?
		iSizeDimension = iValueDimensionSize;
	}





	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getSelection(int)
	 */
	public IVirtualArray[] getVirtualArrayByDim(int iAtDimension) {
		//FIXME add range check..
		return flatSelection;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getSelectionByDimAndIndex(int, int)
	 */
	public IVirtualArray getVirtualArrayByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		return flatSelection[iAtIndex];
	}
	


	/**
	 * Since only one selection is stored only one MetaData obejct is used.
	 * 
	 * @param setMetaData sets the meta data
	 * @see org.caleydo.core.data.collection.IMetaDataCollection#setMetaData(org.caleydo.core.data.collection.IMetaData)
	 * 
	 */
	public void setMetaData(IMetaData setMetaData) {
		
		assert setMetaData != null :"setMetaData() with null-pointer.";
		
		metaDataAllAndAny = setMetaData;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.IMetaDataCollection#getMetaData()
	 */
	public IMetaData getMetaData() {
		return metaDataAllAndAny;
	}
	
	/**
	 * Since only one selection is stored only one MetaData obejct is used.
	 * 
	 * @return curretn meta data
	 * 
	 * @see org.caleydo.core.data.collection.IMetaDataSet#getMetaData()
	 */
	public IMetaData getMetaDataAny() {
		return metaDataAllAndAny;
	}

	/**
	 * Since only one selection is stored only one MetaData obejct is used.
	 * 
	 * @param sets the meta data
	 * 
	 * @see org.caleydo.core.data.collection.IMetaDataSet#setMetaData(org.caleydo.core.data.collection.IMetaData)
	 */
	public void setMetaDataAny(IMetaData setMetaData) {
		setMetaData( setMetaData );
	}

	/**
	 * No subsets are available.
	 * 
	 * @see org.caleydo.core.data.collection.ISubSet#getSubSets()
	 */
	public ISet[] getSubSets() {
		
		assert false: "SetFlatSimple.getSubSets() SetFlatSimple does not supper ISubSet's.";
	
		return null;
	}

	/**
	 * No subsets are available.
	 * 
	 * @see org.caleydo.core.data.collection.ISubSet#hasSubSets()
	 */
	public boolean hasSubSets() {
		return false;
	}

	/**
	 * No subsets are available.
	 * 
	 * @see org.caleydo.core.data.collection.ISubSet#addSubSet(org.caleydo.core.data.collection.ISet)
	 */
	public boolean addSubSet(ISet addSet) {
		throw new RuntimeException("SetFlatSimple.addSubSet() SetFlatSimple does not supper ISubSet's.");
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISubSet#swapSubSet(org.caleydo.core.data.collection.ISet, org.caleydo.core.data.collection.ISet)
	 */
	public boolean swapSubSet(ISet fromSet, ISet toSet) {
		
		assert false: "SetFlatSimple.swapSubSet() SetFlatSimple does not supper ISubSet's.";
	
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISubSet#removeSubSet(org.caleydo.core.data.collection.ISet)
	 */
	public boolean removeSubSet(ISet addSet) {

		assert false: "SetFlatSimple.removeSubSet() SetFlatSimnple does not supper ISubSet's.";
	
		return false;
	}
	
	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param saxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final ISaxParserHandler saxHandler ) {
		
		try {
			CollectionSetSaxParserHandler parser = 
				(CollectionSetSaxParserHandler) saxHandler;
			
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
			flatSelection = new IVirtualArray[iListOfSellectionId.length];
			
			for ( int iIndex=0; iIndex< iListOfSellectionId.length ; iIndex++ ) {
				
				try {					
					Object buffer = generalManager.getVirtualArrayManager().getItem( iListOfSellectionId[iIndex] );
					flatSelection[iIndex] = (IVirtualArray) buffer;
				}
				catch ( NullPointerException npe) {
					npe.printStackTrace();
					throw npe; 
				}
			}
			
			/**
			 * Store reference to the Storages.
			 */
			flatStorage = new IStorage[iListOfStorageId.length];
			
			for ( int iIndex=0; iIndex< iListOfStorageId.length ; iIndex++ ) {
				
				try {					
					Object buffer = generalManager.getStorageManager().getItem( iListOfStorageId[iIndex] );
					flatStorage[iIndex] = (IStorage) buffer;
				}
				catch ( NullPointerException npe) {
					npe.printStackTrace();
					throw npe; 
				}
			}
			
			generalManager.getSetManager().unregisterItem( getId(), 
					ManagerObjectType.SET_LINEAR );
			
			getManager().getSetManager().registerItem(this, 
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
	 * @see org.caleydo.core.data.xml.IMementoXML#createMementoXML()
	 * @return String containing all information on the state 
	 * of the object in XML form with out a header.
	 */
	public String createMementoXML() {
		
//		final String openDetail = "<DataComponentItemDetails type=\"";
//		final String closeDetail = "</DataComponentItemDetails>\n";
		
		//FIXME IMemento is not created yet!
		
		assert false:"IMemento of ISet is not created yet!";
		
		return createMementoXML_Intro(
				ManagerObjectType.VIRTUAL_ARRAY.name())
			+ "</DataComponentItem>\n";
	}

	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.SET_LINEAR;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#getStorage()
	 */
	public final IStorage[] getStorageByDim( final int iAtDimension ) {
		return flatStorage;
	}
	
	public final IStorage getStorageByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		return flatStorage[iAtIndex];
	}
	
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#setStorage(org.caleydo.core.data.collection.IStorage)
	 */
	public final void setStorageByDim(IStorage[] setStorage, final int iAtDimension ) {
		
		assert setStorage != null: "setStorage() with null-pointer";
		
		flatStorage[0] = setStorage[0];
	}
	

	public final boolean setVirtualArrayByDim( Vector<IVirtualArray> setSelection, final int iAtDimension ) {
		
		assert setSelection != null: "setStorage() with null-pointer";
		
		flatSelection = new IVirtualArray[ setSelection.size() ];		
		Iterator <IVirtualArray> iter = setSelection.iterator();
		int iIndex = 0;
		
		while ( iter.hasNext() ) {
			flatSelection[ iIndex ] = iter.next();
		}
		
		return true;
	}
	
	public final boolean setStorageByDim( Vector<IStorage> setStorage, final int iAtDimension ) {
		
		assert setStorage != null: "setStorage() with null-pointer";
		
		flatStorage = new IStorage[setStorage.size()];		

		for (int iStorageIndex = 0; iStorageIndex < setStorage.size(); iStorageIndex++)
		{
			flatStorage[iStorageIndex] = setStorage.get(iStorageIndex);
		}
		
		return true;
	}
	
	/**
	 * Create "Header" for all Selections.
	 * 
	 * @see org.caleydo.core.data.xml.IMementoXML#createMementoXML()
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
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {
		
		if ( this.flatSelection != null ) {
			for ( int i=0; i<flatSelection.length;i++) {
				setCacheIdCompared( flatSelection[i].getCacheId());
			}
		}
		
		if ( this.flatStorage != null ) {
			for ( int i=0; i<flatStorage.length;i++) {
				setCacheIdCompared( flatStorage[i].getCacheId());
			}
		}
		
		return this.iCacheId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.CollectionThreadObject#hasCacheChanged(int)
	 */
	public boolean hasCacheChanged( final int iCompareCacheId ) {
		return (iCompareCacheId < this.getCacheId());
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#hasCacheChangedReadOnly(int)
	 */
	public final boolean hasCacheChangedReadOnly( final int iCompareCacheId ) {
		return (iCompareCacheId > this.iCacheId);
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#iteratorStorageByDim(int)
	 */
	public Iterator<IStorage> iteratorStorageByDim( final int iAtDimension ) {
		
		Vector<IStorage> vec_Storage = new Vector<IStorage> (flatStorage.length);
		
		for ( int i=0; i <flatStorage.length; i++ ) {					
			vec_Storage.addElement( flatStorage[i] );
		}
		
		return vec_Storage.iterator();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISet#iteratorSelectionByDim(int)
	 */
	public IVirtualArrayIterator iteratorVirtualArrayByDim( final int iAtDimension ) {
		Vector<IVirtualArray> vec_Selection = 
			new Vector<IVirtualArray> (flatSelection.length);
		
		for ( int i=0; i <flatSelection.length; i++ ) {					
			vec_Selection.addElement( flatSelection[i] );
		}
		
		VirtualArrayVectorIterator iterator = new VirtualArrayVectorIterator();
		iterator.addSelectionVector( vec_Selection );
		
		return iterator;
	}
	
	public final Vector<IStorage> getStorageVectorByDim( final int iAtDimension ) {
		
		Vector<IStorage> resultVector = new Vector<IStorage> (flatStorage.length);
		
		for ( int i=0; i < flatStorage.length; i++ ) {
			resultVector.addElement( flatStorage[i] );
		}
		
		return resultVector;
	}
	
	public final Vector<IVirtualArray> getVirtualArrayVectorByDim( final int iAtDimension ) {
		
		Vector<IVirtualArray> resultVector = new Vector<IVirtualArray> (flatSelection.length);
		
		for ( int i=0; i < flatSelection.length; i++ ) {
			resultVector.addElement( flatSelection[i] );
		}
		
		return resultVector;
	}

}
