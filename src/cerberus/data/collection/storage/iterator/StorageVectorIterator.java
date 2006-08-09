/**
 * 
 */
package cerberus.data.collection.storage.iterator;


import java.util.Iterator;
//import java.util.NoSuchElementException;
import java.util.Vector;

import cerberus.data.collection.IStorage;
//import cerberus.util.exception.PrometheusVirtualArrayException;
//import cerberus.data.collection.selection.iterator.SelectionIterator;
//import cerberus.data.collection.selection.iterator.SelectionNullIterator;
//import cerberus.data.collection.iterator.CollectionIterator;

/**
 * Iterator for a several ISelection.
 * 
 * @author kalkusch
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
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
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
	 * Note: begin() is called inside this methode.
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @param setStorageVector
	 */
	public void setStorageVector( final Vector <IStorage> setStorageVector) {
		vecStorage = (Vector <IStorage>) setStorageVector.clone();
		
		begin();
	}
	

	/**
	 * Resets the iterator to the begin.
	 * Note: must be called if Storages are set using addStorage().
	 * 
	 * @see cerberus.data.collection.iterator.CollectionIterator#begin()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#addSelection(ISelection)
	 * 
	 */
	public void begin() {
		iteratorStorage = vecStorage.iterator();
	}

	/**
	 * Get the next index.
	 * Note: begin() must be called before pulling the frist index with next()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @see cerberus.data.collection.iterator.CollectionIterator#next()
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
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @see cerberus.data.collection.iterator.CollectionIterator#hasNext()
	 */
	public boolean hasNext() {
		return iteratorStorage.hasNext();
	}

}
