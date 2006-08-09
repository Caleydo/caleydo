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
import cerberus.data.collection.iterator.CollectionIterator;
import cerberus.data.collection.ISelection;

/**
 * Iterator handling several Selections at once.
 * 
 * @author Michael Kalkusch
 */
public interface MultiSelectionIterator
extends CollectionIterator
{

	/**
	 * Insertes a new ISelection to the iterator.
	 * Note, that the same selection sould not be added to the iterator more than once,
	 * becaus the iteration-process will be not as assumed.
	 * 
	 * @param addSelection adds a new ISelection
	 */
	public void addSelection( final ISelection addSelection );
	
	/**
	 * Adds a ISelection to a defined position iPosition.
	 * Note: If any selection is already at position iPosition 
	 * the ISelection is replaced by addSelection
	 * 
	 * @param addSelection object ot add
	 * @param iPosition position in iterator
	 */
	public void addSelectionAt( final ISelection addSelection , final int iPosition );
	
	/**
	 * Get the selection used at a certain position.
	 * 
	 * @param iPosition index in the Iterator
	 * @return ISelection bound to iPosition
	 * @throws ArrayIndexOutOfBoundsException if index does not fit
	 */
	public ISelection getSelectionAt( final int iPosition ) 
		throws ArrayIndexOutOfBoundsException;
	
	/**
	 * Get the total number of selections inside the iterator.
	 * 
	 * @return number of ISelection handled by iterator
	 */
	public int size();
	
	public void setIterationType( MultiSelectionIterationType setIteratorType );
	
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
