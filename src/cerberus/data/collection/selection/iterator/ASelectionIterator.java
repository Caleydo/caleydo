/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data.collection.selection.iterator;

import cerberus.data.collection.ISelection;
import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class ASelectionIterator implements ISelectionIterator {

	/**
	 * Defines the offset of the virtual array.
	 * 
	 * Range [ 0.. (iLength-1) ]
	 */
	protected int iSelectionOffset;
	
	/**
	 * Defines the length of the virtual array.
	 * 
	 * Range [0.. ]
	 */
	protected int iSelectionLength;
	
	/**
	 * Current virtual index.
	 */
	protected int iCurrentVirtualIndex;
	
	/**
	 * Link to Collection-Meta data.
	 */
	protected ISelection refSelection = null;
	
	/**
	 * 
	 */
	protected ASelectionIterator( ISelection setSelection ) {
		
		if ( setSelection == null ) {
			throw new CerberusRuntimeException("ASelectionIterator.Constructor init with null-pointer to ISelection.",
					CerberusExceptionType.VIRTUALARRAY );
		}
		
		refSelection = setSelection;
		
		begin();
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.SelectionIterator#size()
	 */
	public final int size() {
		return iSelectionLength;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.SelectionIterator#remaining()
	 */
	public final int remaining() {
		return iSelectionLength - iCurrentVirtualIndex;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.SelectionIterator#getVirtualIndex()
	 */
	public final int getVirtualIndex() {
		return iCurrentVirtualIndex;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.ICollectionIterator#hasNext()
	 */
	public final boolean hasNext() {
		if ( iCurrentVirtualIndex < iSelectionLength ) {
			return true;
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.ICollectionIterator#begin()
	 */
	public void begin() {
		iSelectionLength = refSelection.length();
		iSelectionOffset = refSelection.getOffset();
		
		iCurrentVirtualIndex = 0;
	}

	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public abstract void setVirtualIndex(int iSetVirtualIndex);
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.ICollectionIterator#next()
	 */
	public abstract int next();



}
