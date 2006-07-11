/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package cerberus.data;

import java.util.NoSuchElementException;

/*
 * Iterator for data access.
 * 
 * @author Michael Kalkusch
 */
public interface DataIteratorInterface {

	/*
	 * Sets the iterator to the first valid item;
	 */
	public void setToBegin();
	
	/*
	 * returns true is end has not been reached yet.
	 * if iterator has reached the last item FALSE is returned.
	 */
	public boolean hasNext();
	
	/*
	 * @return next element in list
	 */
	public Object next() 
		throws NoSuchElementException;
	
	/*
	 * returns the total number of elements in the list.
	 */
	public int size();
	
	/*
	 * Debug info on the iterator.
	 */
	//public String toString();
}
