/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data.collection.iterator;

/**
 * Base iterator interface for Collection.
 * 
 * Desing Pattern "Iterator"
 * 
 * @author Michael Kalkusch
 *
 * @see java.util.Iterator
 */
public interface ICollectionIterator {

	/**
	 * Reset the iterator to the first element.
	 * 
	 * Note: When creating a iterator it is already set to the first element, 
	 * thus calling begin() is a waste of resources.
	 */
	public void begin();
	
	/**
	 * Get the current index and increments Iterator.
	 * 
	 * @return current index
	 */
	public int next();

	/**
	 * Returns TRUE if one more item is the Collection.
	 * 
	 * @return TRUE if one more item is left.
	 */
	public boolean hasNext();
	
}
