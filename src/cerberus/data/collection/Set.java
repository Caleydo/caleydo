/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection;

import java.util.Iterator;
import java.util.Vector;

import cerberus.data.IUniqueManagedObject;
import cerberus.data.collection.Storage;

import cerberus.data.collection.SubSet;
import cerberus.data.collection.CollectionMetaDataInterface;
import cerberus.data.collection.CollectionInterface;
import cerberus.data.collection.selection.iterator.SelectionIterator;
import cerberus.data.collection.selection.iterator.SelectionVectorIterator;
import cerberus.data.collection.thread.CollectionThreadObject;
import cerberus.data.xml.MementoItemXML;

/**
 * Defines a set containing of selections and storage
 * 
 * @author Michael Kalkusch
 *
 */
public interface Set
	extends  CollectionInterface,
	CollectionMetaDataInterface, 
	SubSet,
	MementoItemXML,
	CollectionThreadObject
{
	
	/**
	 * Adds a Selection to a specific dimension.
	 * Note, that addSelection() can not overwrite existing references to other Selection
	 * 
	 * @param addSelection
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * 
	 * @return TRUE if adding was successful
	 */
	public boolean setSelectionByDim( final Selection[] addSelection, 
			final int iAtDimension );
	
	/**
	 * Adds a Selection to a specific dimension.
	 * Note, that addSelection() can not overwrite existing references to other Selection
	 * 
	 * @param addSelection
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * 
	 * @return TRUE if adding was successful
	 */
	public boolean setSelectionByDim( final Vector<Selection> addSelection, 
			final int iAtDimension );
	
	/**
	 * Adds a Selection to a specific dimension.
	 * Note, that addSelection() can not overwrite existing references to other Selection
	 * 
	 * @param addSelection Selection to be added
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * @param iAtIndex index to put new Selection to
	 * @return TRUE if adding was successful
	 */
	public boolean setSelectionByDimAndIndex( final Selection addSelection, 
			final int iAtDimension, 
			final int iAtIndex );
	
	
	/**
	 * Removes a selection bound to a dimension.
	 * 
	 * @param removeSelection Selection to be removed
	 * @param iFromDimension address which dimension removeSelection shall be removed from, range [0.. getDimensionSize()-1 ]
	 * @return TRUE if removeSelection was removed from dimension iFromDimension
	 */
	public boolean removeSelection( final Selection[] removeSelection, 
			final int iFromDimension );
	
	/**
	 * Tests, if testSelection is in a specific dimension addressed by iAtDimension.
	 * 
	 * @param testSelection Selection to search for
	 * @param iAtDimension address a dimension
	 * @return TRUE if the testSelection is used for dimension iAtDimension
	 */
	public boolean hasSelection( final Selection testSelection, 
			final int iAtDimension );
	
	/**
	 * Tests if testSelection is in any Selecions of this set. 
	 * 
	 * @param testSelection in any dimension of this set
	 * @return TRUE if testSelection was in any dimension
	 */
	public boolean hasSelectionInSet( final Selection testSelection );
	
	
	/**
	 * Get the size of each dimension.
	 * 
	 * @return array containing sizes of dimension
	 */
	public int[] getDimensionSizeForAllSelections();
	
	/**
	 * Get the size of one dimension addressed by iAtDimension.
	 * 
	 * @see cerberus.data.collection#setDimensionSize()
	 * 
	 * @param iAtDimension
	 * @return size of the dimension defined by the set.
	 */
	public int getDimensionSize( final int iAtDimension);
	
	/**
	 * Get the number of dimensions used in this set. 
	 * The number of dimension is identical the number of stored Selections and Storages.
	 * 
	 * @return total number of dimensions, which is equal to the total nubmer 
	 * of handled Selections and Storages. 
	 */
	public int getDimensions();
	
//	/**
//	 * Sets the size of a dimension.
//	 * Note, that not all implementations will support this.
//	 * 
//	 * @param iIndexDimension address on diemnsion
//	 * @param iValueDimensionSize sets the size of the dimension
//	 */
//	public void setDimensionSize( final int iIndexDimension,
//			final int iValueDimensionSize );
	
	
	/**
	 * Sets a reference to a storage.
	 */
	public void setStorageByDim( final Storage[] setStorage, final int iAtDimension  );
	
	/**
	 * Sets a reference to a storage.
	 */
	public boolean setStorageByDim( final Vector<Storage> setStorage, final int iAtDimension  );
	
	
	
	/**
	 * Sets a reference to a storage.
	 * 
	 * @param addStorage Storage to be added
	 * @param iAtDimension 
	 * @param iAtIndex
	 * @return TRUE if it was successful
	 */
	public boolean setStorageByDimAndIndex( final Storage addStorage, 
			final int iAtDimension, 
			final int iAtIndex );
	
	/**
	 * Get an Iterator containin all Selection's used in this Set.
	 * 
	 * @param iAtDimension requested dimension
	 * @return iterator for Selection's
	 */	
	public SelectionIterator iteratorSelectionByDim( final int iAtDimension );

	/**
	 * Get an Iterator for all Storage used in this Set.
	 * 
	 * @param iAtDimension requested dimension
	 * @return Storage bound to this dimension
	 */
	public Iterator<Storage> iteratorStorageByDim( final int iAtDimension );
	
	/**
	 * Get the reference to the storage.
	 * 
	 * @return
	 */
	public Storage[] getStorageByDim( final int iAtDimension );
	
	public Vector<Storage> getStorageVectorByDim( final int iAtDimension );
	
	public Storage getStorageByDimAndIndex( final int iAtDimension, final int iAtIndex );
	
	public Selection[] getSelectionByDim( final int iAtDimension );
	
	public Vector<Selection> getSelectionVectorByDim( final int iAtDimension );
	
	public Selection getSelectionByDimAndIndex( final int iAtDimension, final int iAtIndex );
	
	/**
	 * Test if cache has changed without reevaluating the stats of the cildren.
	 * 
	 * @see cerberus.data.collection.thread.CollectionThreadObject#hasCacheChanged(int)
	 * 
	 * @param iCompareCacheId
	 */
	public boolean hasCacheChangedReadOnly( final int iCompareCacheId );
	
/////////////////////////////
//	/**
//	 * Adds a Selection to a specific dimension.
//	 * Note, that addSelection() can not overwrite existing references to other Selection
//	 * 
//	 * @param addSelection
//	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if adding was successful
//	 */
//	public boolean setSelectionByDim( final Selection[] addSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Removes a selection bound to a dimension.
//	 * 
//	 * @param removeSelection Selection to be removed
//	 * @param iFromDimension address which dimension removeSelection shall be removed from, range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if removeSelection was removed from dimension iFromDimension
//	 */
//	public boolean removeSelectionByDim( final Selection[] removeSelection, 
//			final int iFromDimension );
//	
//	/**
//	 * Tests, if testSelection is in a specific dimension addressed by iAtDimension.
//	 * 
//	 * @param testSelection Selection to search for
//	 * @param iAtDimension address a dimension
//	 * @return TRUE if the testSelection is used for dimension iAtDimension
//	 */
//	public boolean hasSelection( final Selection testSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Tests if testSelection is in any Selecions of this set. 
//	 * 
//	 * @param testSelection in any dimension of this set
//	 * @return TRUE if testSelection was in any dimension
//	 */
//	public boolean hasSelectionInSet( final Selection testSelection );
//	
//	
//	/**
//	 * Get the size of each dimension.
//	 * 
//	 * @return array containing sizes of dimension
//	 */
//	public int[] getDimensionSizeForAllSelections();
//	
//	/**
//	 * Get the size of one dimension addressed by iAtDimension.
//	 * 
//	 * @see cerberus.data.collection#setDimensionSize()
//	 * 
//	 * @param iAtDimension
//	 * @return size of the dimension defined by the set.
//	 */
//	public int getDimensionSize( final int iAtDimension);
//	
//	/**
//	 * Get the number of dimensions used in this set. 
//	 * The number of dimension is identicto the number of stored Selection's.
//	 * 
//	 * @return total number of dimensions, which is equal to the total nubmer of handled Selection's
//	 */
//	public int getDimensions();
//	
//	/**
//	 * Sets the size of a dimension.
//	 * Note, that not all implementations will support this.
//	 * 
//	 * @param iIndexDimension address on diemnsion
//	 * @param iValueDimensionSize sets the size of the dimension
//	 */
//	public void setDimensionSize( final int iIndexDimension,
//			final int iValueDimensionSize );
//	
//	
//	/**
//	 * Sets a reference to a storage.
//	 */
//	public void setStorageByDim( final Storage[] setStorage, final int iDimension );
//	
//	/**
//	 * Get the reference to the storage.
//	 * 
//	 * @return
//	 */
//	public Storage[] getStorageByDim( final int iDimension );
//	
//	public Selection[] getSelectionByDim( final int iDimension );
	
}
