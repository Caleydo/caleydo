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
 * Iterator for Selecion VirtualArraySingleBlock
 * 
 * Desing Pattern "Iterator"
 * 
 * @author Michael Kalkusch
 *
 */
public class VirtualArraySingleBlockIterator 
extends AVirtualArrayIterator
implements IVirtualArrayIterator {

	
	/**
	 * 
	 */
	public VirtualArraySingleBlockIterator( IVirtualArray setSelection ) {
		super( setSelection );

	}


	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(int iSetVirtualIndex)
			throws GeneViewRuntimeException {
		
		if (( iSetVirtualIndex < 0 ) || 
				( iSetVirtualIndex >= iSelectionLength) ) {
			throw new GeneViewRuntimeException("setVirtualIndex() with index that was out of bounds.",
							GeneViewRuntimeExceptionType.VIRTUALARRAY );
		}
		
		iCurrentVirtualIndex = iSetVirtualIndex;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.ICollectionIterator#next()
	 */
	public int next() {
		final int iCurrentRealIndex = iCurrentVirtualIndex + iSelectionOffset;
				
		iCurrentVirtualIndex++;
		return iCurrentRealIndex;
	}

	public String toString() {
		String result = "[ " + iCurrentVirtualIndex + 
			"# " + iSelectionOffset + 
			":" + (iSelectionOffset + iSelectionLength) + "]";
		
		return result;
	}


	public void setToEnd()
	{
		iCurrentVirtualIndex = iSelectionLength;		
	}

}
