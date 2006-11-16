/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */

package cerberus.data.collection.selection.iterator;

import java.lang.ArrayIndexOutOfBoundsException;
import cerberus.data.collection.iterator.ICollectionIterator;
import cerberus.data.collection.IVirtualArray;

/**
 * Iterator handling several Selections at once.
 * 
 * @author Michael Kalkusch
 */
public interface IMultiVirtualArrayIterator
extends ICollectionIterator
{

	/**
	 * Insertes a new IVirtualArray to the iterator.
	 * Note, that the same selection sould not be added to the iterator more than once,
	 * becaus the iteration-process will be not as assumed.
	 * 
	 * @param addSelection adds a new IVirtualArray
	 */
	public void addSelection( final IVirtualArray addSelection );
	
	/**
	 * Adds a IVirtualArray to a defined position iPosition.
	 * Note: If any selection is already at position iPosition 
	 * the IVirtualArray is replaced by addSelection
	 * 
	 * @param addSelection object ot add
	 * @param iPosition position in iterator
	 */
	public void addSelectionAt( final IVirtualArray addSelection , final int iPosition );
	
	/**
	 * Get the selection used at a certain position.
	 * 
	 * @param iPosition index in the Iterator
	 * @return IVirtualArray bound to iPosition
	 * @throws ArrayIndexOutOfBoundsException if index does not fit
	 */
	public IVirtualArray getSelectionAt( final int iPosition ) 
		throws ArrayIndexOutOfBoundsException;
	
	/**
	 * Get the total number of selections inside the iterator.
	 * 
	 * @return number of IVirtualArray handled by iterator
	 */
	public int size();
	
	public void setIterationType( MultiVirtualArrayIterationType setIteratorType );
	
	/**
	 * Returns true is end has not been reached yet.
	 * if iterator has reached the last item FALSE is returned.
	 * 
	 * @return TRUE if one more item is in the iterator.
	 */
	public boolean hasNext();
	
	/**
	 * Returns current index and increments the iterator.
	 * 
	 * @return next element in list
	 */
	public int next();
	

	/**
	 * Create useful debug information on the iterator.
	 */
	public String toString();
	
}
