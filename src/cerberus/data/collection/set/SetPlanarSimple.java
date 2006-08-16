/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.set;

import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;

import cerberus.manager.IGeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.IMetaData;
import cerberus.data.collection.ISelection;
import cerberus.data.collection.ISet;
import cerberus.data.collection.IStorage;
import cerberus.data.collection.parser.CollectionSetSaxParserHandler;
import cerberus.data.collection.selection.iterator.ISelectionIterator;
import cerberus.data.collection.selection.iterator.SelectionVectorIterator;
import cerberus.data.collection.set.ASetSimple;
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
	 *  Stores state of each ISelection combined by AND.
	 */
	protected IMetaData refMetaDataAll = null;
	
	/**
	 * Stores state of each ISelection combined by OR.
	 */
	protected IMetaData refMetaDataAny = null;
	
//	/**
//	 * Store reference to the only IStorage.
//	 */
//	protected IStorage refStorage = null;
	
	/**
	 * Store reference to the ISelection.
	 */
	protected Vector < Vector<ISelection> >vecRefSelection_Array;
	
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
	 * 
	 */
	public SetPlanarSimple( int iSetCollectionId, IGeneralManager setGeneralManager) {
		
		super( iSetCollectionId, setGeneralManager );
		
		vecRefSelection_Array = new Vector< Vector<ISelection> > (2);
		vecRefSelection_Array.add(0, new Vector<ISelection> (2));
		vecRefSelection_Array.add(1, new Vector<ISelection> (2));
		
		vecRefStorage_Array = new Vector< Vector<IStorage> > (2);
		vecRefStorage_Array.add(0, new Vector<IStorage> (2));
		vecRefStorage_Array.add(1, new Vector<IStorage> (2));
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setSelection(cerberus.data.collection.ISelection, int)
	 */
	public boolean setSelectionByDim( final ISelection[] addSelection, final int iAtDimension) {
		
		Vector<ISelection> vecSelectionBuffer = 
			vecRefSelection_Array.get(iAtDimension);
		
		/* reset size of vector to size of array */
		vecSelectionBuffer.setSize( addSelection.length );
	
		for ( int i=0; i < addSelection.length ; i++ ) {
			vecSelectionBuffer.setElementAt( addSelection[i], i );
		}
		
		/* assing result..*/
		vecRefSelection_Array.setElementAt(vecSelectionBuffer,iAtDimension);
		
		return true;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#setSelection(cerberus.data.collection.ISelection, int)
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
	public boolean setSelectionByDimAndIndex( final ISelection addSelection, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		vecRefSelection_Array.get(iAtDimension).insertElementAt(addSelection,iAtIndex);
		
		return true;
	}
	
	public final boolean setStorageByDim( Vector <IStorage> setVecStorage, final int iAtDimension ) {
		
		assert setVecStorage != null: "setStorageVectorByDim() with null-pointer";
		
		this.vecRefStorage_Array.setElementAt( setVecStorage, iAtDimension );

		return true;
	}
	
	public final boolean setSelectionByDim( Vector <ISelection> setVecSelection, final int iAtDimension ) {
		
		assert setVecSelection != null: "setStorageVectorByDim() with null-pointer";
		
		this.vecRefSelection_Array.setElementAt( setVecSelection, iAtDimension );

		return true;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#removeSelection(cerberus.data.collection.ISelection, int)
	 */
	public boolean removeSelection(ISelection[] removeSelection, int iFromDimension) {
		
		Vector <ISelection> bufferSelection = vecRefSelection_Array.get(iFromDimension);
		
		boolean bAllRemoved = true;
		for ( int i=0; i< removeSelection.length; i++) {
			
			if ( ! bufferSelection.remove( removeSelection[i] ) ) {
				bAllRemoved = false;
				
				
				System.err.println( "Can not remove ISelection: " + 
						removeSelection[i].toString() +
						" from ISet: " +
						this.toString() );
				assert false : "Can not remove ISelection from ISet.";
			}
		} // end: for
		
		/* assing result .. */
		vecRefSelection_Array.setElementAt(bufferSelection,iFromDimension);
		
		return bAllRemoved;	
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#hasSelection(cerberus.data.collection.ISelection, int)
	 */
	public boolean hasSelection(ISelection testSelection, int iAtDimension) {
		
		return vecRefSelection_Array.get(iAtDimension).contains( testSelection );		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#hasSelectionInSet(cerberus.data.collection.ISelection)
	 */
	public boolean hasSelectionInSet(ISelection testSelection) {
		if ( hasSelection( testSelection, 0 )) {
			return true;
		}
		return hasSelection( testSelection, 1 );
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISet#getDimensionSizeForAllSelections()
	 */
	public int[] getDimensionSizeForAllSelections() {
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
	public ISelection[] getSelectionByDim(int iAtDimension) {
		Vector <ISelection> vec_Selection =
			vecRefSelection_Array.get( iAtDimension );
		
		ISelection[] selResult = new ISelection[vec_Selection.size()];
		
		Iterator <ISelection> iter = vec_Selection.iterator();
		
		for ( int iIndex = 0; iter.hasNext(); iIndex++ ) {
			selResult[iIndex] = iter.next();
		}
		
		return selResult;
	}

	public ISelection getSelectionByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		
		return vecRefSelection_Array.get(iAtDimension).get(iAtIndex);		
	}
	
	public final IStorage[] getStorageByDim(int iAtDimension) {
		//	FIXME what shall this be used for?
		
		return (IStorage[]) vecRefStorage_Array.get(iAtDimension).toArray();		
	}
	
	public final IStorage getStorageByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		
		return vecRefStorage_Array.get(iAtDimension).get(iAtIndex);
	}

	public Vector<ISelection> getSelectionArray(int iAtDimension) {
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

			final IGeneralManager refManger = getManager();
			
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
				ManagerObjectType.SELECTION_SINGLE_BLOCK.name())
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
		
		assert false : "not implemented update yet";
	
//		Iterator< Vector<ISelection> > iterDim = vecRefSelection_Array.iterator();
//		
//		while ( iterDim.hasNext() ) {
//			Vector<ISelection> refVecSelect = iterDim.next();
//			Iterator< ISelection > iterSel = refVecSelect.iterator();
//			while ( iterSel.hasNext() ) {
//				this.set iterSel.next().getCacheId();
//			}
//		}
		
		return this.iCacheId;
	}

	/**
	 * Get an Iterator for all IStorage used in this ISet.
	 * 
	 * @param iAtDimension requested dimension
	 * @return IStorage bound to this dimension
	 *
	 * @see cerberus.data.collection.ISet#iteratorSelectionByDim(int)
	 */
	public ISelectionIterator iteratorSelectionByDim( final int iAtDimension ) {
		Vector<ISelection> vec_SelectionResult = 
			this.vecRefSelection_Array.get( iAtDimension );
		
		SelectionVectorIterator iterator = new SelectionVectorIterator();
		
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
	
	public final Vector<ISelection> getSelectionVectorByDim( final int iAtDimension ) {
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
			Enumeration <Vector<ISelection>> itemsVectorSelection = 
				this.vecRefSelection_Array.elements();
			
			while ( itemsVectorSelection.hasMoreElements() ) 
			{
				Enumeration <ISelection> itemsInVectorSelection = 
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
					
					result.append( itemsInVector_storage.nextElement().toString() );
					
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
