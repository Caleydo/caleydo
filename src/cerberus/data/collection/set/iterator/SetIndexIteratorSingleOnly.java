/**
 * 
 */
package cerberus.data.collection.set.iterator;

import cerberus.data.collection.ISet;
import cerberus.data.collection.ISelection;
import cerberus.data.collection.selection.iterator.ISelectionIterator;

/**
 * @author kalkusch
 *
 */
public class SetIndexIteratorSingleOnly implements ISetIndexIterator {

	protected ISet refSet;
	
	protected ISelectionIterator selectIterator;
	
	/**
	 * 
	 */
	public SetIndexIteratorSingleOnly( final ISet useSet ) {
		
		assert useSet == null : "Can not handel null pointer, need set";
		
		refSet = useSet;
		
		
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.ISetDataIterator#begin()
	 */
	public void begin() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.ISetDataIterator#hasNext()
	 */
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.ISetDataIterator#hasNextInSelection()
	 */
	public boolean hasNextInSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.ISetDataIterator#nextIndex()
	 */
	public int nextIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.iterator.ISetDataIterator#nextSelection()
	 */
	public ISelection nextSelection() {
		// TODO Auto-generated method stub
		return null;
	}

}
