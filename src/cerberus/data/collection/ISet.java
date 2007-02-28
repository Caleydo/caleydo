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

//import cerberus.data.IUniqueManagedObject;
import cerberus.data.collection.IStorage;

import cerberus.data.collection.ISubSet;
import cerberus.data.collection.IMetaDataHandler;
import cerberus.data.collection.ICollection;
import cerberus.data.collection.thread.ICollectionThreadObject;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;
//import cerberus.data.collection.virtualarray.iterator.VirtualArrayVectorIterator;
import cerberus.data.xml.IMementoItemXML;

/**
 * Defines a set containing of VirtualArrays and storage
 * 
 * @author Michael Kalkusch
 *
 */
public interface ISet
	extends  ICollection,
	IMetaDataHandler, 
	ISubSet,
	IMementoItemXML,
	ICollectionThreadObject
{
	
	/**
	 * Adds a IVirtualArray to a specific dimension.
	 * Note, that addVirtualArray() can not overwrite existing references to other IVirtualArray
	 * 
	 * @param addVirtualArray
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * 
	 * @return TRUE if adding was successful
	 */
	public boolean setVirtualArrayByDim( final IVirtualArray[] addVirtualArray, 
			final int iAtDimension );
	
	/**
	 * Adds a IVirtualArray to a specific dimension.
	 * Note, that addVirtualArray() can not overwrite existing references to other IVirtualArray
	 * 
	 * @param addVirtualArray
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * 
	 * @return TRUE if adding was successful
	 */
	public boolean setVirtualArrayByDim( final Vector<IVirtualArray> addVirtualArray, 
			final int iAtDimension );
	
	/**
	 * Adds a IVirtualArray to a specific dimension.
	 * Note, that addVirtualArray() can not overwrite existing references to other IVirtualArray
	 * 
	 * @param addVirtualArray IVirtualArray to be added
	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
	 * @param iAtIndex index to put new IVirtualArray to
	 * @return TRUE if adding was successful
	 */
	public boolean setVirtualArrayByDimAndIndex( final IVirtualArray addVirtualArray, 
			final int iAtDimension, 
			final int iAtIndex );
	
	
	/**
	 * Removes a VirtualArray bound to a dimension.
	 * 
	 * @param removeVirtualArray IVirtualArray to be removed
	 * @param iFromDimension address which dimension removeVirtualArray shall be removed from, range [0.. getDimensionSize()-1 ]
	 * @return TRUE if removeVirtualArray was removed from dimension iFromDimension
	 */
	public boolean removeVirtualArray( final IVirtualArray[] removeVirtualArray, 
			final int iFromDimension );
	
	/**
	 * Tests, if testVirtualArray is in a specific dimension addressed by iAtDimension.
	 * 
	 * @param testVirtualArray IVirtualArray to search for
	 * @param iAtDimension address a dimension
	 * @return TRUE if the testVirtualArray is used for dimension iAtDimension
	 */
	public boolean hasVirtualArray( final IVirtualArray testVirtualArray, 
			final int iAtDimension );
	
	/**
	 * Tests if testVirtualArray is in any Selecions of this set. 
	 * 
	 * @param testVirtualArray in any dimension of this set
	 * @return TRUE if testVirtualArray was in any dimension
	 */
	public boolean hasVirtualArrayInSet( final IVirtualArray testVirtualArray );
	
	
	/**
	 * Get the size of each dimension.
	 * 
	 * @return array containing sizes of dimension
	 */
	public int[] getDimensionSizeForAllVirtualArrays();
	
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
	 * The number of dimension is identical the number of stored VirtualArrays and Storages.
	 * 
	 * @return total number of dimensions, which is equal to the total number 
	 * of handled VirtualArrays and Storages. 
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
	 * Get an Iterator containin all IVirtualArray's used in this ISet.
	 * 
	 * @param iAtDimension requested dimension
	 * @return iterator for IVirtualArray's
	 */	
	public IVirtualArrayIterator iteratorVirtualArrayByDim( final int iAtDimension );

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
	
	/**
	 * 
	 * @param iAtDimension select one Storage object inside the Storage array
	 * @param iAtIndex select index inside a single Storage
	 * @return
	 */
	public IStorage getStorageByDimAndIndex( final int iAtDimension, final int iAtIndex );
	
	public IVirtualArray[] getVirtualArrayByDim( final int iAtDimension );
	
	public Vector<IVirtualArray> getVirtualArrayVectorByDim( final int iAtDimension );
	
	public IVirtualArray getVirtualArrayByDimAndIndex( final int iAtDimension, final int iAtIndex );
	
	/**
	 * Test if cache has changed without reevaluating the stats of the cildren.
	 * 
	 * @see cerberus.data.collection.thread.ICollectionThreadObject#hasCacheChanged(int)
	 * 
	 * @param iCompareCacheId
	 */
	public boolean hasCacheChangedReadOnly( final int iCompareCacheId );


	public SetType getSetType();
	
	
/////////////////////////////
//	/**
//	 * Adds a IVirtualArray to a specific dimension.
//	 * Note, that addSelection() can not overwrite existing references to other IVirtualArray
//	 * 
//	 * @param addSelection
//	 * @param iAtDimension range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if adding was successful
//	 */
//	public boolean setSelectionByDim( final IVirtualArray[] addSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Removes a selection bound to a dimension.
//	 * 
//	 * @param removeSelection IVirtualArray to be removed
//	 * @param iFromDimension address which dimension removeSelection shall be removed from, range [0.. getDimensionSize()-1 ]
//	 * @return TRUE if removeSelection was removed from dimension iFromDimension
//	 */
//	public boolean removeSelectionByDim( final IVirtualArray[] removeSelection, 
//			final int iFromDimension );
//	
//	/**
//	 * Tests, if testSelection is in a specific dimension addressed by iAtDimension.
//	 * 
//	 * @param testSelection IVirtualArray to search for
//	 * @param iAtDimension address a dimension
//	 * @return TRUE if the testSelection is used for dimension iAtDimension
//	 */
//	public boolean hasSelection( final IVirtualArray testSelection, 
//			final int iAtDimension );
//	
//	/**
//	 * Tests if testSelection is in any Selecions of this set. 
//	 * 
//	 * @param testSelection in any dimension of this set
//	 * @return TRUE if testSelection was in any dimension
//	 */
//	public boolean hasSelectionInSet( final IVirtualArray testSelection );
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
//	 * The number of dimension is identicto the number of stored IVirtualArray's.
//	 * 
//	 * @return total number of dimensions, which is equal to the total nubmer of handled IVirtualArray's
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
//	public IVirtualArray[] getSelectionByDim( final int iDimension );
	
}
