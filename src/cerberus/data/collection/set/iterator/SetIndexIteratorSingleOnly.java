/**
 * 
 */
package cerberus.data.collection.set.iterator;

import cerberus.data.collection.Set;
import cerberus.data.collection.Selection;
import cerberus.data.collection.selection.iterator.SelectionIterator;

/**
 * @author kalkusch
 *
 */
public class SetIndexIteratorSingleOnly implements SetIndexIterator {

	protected Set refSet;
	
	protected SelectionIterator selectIterator;
	
	/**
	 * 
	 */
	public SetIndexIteratorSingleOnly( final Set useSet ) {
		
		assert useSet == null : "Can not handel null pointer, need set";
		
		refSet = useSet;
		
		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.SetDataIterator#begin()
	 */
	public void begin() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.SetDataIterator#hasNext()
	 */
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.SetDataIterator#hasNextInSelection()
	 */
	public boolean hasNextInSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.SetDataIterator#nextIndex()
	 */
	public int nextIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.SetDataIterator#nextSelection()
	 */
	public Selection nextSelection() {
		// TODO Auto-generated method stub
		return null;
	}

}
