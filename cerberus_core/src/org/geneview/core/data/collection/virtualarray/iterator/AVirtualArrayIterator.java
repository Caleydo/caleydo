/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data.collection.virtualarray.iterator;

import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.util.exception.GeneViewRuntimeExceptionType;
import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AVirtualArrayIterator implements IVirtualArrayIterator {

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
	protected IVirtualArray refSelection = null;
	
	/**
	 * 
	 */
	protected AVirtualArrayIterator( IVirtualArray setSelection ) {
		
		if ( setSelection == null ) {
			throw new GeneViewRuntimeException("AVirtualArrayIterator.Constructor init with null-pointer to IVirtualArray.",
					GeneViewRuntimeExceptionType.VIRTUALARRAY );
		}
		
		refSelection = setSelection;
		
		begin();
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.iterator.SelectionIterator#size()
	 */
	public final int size() {
		return iSelectionLength;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.iterator.SelectionIterator#remaining()
	 */
	public final int remaining() {
		return iSelectionLength - iCurrentVirtualIndex;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.iterator.SelectionIterator#getVirtualIndex()
	 */
	public final int getVirtualIndex() {
		return iCurrentVirtualIndex;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.iterator.ICollectionIterator#hasNext()
	 */
	public final boolean hasNext() {
		if ( iCurrentVirtualIndex < iSelectionLength ) {
			return true;
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.iterator.ICollectionIterator#begin()
	 */
	public void begin() {
		iSelectionLength = refSelection.length();
		iSelectionOffset = refSelection.getOffset();
		
		iCurrentVirtualIndex = 0;
	}

	
	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public abstract void setVirtualIndex(int iSetVirtualIndex);
	
	/* (non-Javadoc)
	 * @see org.geneview.core.data.collection.iterator.ICollectionIterator#next()
	 */
	public abstract int next();



}
