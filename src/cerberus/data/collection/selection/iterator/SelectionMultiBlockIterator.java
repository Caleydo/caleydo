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
 * Iterator for Selecion SelectionMultiBlock
 * 
 * Desing Pattern "Iterator"
 * 
 * @author Michael Kalkusch
 *
 */
public class SelectionMultiBlockIterator 
extends AbstractSelectionIterator
implements SelectionIterator {


	/**
	 * Virtual block offset of a multi-block
	 */
	protected int iVirtualBlockOffset;
	
	/**
	 * Virtual block repeat in a multi-block
	 */
	protected int iVirtualBlockRepeat;
	
	/**
	 * Stores last BlockOffset position
	 */
	protected int iLastBlockOffset;
	
	/**
	 * Index inside a multi-block
	 */
	protected int iCurrentBlockIndex;
	
	/**
	 * Defines the current real index.
	 */
	protected int iCurrentRealIndex;
	
	
	/**
	 * 
	 */
	public SelectionMultiBlockIterator( ISelection setSelection ) {
		
		super( setSelection );
		
	}



	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.SelectionIterator#setVirtualIndex(int)
	 */
	public void setVirtualIndex(final int iSetVirtualIndex)
			throws CerberusRuntimeException {
		
		if (( iSetVirtualIndex < 0 ) || 
				( iSetVirtualIndex >= iSelectionLength) ) {
			throw new CerberusRuntimeException("setVirtualIndex() with index that was out of bounds.",
							CerberusExceptionType.VIRTUALARRAY );
		}				
		
		final int iCountMultiBlocks = (int) ( (float) iCurrentVirtualIndex % (float) iVirtualBlockRepeat );
		
		iLastBlockOffset = iSelectionOffset + (iCountMultiBlocks * iVirtualBlockOffset);
		iCurrentBlockIndex = iSetVirtualIndex - (iCountMultiBlocks * iVirtualBlockRepeat);
		
		iCurrentVirtualIndex = iSetVirtualIndex;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#begin()
	 */
	public void begin() {
		
		super.begin();
		
		iVirtualBlockOffset = refSelection.getMultiOffset();
		iVirtualBlockRepeat = refSelection.getMultiRepeat();
		iCurrentVirtualIndex = 0;
		
		// set virtual block index to 0...
		iCurrentBlockIndex = 1;
		// initialize block offset with current SelectionOffset...
		iLastBlockOffset = iSelectionOffset;
		
		iCurrentRealIndex = iSelectionOffset;
	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.iterator.CollectionIterator#next()
	 */
	public int next() {
		
		final int iCurrentRealIndexBuffer = iCurrentRealIndex;
			
		if ( iCurrentBlockIndex < iVirtualBlockRepeat) {
			// increment virtual block index...
			iCurrentBlockIndex++;
			
			//increment real index...
			iCurrentRealIndex++;
		}
		else {
			///reached end of block, go to begin of next block..
			//update last-block index to current block...
			iLastBlockOffset += iVirtualBlockOffset;
			/// reset real index to begin of new block...
			iCurrentRealIndex = iLastBlockOffset;
			// reset counter for index in block...
			iCurrentBlockIndex = 1;
			
		}
		
		// increment virutal index...
		iCurrentVirtualIndex++;
		
		return iCurrentRealIndexBuffer;
	}

	public String toString() {
		String result ="[ " + iCurrentVirtualIndex + 
			"# " + iSelectionOffset + 
			":" + (iSelectionOffset + iSelectionLength) + " (" +
			iCurrentBlockIndex + "->" +
			iVirtualBlockRepeat + ":" +
			iVirtualBlockOffset + "]";
		
		return result;
	}

}
