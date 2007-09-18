/**
 * 
 */
package org.geneview.core.data.collection.storage.iterator;


import java.util.Iterator;
//import java.util.NoSuchElementException;
import java.util.Vector;

import org.geneview.core.data.collection.IStorage;
//import org.geneview.core.util.exception.PrometheusVirtualArrayException;
//import org.geneview.core.data.collection.selection.iterator.IVirtualArrayIterator;
//import org.geneview.core.data.collection.selection.iterator.VirtualArrayNullIterator;
//import org.geneview.core.data.collection.iterator.ICollectionIterator;

/**
 * Iterator for a several IVirtualArray.
 * 
 * @author Michael Kalkusch
 *
 */
public class StorageVectorIterator {

	/**
	 * Vector storing all Selections.
	 */
	private Vector <IStorage> vecStorage = null;
	
	/**
	 * Iterator for Vector vecSelection
	 */
	private Iterator <IStorage> iteratorStorage;
	
	
	
	/**
	 * 
	 */
	public StorageVectorIterator() {
		
	}

	/**
	 * 
	 * Note: must call begin() before using this iterator!
	 * 
	 * @see cerberus.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @param addSelection
	 */
	public void addStorage( final IStorage addStorage) {
		
		if ( vecStorage == null ) {
			vecStorage = new Vector <IStorage> (2);
			vecStorage.addElement( addStorage );
			
			return;
		}
		
		if ( ! vecStorage.contains( addStorage ) ) {
			vecStorage.addElement( addStorage );
		}
	}
	
	/**
	 * Assign a hole Vector <IStorage> to this iterator.
	 * Note: begin() is called inside this method.
	 * 
	 * @see cerberus.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @param setStorageVector
	 */
	public void setStorageVector( final Vector <IStorage> setStorageVectorRef) {
		vecStorage = setStorageVectorRef;
		
		begin();
	}
	

	/**
	 * Resets the iterator to the begin.
	 * Note: must be called if Storages are set using addStorage().
	 * 
	 * @see cerberus.data.collection.iterator.ICollectionIterator#begin()
	 * 
	 * @see cerberus.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#addSelection(IVirtualArray)
	 * 
	 */
	public void begin() {
		iteratorStorage = vecStorage.iterator();
	}

	/**
	 * Get the next index.
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see cerberus.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @see cerberus.data.collection.iterator.ICollectionIterator#next()
	 */
	public IStorage next() {
		return iteratorStorage.next();
	}
		
		

	/**
	 * Returns true, if the current IStorage has mor elements, or if 
	 * there are any other Storages left, that have mor elements.
	 * If the crrent IStorage does not have any more elements a new
	 * iterator from the next IStorage is created unde the hood.
	 * 
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see cerberus.data.collection.virtualarray.iterator.VirtualArrayVectorIterator#begin()
	 * 
	 * @see cerberus.data.collection.iterator.ICollectionIterator#hasNext()
	 */
	public boolean hasNext() {
		return iteratorStorage.hasNext();
	}

}
