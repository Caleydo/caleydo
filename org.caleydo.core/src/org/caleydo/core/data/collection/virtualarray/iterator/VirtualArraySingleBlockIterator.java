package org.caleydo.core.data.collection.virtualarray.iterator;

import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Iterator for Selection VirtualArraySingleBlock
 * 
 * Design Pattern "Iterator"
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
	 * @see org.caleydo.core.data.collection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(int iSetVirtualIndex)
			throws CaleydoRuntimeException {
		
		if (( iSetVirtualIndex < 0 ) || 
				( iSetVirtualIndex >= iSelectionLength) ) {
			throw new CaleydoRuntimeException("setVirtualIndex() with index that was out of bounds.",
							CaleydoRuntimeExceptionType.VIRTUALARRAY );
		}
		
		iCurrentVirtualIndex = iSetVirtualIndex;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.iterator.ICollectionIterator#next()
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
