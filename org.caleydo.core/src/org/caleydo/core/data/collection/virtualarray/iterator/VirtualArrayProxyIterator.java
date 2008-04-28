package org.caleydo.core.data.collection.virtualarray.iterator;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;

/**
 * Automatical creates the suitable iterator for several IVirtualArray's.
 * 
 * Design Pattern "Factory"
 * 
 * @author Michael Kalkusch
 *
 */
public class VirtualArrayProxyIterator 
implements IVirtualArrayIterator {

	/**
	 * Reference to the actual iterator.
	 */
	protected IVirtualArrayIterator refSelectionIterator = null;
	
	public VirtualArrayProxyIterator() {
		
	}
	
	/**
	 * 
	 */
	public VirtualArrayProxyIterator( IVirtualArray setSelection ) {
		
		assert setSelection != null : "can not handle null-pointer IVirtualArray";
		
		switch ( setSelection.getSelectionType() ) {
		
		case VIRTUAL_ARRAY_SINGLE_BLOCK:
			refSelectionIterator = new VirtualArraySingleBlockIterator( setSelection );
			break;
			
		case VIRTUAL_ARRAY_MULTI_BLOCK:
			refSelectionIterator = new VirtualArrayMultiBlockIterator( setSelection );
			break;
			
		case VIRTUAL_ARRAY_MULTI_BLOCK_RLE:
			
		case VIRTUAL_ARRAY_RANDOM_BLOCK:
			
		default:
			refSelectionIterator = null;
			throw new CaleydoRuntimeException("VirtualArrayProxyIterator.Constructor with unsuppoerte selection type: [" +
					setSelection.getSelectionType() + "] !",
					CaleydoRuntimeExceptionType.VIRTUALARRAY );
		}
		
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.SelectionIterator#size()
	 */
	public int size() {
		return refSelectionIterator.size();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.SelectionIterator#remaining()
	 */
	public int remaining() {
		return refSelectionIterator.remaining();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.SelectionIterator#getVirtualIndex()
	 */
	public int getVirtualIndex() {
		return refSelectionIterator.getVirtualIndex();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(int iSetVirtualIndex)
			throws CaleydoRuntimeException {
		refSelectionIterator.setVirtualIndex( iSetVirtualIndex );
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#begin()
	 */
	public void begin() {
		refSelectionIterator.begin();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#next()
	 */
	public int next() {
		return refSelectionIterator.next();
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#hasNext()
	 */
	public boolean hasNext() {
		return refSelectionIterator.hasNext();
	}

	public void setToEnd()
	{
		refSelectionIterator.setToEnd();		
	}

}
