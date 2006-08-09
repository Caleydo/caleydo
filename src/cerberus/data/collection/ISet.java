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
import cerberus.data.collection.IStorage;

import cerberus.data.collection.ISubSet;
import cerberus.data.collection.IMetaDataHandler;
import cerberus.data.collection.ICollection;
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
public interface ISet
	extends  ICollection,
	IMetaDataHandler, 
	ISubSet,
	MementoItemXML,
	CollectionThreadObject
{
	
	/**
	 * Adds a ISelection to a specific dimension.
	 * Note, that addSelection() can not overwrite existing references to other ISelection
	 * 
	 * @param addSelection
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * 
	 * @return TRUE if adding was successful
	 */
	public boolean setSelectionByDim( final ISelection[] addSelection, 
			final int iAtDimension );
	
	/**
	 * Adds a ISelection to a specific dimension.
	 * Note, that addSelection() can not overwrite existing references to other ISelection
	 * 
	 * @param addSelection
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * 
	 * @return TRUE if adding was successful
	 */
	public boolean setSelectionByDim( final Vector<ISelection> addSelection, 
			final int iAtDimension );
	
	/**
	 * Adds a ISelection to a specific dimension.
	 * Note, that addSelection() can not overwrite existing references to other ISelection
	 * 
	 * @param addSelection ISelection to be added
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * @param iAtIndex index to put new ISelection to
	 * @return TRUE if adding was successful
	 */
	public boolean setSelectionByDimAndIndex( final ISelection addSelection, 
			final int iAtDimension, 
			final int iAtIndex );
	
	
	/**
	 * Removes a selection bound to a dimension.
	 * 
	 * @param removeSelection ISelection to be removed
	 * @param iFromDimension address which dimension removeSelection shall be removed from, range [0.. getDimensionSize()-1 ]
	 * @return TRUE if removeSelection was removed from dimension iFromDimension
	 */
	public boolean removeSelection( final ISelection[] removeSelection, 
			final int iFromDimension );
	
	/**
	 * Tests, if testSelection is in a specific dimension addressed by iAtDimension.
	 * 
	 * @param testSelection ISelection to search for
	 * @param iAtDimension address a dimension
	 * @return TRUE if the testSelection is used for dimension iAtDimension
	 */
	public boolean hasSelection( final ISelection testSelection, 
			final int iAtDimension );
	
	/**
	 * Tests if testSelection is in any Selecions of this set. 
	 * 
	 * @param testSelection in any dimension of this set
	 * @return TRUE if testSelection was in any dimension
	 */
	public boolean hasSelectionInSet( final ISelection testSelection );
	
	
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
	public void setStorageByDim( final IStorage[] setStorage, final int iAtDimension  );
	
	/**
	 * Sets a reference to a storage.
	 */
	public boolean setStorageByDim( final Vector<IStorage> setStorage, final int iAtDimension  );
	
	
	
	/**
	 * Sets a reference to a storage.
	 * 
	 * @param addStorage IStorage to be added
	 * @param iAtDimension 
	 * @param iAtIndex
	 * @return TRUE if it was successful
	 */
	public boolean setStorageByDimAndIndex( final IStorage addStorage, 
			final int iAtDimension, 
			final int iAtIndex );
	
	/**
	 * Get an Iterator containin all ISelection's used in this ISet.
	 * 
	 * @param iAtDimension requested dimension
	 * @return iterator for ISelection's
	 */	
	public SelectionIterator iteratorSelectionByDim( final int iAtDimension );

	/**
	 * Get an Iterator for all IStorage used in this ISet.
	 * 
	 * @param iAtDimension requested dimension
	 * @return IStorage bound to this dimension
	 */
	public Iterator<IStorage> iteratorStorageByDim( final int iAtDimension );
	
	/**
	 * Get the reference to the storage.
	 * 
	 * @return
	 */
	public IStorage[] getStorageByDim( final int iAtDimension );
	
	public Vector<IStorage> getStorageVectorByDim( final int iAtDimension );
	
	public IStorage getStorageByDimAndIndex( final int iAtDimension, final int iAtIndex );
	
	public ISelection[] getSelectionByDim( final int iAtDimension );
	
	public Vector<ISelection> getSelectionVectorByDim( final int iAtDimension );
	
	public ISelection getSelectionByDimAndIndex( final int iAtDimension, final int iAtIndex );
	
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
//	 * Adds a ISelection to a specific dimension.
//	 * Note, that addSelection() can not overwrite existing references to other ISelection
//	 * 
//	 * @param addSelection
//	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if adding was successful
//	 */
//	public boolean setSelectionByDim( final ISelection[] addSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Removes a selection bound to a dimension.
//	 * 
//	 * @param removeSelection ISelection to be removed
//	 * @param iFromDimension address which dimension removeSelection shall be removed from, range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if removeSelection was removed from dimension iFromDimension
//	 */
//	public boolean removeSelectionByDim( final ISelection[] removeSelection, 
//			final int iFromDimension );
//	
//	/**
//	 * Tests, if testSelection is in a specific dimension addressed by iAtDimension.
//	 * 
//	 * @param testSelection ISelection to search for
//	 * @param iAtDimension address a dimension
//	 * @return TRUE if the testSelection is used for dimension iAtDimension
//	 */
//	public boolean hasSelection( final ISelection testSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Tests if testSelection is in any Selecions of this set. 
//	 * 
//	 * @param testSelection in any dimension of this set
//	 * @return TRUE if testSelection was in any dimension
//	 */
//	public boolean hasSelectionInSet( final ISelection testSelection );
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
//	 * The number of dimension is identicto the number of stored ISelection's.
//	 * 
//	 * @return total number of dimensions, which is equal to the total nubmer of handled ISelection's
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
//	public void setStorageByDim( final IStorage[] setStorage, final int iDimension );
//	
//	/**
//	 * Get the reference to the storage.
//	 * 
//	 * @return
//	 */
//	public IStorage[] getStorageByDim( final int iDimension );
//	
//	public ISelection[] getSelectionByDim( final int iDimension );
	
}
