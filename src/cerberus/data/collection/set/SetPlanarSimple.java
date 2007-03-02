/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.set;

//import java.util.LinkedList;
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.IMetaData;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.SetType;
import cerberus.data.collection.parser.CollectionSetSaxParserHandler;
import cerberus.data.collection.set.ASetSimple;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
import cerberus.data.collection.virtualarray.iterator.VirtualArrayVectorIterator;
import cerberus.xml.parser.ISaxParserHandler;

/**
 * Defines a Planar 2-dimensional set. 
 * 
 * Only one IStorage can be used.
 * 
 * @author Michael Kalkusch
 *
 */
public class SetPlanarSimple 
extends ASetSimple
implements ISet {


	
	/**
	 *  Stores state of each IVirtualArray combined by AND.
	 */
	protected IMetaData refMetaDataAll = null;
	
	/**
	 * Stores state of each IVirtualArray combined by OR.
	 */
	protected IMetaData refMetaDataAny = null;
	
//	/**
//	 * Store reference to the only IStorage.
//	 */
//	protected IStorage refStorage = null;
	
	/**
	 * Store reference to the IVirtualArray.
	 */
	protected Vector < Vector<IVirtualArray> >vecRefSelection_Array;
	
	/**
	 * Store reference to the IStorage.
	 */
	protected Vector< Vector<IStorage> > vecRefStorage_Array;
	
	//private int iDimensionOfSet = 2;
	
	/**
	 * Variable for the dimension of this set.
	 */
	protected int[] iSizeDimension = {0,0};
	
	/**
	 * Constructor.
	 * 
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 * @param setType
	 */
	public SetPlanarSimple( int iSetCollectionId, 
			final IGeneralManager setGeneralManager,
			final SetType setType) {
		
		super( iSetCollectionId, 
				setGeneralManager,
				setType);
		
		vecRefSelection_Array = new Vector< Vector<IVirtualArray> > (2);
		vecRefSelection_Array.add(0, new Vector<IVirtualArray> (2));
		vecRefSelection_Array.add(1, new Vector<IVirtualArray> (2));
		
		vecRefStorage_Array = new Vector< Vector<IStorage> > (2);
		vecRefStorage_Array.add(0, new Vector<IStorage> (2));
		vecRefStorage_Array.add(1, new Vector<IStorage> (2));
	}
	
	/**
	 * Constructor.
	 */
	public SetPlanarSimple( int iSetCollectionId, 
			final IGeneralManager setGeneralManager) {
		
		this( iSetCollectionId, 
				setGeneralManager,
				SetType.SET_RAW_DATA);				
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setSelection(cerberus.data.collection.IVirtualArray, int)
	 */
	public boolean setVirtualArrayByDim( final IVirtualArray[] addVirtualArray, final int iAtDimension) {
		
		Vector<IVirtualArray> vecSelectionBuffer = 
			vecRefSelection_Array.get(iAtDimension);
		
		/* reset size of vector to size of array */
		vecSelectionBuffer.setSize( addVirtualArray.length );
	
		for ( int i=0; i < addVirtualArray.length ; i++ ) {
			vecSelectionBuffer.setElementAt( addVirtualArray[i], i );
		}
		
		/* assing result..*/
		vecRefSelection_Array.setElementAt(vecSelectionBuffer,iAtDimension);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setSelection(cerberus.data.collection.IVirtualArray, int)
	 */
	public void setStorageByDim( final IStorage[] addStorage, final int iAtDimension) {
		
		Vector<IStorage> vecStoreBuffer = 
			vecRefStorage_Array.get(iAtDimension);
		
		/* reset size of vector to size of array */
		vecStoreBuffer.setSize( addStorage.length );
	
		for ( int i=0; i < addStorage.length ; i++ ) {
			vecStoreBuffer.setElementAt( addStorage[i], i );
		}
		
		/* assing result..*/
		vecRefStorage_Array.setElementAt(vecStoreBuffer,iAtDimension);
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setStorageByDimAndIndex(cerberus.data.collection.IStorage, int, int)
	 */
	public boolean setStorageByDimAndIndex( final IStorage addStorage, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		vecRefStorage_Array.get(iAtDimension).insertElementAt(addStorage,iAtIndex);
		
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setStorageByDimAndIndex(cerberus.data.collection.IStorage, int, int)
	 */
	public boolean setVirtualArrayByDimAndIndex( final IVirtualArray addVirtualArray, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		vecRefSelection_Array.get(iAtDimension).insertElementAt(addVirtualArray,iAtIndex);
		
		return true;
	}
	
	public final boolean setStorageByDim( Vector <IStorage> setVecStorage, final int iAtDimension ) {
		
		assert setVecStorage != null: "setStorageVectorByDim() with null-pointer";
		
		this.vecRefStorage_Array.setElementAt( setVecStorage, iAtDimension );

		return true;
	}
	
	public final boolean setVirtualArrayByDim( Vector <IVirtualArray> setVecSelection, final int iAtDimension ) {
		
		assert setVecSelection != null: "setStorageVectorByDim() with null-pointer";
		
		this.vecRefSelection_Array.setElementAt( setVecSelection, iAtDimension );

		return true;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#removeSelection(cerberus.data.collection.IVirtualArray, int)
	 */
	public boolean removeVirtualArray(IVirtualArray[] removeVirtualArray, int iFromDimension) {
		
		Vector <IVirtualArray> bufferSelection = vecRefSelection_Array.get(iFromDimension);
		
		boolean bAllRemoved = true;
		for ( int i=0; i< removeVirtualArray.length; i++) {
			
			if ( ! bufferSelection.remove( removeVirtualArray[i] ) ) {
				bAllRemoved = false;
				
				
				System.err.println( "Can not remove IVirtualArray: " + 
						removeVirtualArray[i].toString() +
						" from ISet: " +
						this.toString() );
				assert false : "Can not remove IVirtualArray from ISet.";
			}
		} // end: for
		
		/* assing result .. */
		vecRefSelection_Array.setElementAt(bufferSelection,iFromDimension);
		
		return bAllRemoved;	
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#hasSelection(cerberus.data.collection.IVirtualArray, int)
	 */
	public boolean hasVirtualArray(IVirtualArray testVirtualArray, int iAtDimension) {
		
		return vecRefSelection_Array.get(iAtDimension).contains( testVirtualArray );		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#hasSelectionInSet(cerberus.data.collection.IVirtualArray)
	 */
	public boolean hasVirtualArrayInSet(IVirtualArray testVirtualArray) {
		if ( hasVirtualArray( testVirtualArray, 0 )) {
			return true;
		}
		return hasVirtualArray( testVirtualArray, 1 );
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getDimensionSizeForAllSelections()
	 */
	public int[] getDimensionSizeForAllVirtualArrays() {
		// TODO Auto-generated method stub
		//FIXME what shall this be used for?
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getDimensionSize(int)
	 */
	public int getDimensionSize(int iAtDimension) {
		// TODO Auto-generated method stub
		//FIXME what shall this be used for?
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getDimensions()
	 */
	public int getDimensions() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setDimensionSize(int, int)
	 */
	public void setDimensionSize(int iIndexDimension, int iValueDimensionSize) {
		// TODO Auto-generated method stub
		//	FIXME what shall this be used for?

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getSelection(int)
	 */
	public IVirtualArray[] getVirtualArrayByDim(int iAtDimension) {
		Vector <IVirtualArray> vec_Selection =
			vecRefSelection_Array.get( iAtDimension );
		
		IVirtualArray[] selResult = new IVirtualArray[vec_Selection.size()];
		
		Iterator <IVirtualArray> iter = vec_Selection.iterator();
		
		for ( int iIndex = 0; iter.hasNext(); iIndex++ ) {
			selResult[iIndex] = iter.next();
		}
		
		return selResult;
	}

	public IVirtualArray getVirtualArrayByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		
		return vecRefSelection_Array.get(iAtDimension).get(iAtIndex);		
	}
	
	public final IStorage[] getStorageByDim(int iAtDimension) {
		//	FIXME what shall this be used for?
		
		Vector <IStorage> buffer = vecRefStorage_Array.get(iAtDimension);
		
		assert buffer != null : "emtpy buffer in IStorage[] getStorageByDim(int iAtDimension)";
		
		Iterator <IStorage> iter = buffer.iterator();
		
		IStorage[] result = new IStorage[ buffer.size() ];
		
		for ( int i=0; iter.hasNext() ; i++ )
		{
			result[i] = iter.next();
		}
		
		return result;		
	}
	
//	public final LinkedList <IStorage> getStorageByDimLinkedList(int iAtDimension) {
//		//	FIXME what shall this be used for?
//		
//		return (IStorage[]) vecRefStorage_Array.get(iAtDimension).toArray();		
//	}
	
	public final IStorage getStorageByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		
		return vecRefStorage_Array.get(iAtDimension).get(iAtIndex);
	}

	public Vector<IVirtualArray> getSelectionArray(int iAtDimension) {
		return vecRefSelection_Array.get(iAtDimension);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataCollection#getMetaData()
	 */
	public IMetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataCollection#setMetaData(cerberus.data.collection.IMetaData)
	 */
	public void setMetaData(IMetaData setMetaData) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataSet#getMetaDataAny()
	 */
	public IMetaData getMetaDataAny() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.IMetaDataSet#setMetaDataAny(cerberus.data.collection.IMetaData)
	 */
	public void setMetaDataAny(IMetaData setMetaData) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#getSubSets()
	 */
	public ISet[] getSubSets() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#hasSubSets()
	 */
	public boolean hasSubSets() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#addSubSet(cerberus.data.collection.ISet)
	 */
	public boolean addSubSet(ISet addSet) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#swapSubSet(cerberus.data.collection.ISet, cerberus.data.collection.ISet)
	 */
	public boolean swapSubSet(ISet fromSet, ISet toSet) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#removeSubSet(cerberus.data.collection.ISet)
	 */
	public boolean removeSubSet(ISet addSet) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final ISaxParserHandler refSaxHandler ) {
		
		assert false : "not impelemted yet";
			
		try {
			CollectionSetSaxParserHandler parser = 
				(CollectionSetSaxParserHandler) refSaxHandler;
			
			if ( parser.getDim() < 0 ) {
				assert false:"Parser does not return informations neede";
				return false;
			}

			//final IGeneralManager refManger = getManager();
			
			getManager().unregisterItem( getId(), 
					ManagerObjectType.SET_PLANAR );
			
			getManager().registerItem( this, 
					parser.getXML_DataComponent_Id(), 
					ManagerObjectType.SET_PLANAR );
			
			setId( parser.getXML_DataComponent_Id() );			
			
			return true;
		}
		catch (NullPointerException npe) {
			return false;
		}
		
	}
	
	/**
	 * @see cerberus.data.xml.IMementoXML#createMementoXML()
	 * @return String containing all information on the state 
	 * of the object in XML form with out a header.
	 */
	public String createMementoXML() {
		
//		final String openDetail = "<DataComponentItemDetails type=\"";
//		final String closeDetail = "</DataComponentItemDetails>\n";
		
		//FIXME IMemento is not created yet!
		
		assert false:"IMemento of ISet is not created yet!";
		
		return createMementoXML_Intro(
				ManagerObjectType.VIRTUAL_ARRAY_SINGLE_BLOCK.name())
			+ "</DataComponentItem>\n";
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.UniqueManagedInterface#getBaseType()
	 */
	public ManagerObjectType getBaseType() {
		return ManagerObjectType.SET_PLANAR;
	}
	
	/**
	 * @see cerberus.data.collection.ICollection#getCacheId()
	 */
	public int getCacheId() {
		
		//assert false : "not implemented update yet";
	    //TODO check if storage need to be tested also?
		
		SetUpdateChacheId updateSelection = new SetUpdateChacheId <IVirtualArray> () ;		
		iCacheId = updateSelection.updateCacheId( vecRefSelection_Array,
				iCacheId );
		
		SetUpdateChacheId updateStorage = new SetUpdateChacheId <IStorage> ();		
		iCacheId = updateSelection.updateCacheId( vecRefStorage_Array,
				iCacheId );
		
		return this.iCacheId;
	}

	/**
	 * Get an Iterator for all IStorage used in this ISet.
	 * 
	 * @param iAtDimension requested dimension
	 * @return IStorage bound to this dimension
	 *
	 * @see cerberus.data.collection.ISet#iteratorVirtualArrayByDim(int)
	 */
	public IVirtualArrayIterator iteratorVirtualArrayByDim( final int iAtDimension ) {
		Vector<IVirtualArray> vec_SelectionResult = 
			this.vecRefSelection_Array.get( iAtDimension );
		
		VirtualArrayVectorIterator iterator = new VirtualArrayVectorIterator();
		
		iterator.addSelectionVector( vec_SelectionResult );
		
		return iterator;
	}
	
	/*
	 * @see cerberus.data.collection.ISet#iteratorStorageByDim(int)
	 */
	public Iterator<IStorage> iteratorStorageByDim( final int iAtDimension ) {
		
		return vecRefStorage_Array.get( iAtDimension ).iterator();
	}
	
	public final Vector<IStorage> getStorageVectorByDim( final int iAtDimension ) {
		return this.vecRefStorage_Array.get( iAtDimension );
	}
	
	public final Vector<IVirtualArray> getVirtualArrayVectorByDim( final int iAtDimension ) {
		return this.vecRefSelection_Array.get( iAtDimension );
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer("SET:");
	
		result.append( this.getId() );
		
		result.append(" Se=");
		if ( this.vecRefSelection_Array.isEmpty() ) 
		{
			result.append("-empty-");
		}
		else 
		{
			Enumeration <Vector<IVirtualArray>> itemsVectorSelection = 
				this.vecRefSelection_Array.elements();
			
			while ( itemsVectorSelection.hasMoreElements() ) 
			{
				Enumeration <IVirtualArray> itemsInVectorSelection = 
					itemsVectorSelection.nextElement().elements();
				
				while ( itemsInVectorSelection.hasMoreElements() ) {
					
					result.append( itemsInVectorSelection.nextElement().toString() );
					
					if ( itemsInVectorSelection.hasMoreElements() ) 
					{
						result.append( " - " );
					}
					
				} // end: while ( itemsInVector.hasMoreElements() ) {
				
				if ( itemsVectorSelection.hasMoreElements() ) 
				{
					result.append( " | " );
				} // end: if ( itemsVector.hasMoreElements() ) 
				
			} //end: while ( itemsVector.hasMoreElements() ) 
			
		} // end: if ( this.vecRefSelection_Array.isEmpty() ) {...} else {..}
		
		result.append(" St=");
		if ( this.vecRefStorage_Array.isEmpty() ) 
		{
			result.append("-empty-");
		} 
		else 
		{
			Enumeration <Vector<IStorage>> itemsVectorStorage = 
				this.vecRefStorage_Array.elements();
			
			while ( itemsVectorStorage.hasMoreElements() ) 
			{
				Enumeration <IStorage> itemsInVector_storage = 
					itemsVectorStorage.nextElement().elements();
				
				while ( itemsInVector_storage.hasMoreElements() ) {
					
					IStorage buffer = itemsInVector_storage.nextElement();
					if ( buffer != null ) 
					{
						result.append( buffer.toString() );
					}
					
					if ( itemsInVector_storage.hasMoreElements() ) 
					{
						result.append( " - " );
					}
					
				} // end: while ( itemsInVector.hasMoreElements() ) {
				
				if ( itemsVectorStorage.hasMoreElements() ) 
				{
					result.append( " | " );
				} // end: if ( itemsVector.hasMoreElements() ) 
				
			} //end: while ( itemsVector.hasMoreElements() ) 
			
		} //end: if ( this.vecRefStorage_Array.isEmpty() ) 
		
		return result.toString();
	}
	
}
