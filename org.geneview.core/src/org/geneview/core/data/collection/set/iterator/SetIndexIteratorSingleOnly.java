/**
 * 
 */
package org.geneview.core.data.collection.set.iterator;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.data.collection.virtualarray.iterator.IVirtualArrayIterator;

/**
 * @author Michael Kalkusch
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
	 * @see org.geneview.core.data.collection.set.iterator.ISetDataIterator#begin()
	 */
	public void begin() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.set.iterator.ISetDataIterator#hasNext()
	 */
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.set.iterator.ISetDataIterator#hasNextInSelection()
	 */
	public boolean hasNextInSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.set.iterator.ISetDataIterator#nextIndex()
	 */
	public int nextIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.set.iterator.ISetDataIterator#nextSelection()
	 */
	public IVirtualArray nextSelection() {
		// TODO Auto-generated method stub
		return null;
	}

}
