package org.caleydo.core.data;

import java.util.NoSuchElementException;

/**
 * Iterator for data access.
 * 
 * @author Michael Kalkusch
 */
public interface IDataIterator {

	/**
	 * Sets the iterator to the first valid item;
	 */
	public void setToBegin();
	
	/**
	 * Returns true is end has not been reached yet.
	 * if iterator has reached the last item FALSE is returned.
	 */
	public boolean hasNext();
	
	/**
	 * @return next element in list
	 */
	public Object next() 
		throws NoSuchElementException;
	
	/**
	 * Returns the total number of elements in the list.
	 */
	public int size();
}
