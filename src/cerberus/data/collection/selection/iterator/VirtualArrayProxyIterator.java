/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.selection.iterator;

import cerberus.data.collection.IVirtualArray;
//import prometheus.data.collection.SelectionType;

import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Automatical creates the suitable iterator for several IVirtualArray's.
 * 
 * Desing Pattern "Factory"
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
		
		case SELECTION_SINGLE_BLOCK:
			refSelectionIterator = new VirtualArraySingleBlockIterator( setSelection );
			break;
			
		case SELECTION_MULTI_BLOCK:
			refSelectionIterator = new VirtualArrayMultiBlockIterator( setSelection );
			break;
			
		case SELECTION_MULTI_BLOCK_RLE:
			
		case SELECTION_RANDOM_BLOCK:
			
		default:
			refSelectionIterator = null;
			throw new CerberusRuntimeException("VirtualArrayProxyIterator.Constructor with unsuppoerte selection type: [" +
					setSelection.getSelectionType() + "] !",
					CerberusExceptionType.VIRTUALARRAY );
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
			throws CerberusRuntimeException {
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
