/**
 * 
 */
package cerberus.data.collection.selection.iterator;

import java.util.NoSuchElementException;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * Null iterator representing null obejct for iteration.
 * 
 * @author kalkusch
 *
 */
public final class VirtualArrayNullIterator implements IVirtualArrayIterator {

	/**
	 * 
	 */
	public VirtualArrayNullIterator() {
		
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.selection.iterator.SelectionIterator#size()
	 */
	public int size() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.selection.iterator.SelectionIterator#remaining()
	 */
	public int remaining() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.selection.iterator.SelectionIterator#getVirtualIndex()
	 */
	public int getVirtualIndex() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.selection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(int iSetVirtualIndex)
			throws CerberusRuntimeException {
		assert false : "Does not make sence to set idnex on null iterator.";
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#begin()
	 */
	public void begin() {
		
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#next()
	 */
	public int next() {		
		throw new NoSuchElementException();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#hasNext()
	 */
	public boolean hasNext() {
		return false;
	}

	public void setToEnd()
	{
		
	}

}
