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

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.data.collection.CollectionMetaData;
import cerberus.data.collection.Selection;
import cerberus.data.collection.Set;
import cerberus.data.collection.Storage;
import cerberus.data.collection.parser.CollectionSetParseSaxHandler;
import cerberus.data.collection.selection.iterator.SelectionIterator;
import cerberus.data.collection.selection.iterator.SelectionVectorIterator;
import cerberus.data.collection.set.SetSimpleBase;
import cerberus.xml.parser.DParseSaxHandler;

/**
 * Defines a Planar 2-dimensional set. 
 * 
 * Only one Storage can be used.
 * 
 * @author Michael Kalkusch
 *
 */
public class SetPlanarSimple 
extends SetSimpleBase
implements Set {


	
	/**
	 *  Stores state of each Selection combined by AND.
	 */
	protected CollectionMetaData refMetaDataAll = null;
	
	/**
	 * Stores state of each Selection combined by OR.
	 */
	protected CollectionMetaData refMetaDataAny = null;
	
//	/**
//	 * Store reference to the only Storage.
//	 */
//	protected Storage refStorage = null;
	
	/**
	 * Store reference to the Selection.
	 */
	protected Vector < Vector<Selection> >vecRefSelection_Array;
	
	/**
	 * Store reference to the Storage.
	 */
	protected Vector< Vector<Storage> > vecRefStorage_Array;
	
	//private int iDimensionOfSet = 2;
	
	/**
	 * Variable for the dimension of this set.
	 */
	protected int[] iSizeDimension = {0,0};
	
	
	/**
	 * 
	 */
	public SetPlanarSimple( int iSetCollectionId, GeneralManager setGeneralManager) {
		
		super( iSetCollectionId, setGeneralManager );
		
		vecRefSelection_Array = new Vector< Vector<Selection> > (2);
		vecRefSelection_Array.add(0, new Vector<Selection> (2));
		vecRefSelection_Array.add(1, new Vector<Selection> (2));
		
		vecRefStorage_Array = new Vector< Vector<Storage> > (2);
		vecRefStorage_Array.add(0, new Vector<Storage> (2));
		vecRefStorage_Array.add(1, new Vector<Storage> (2));
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#setSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean setSelectionByDim( final Selection[] addSelection, final int iAtDimension) {
		
		Vector<Selection> vecSelectionBuffer = 
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
	 * @see cerberus.data.collection.Set#setSelection(cerberus.data.collection.Selection, int)
	 */
	public void setStorageByDim( final Storage[] addStorage, final int iAtDimension) {
		
		Vector<Storage> vecStoreBuffer = 
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
	 * @see cerberus.data.collection.Set#setStorageByDimAndIndex(cerberus.data.collection.Storage, int, int)
	 */
	public boolean setStorageByDimAndIndex( final Storage addStorage, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		vecRefStorage_Array.get(iAtDimension).insertElementAt(addStorage,iAtIndex);
		
		return true;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.data.collection.Set#setStorageByDimAndIndex(cerberus.data.collection.Storage, int, int)
	 */
	public boolean setSelectionByDimAndIndex( final Selection addSelection, 
			final int iAtDimension, 
			final int iAtIndex ) {
		
		vecRefSelection_Array.get(iAtDimension).insertElementAt(addSelection,iAtIndex);
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#removeSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean removeSelection(Selection[] removeSelection, int iFromDimension) {
		
		Vector <Selection> bufferSelection = vecRefSelection_Array.get(iFromDimension);
		
		boolean bAllRemoved = true;
		for ( int i=0; i< removeSelection.length; i++) {
			
			if ( ! bufferSelection.remove( removeSelection[i] ) ) {
				bAllRemoved = false;
				
				
				System.err.println( "Can not remove Selection: " + 
						removeSelection[i].toString() +
						" from Set: " +
						this.toString() );
				assert false : "Can not remove Selection from Set.";
			}
		} // end: for
		
		/* assing result .. */
		vecRefSelection_Array.setElementAt(bufferSelection,iFromDimension);
		
		return bAllRemoved;	
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasSelection(cerberus.data.collection.Selection, int)
	 */
	public boolean hasSelection(Selection testSelection, int iAtDimension) {
		
		return vecRefSelection_Array.get(iAtDimension).contains( testSelection );		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#hasSelectionInSet(cerberus.data.collection.Selection)
	 */
	public boolean hasSelectionInSet(Selection testSelection) {
		if ( hasSelection( testSelection, 0 )) {
			return true;
		}
		return hasSelection( testSelection, 1 );
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getDimensionSizeForAllSelections()
	 */
	public int[] getDimensionSizeForAllSelections() {
		// TODO Auto-generated method stub
		//FIXME what shall this be used for?
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getDimensionSize(int)
	 */
	public int getDimensionSize(int iAtDimension) {
		// TODO Auto-generated method stub
		//FIXME what shall this be used for?
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getDimensions()
	 */
	public int getDimensions() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#setDimensionSize(int, int)
	 */
	public void setDimensionSize(int iIndexDimension, int iValueDimensionSize) {
		// TODO Auto-generated method stub
		//	FIXME what shall this be used for?

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.Set#getSelection(int)
	 */
	public Selection[] getSelectionByDim(int iAtDimension) {
		Vector <Selection> vec_Selection =
			vecRefSelection_Array.get( iAtDimension );
		
		Selection[] selResult = new Selection[vec_Selection.size()];
		
		Iterator <Selection> iter = vec_Selection.iterator();
		
		for ( int iIndex = 0; iter.hasNext(); iIndex++ ) {
			selResult[iIndex] = iter.next();
		}
		
		return selResult;
	}

	public Selection getSelectionByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		
		return vecRefSelection_Array.get(iAtDimension).get(iAtIndex);		
	}
	
	public final Storage[] getStorageByDim(int iAtDimension) {
		//	FIXME what shall this be used for?
		
		return (Storage[]) vecRefStorage_Array.get(iAtDimension).toArray();		
	}
	
	public final Storage getStorageByDimAndIndex( final int iAtDimension, 
			final int iAtIndex ) {
		
		return vecRefStorage_Array.get(iAtDimension).get(iAtIndex);
	}

	public Vector<Selection> getSelectionArray(int iAtDimension) {
		return vecRefSelection_Array.get(iAtDimension);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.CollectionMetaDataInterface#getMetaData()
	 */
	public CollectionMetaData getMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.CollectionMetaDataInterface#setMetaData(cerberus.data.collection.CollectionMetaData)
	 */
	public void setMetaData(CollectionMetaData setMetaData) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.MetaDataSetInterface#getMetaDataAny()
	 */
	public CollectionMetaData getMetaDataAny() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.MetaDataSetInterface#setMetaDataAny(cerberus.data.collection.CollectionMetaData)
	 */
	public void setMetaDataAny(CollectionMetaData setMetaData) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.SubSet#getSubSets()
	 */
	public Set[] getSubSets() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.SubSet#hasSubSets()
	 */
	public boolean hasSubSets() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.SubSet#addSubSet(cerberus.data.collection.Set)
	 */
	public boolean addSubSet(Set addSet) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.SubSet#swapSubSet(cerberus.data.collection.Set, cerberus.data.collection.Set)
	 */
	public boolean swapSubSet(Set fromSet, Set toSet) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.SubSet#removeSubSet(cerberus.data.collection.Set)
	 */
	public boolean removeSubSet(Set addSet) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Restore state of object by update data from SaxHandler
	 * 
	 * @param refSaxHandler reference to SaxHandler
	 * @return TRUE if the provided handler provided same Id as object.
	 */
	public boolean setMementoXML_usingHandler( final DParseSaxHandler refSaxHandler ) {
		
		assert false : "not impelemted yet";
			
		try {
			CollectionSetParseSaxHandler parser = 
				(CollectionSetParseSaxHandler) refSaxHandler;
			
			if ( parser.getDim() < 0 ) {
				assert false:"Parser does not return informations neede";
				return false;
			}

			final GeneralManager refManger = getManager();
			
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
		return ManagerObjectType.SET_PLANAR;
	}
	
	/**
	 * @see cerberus.data.collection.CollectionInterface#getCacheId()
	 */
	public int getCacheId() {
		
		assert false : "not implemented update yet";
	
//		Iterator< Vector<Selection> > iterDim = vecRefSelection_Array.iterator();
//		
//		while ( iterDim.hasNext() ) {
//			Vector<Selection> refVecSelect = iterDim.next();
//			Iterator< Selection > iterSel = refVecSelect.iterator();
//			while ( iterSel.hasNext() ) {
//				this.set iterSel.next().getCacheId();
//			}
//		}
		
		return this.iCacheId;
	}

	/**
	 * Get an Iterator for all Storage used in this Set.
	 * 
	 * @param iAtDimension requested dimension
	 * @return Storage bound to this dimension
	 *
	 * @see cerberus.data.collection.Set#iteratorSelectionByDim(int)
	 */
	public SelectionIterator iteratorSelectionByDim( final int iAtDimension ) {
		Vector<Selection> vec_SelectionResult = 
			this.vecRefSelection_Array.get( iAtDimension );
		
		SelectionVectorIterator iterator = new SelectionVectorIterator();
		
		iterator.addSelectionVector( vec_SelectionResult );
		
		return iterator;
	}
	
	/*
	 * @see cerberus.data.collection.Set#iteratorStorageByDim(int)
	 */
	public Iterator<Storage> iteratorStorageByDim( final int iAtDimension ) {
		
		return vecRefStorage_Array.get( iAtDimension ).iterator();
	}
	
}
