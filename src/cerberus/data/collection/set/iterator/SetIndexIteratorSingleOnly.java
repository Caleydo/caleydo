/**
 * 
 */
package cerberus.data.collection.set.iterator;

import cerberus.data.collection.ISet;
import cerberus.data.collection.IVirtualArray;
import cerberus.data.collection.virtualarray.iterator.IVirtualArrayIterator;

/**
 * @author kalkusch
 *
 */
public class SetIndexIteratorSingleOnly implements ISetIndexIterator {

	protected ISet refSet;
	
	protected IVirtualArrayIterator selectIterator;
	
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
	public IVirtualArray nextSelection() {
		// TODO Auto-generated method stub
		return null;
	}

}
