/**
 * 
 */
package cerberus.data.collection.storage.iterator;


import java.util.Iterator;
//import java.util.NoSuchElementException;
import java.util.Vector;

import cerberus.data.collection.Storage;
//import cerberus.util.exception.PrometheusVirtualArrayException;
//import cerberus.data.collection.selection.iterator.SelectionIterator;
//import cerberus.data.collection.selection.iterator.SelectionNullIterator;
//import cerberus.data.collection.iterator.CollectionIterator;

/**
 * Iterator for a several Selection.
 * 
 * @author kalkusch
 *
 */
public class StorageVectorIterator {

	/**
	 * Vector storing all Selections.
	 */
	private Vector <Storage> vecStorage = null;
	
	/**
	 * Iterator for Vector vecSelection
	 */
	private Iterator <Storage> iteratorStorage;
	
	
	
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
	public void addStorage( final Storage addStorage) {
		
		if ( vecStorage == null ) {
			vecStorage = new Vector <Storage> (2);
			vecStorage.addElement( addStorage );
			
			return;
		}
		
		if ( ! vecStorage.contains( addStorage ) ) {
			vecStorage.addElement( addStorage );
		}
	}
	
	/**
	 * Assign a hole Vector <Storage> to this iterator.
	 * Note: begin() is called inside this methode.
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#begin()
	 * 
	 * @param setStorageVector
	 */
	public void setStorageVector( final Vector <Storage> setStorageVector) {
		vecStorage = (Vector <Storage>) setStorageVector.clone();
		
		begin();
	}
	

	/**
	 * Resets the iterator to the begin.
	 * Note: must be called if Storages are set using addStorage().
	 * 
	 * @see cerberus.data.collection.iterator.CollectionIterator#begin()
	 * 
	 * @see cerberus.data.collection.selection.iterator.SelectionVectorIterator#addSelection(Selection)
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
	public Storage next() {
		return iteratorStorage.next();
	}
		
		

	/**
	 * Returns true, if the current Storage has mor elements, or if 
	 * there are any other Storages left, that have mor elements.
	 * If the crrent Storage does not have any more elements a new
	 * iterator from the next Storage is created unde the hood.
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
