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
 * Iterator for Selecion SelectionSingleBlock
 * 
 * Desing Pattern "Iterator"
 * 
 * @author Michael Kalkusch
 *
 */
public class SelectionSingleBlockIterator 
extends ASelectionIterator
implements ISelectionIterator {

	
	/**
	 * 
	 */
	public SelectionSingleBlockIterator( ISelection setSelection ) {
		super( setSelection );

	}


	/* (non-Javadoc)
	 * @see cerberus.data.collection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(int iSetVirtualIndex)
			throws CerberusRuntimeException {
		
		if (( iSetVirtualIndex < 0 ) || 
				( iSetVirtualIndex >= iSelectionLength) ) {
			throw new CerberusRuntimeException("setVirtualIndex() with index that was out of bounds.",
							CerberusExceptionType.VIRTUALARRAY );
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
