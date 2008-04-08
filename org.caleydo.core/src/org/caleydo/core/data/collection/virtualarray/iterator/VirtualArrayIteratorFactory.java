/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.caleydo.core.data.collection.virtualarray.iterator;

import org.caleydo.core.data.collection.IVirtualArray;
//import prometheus.data.collection.SelectionType;

import org.caleydo.core.util.exception.CaleydoRuntimeExceptionType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Automatical creates the suitable iterator for several IVirtualArray's.
 * 
 * Desing Pattern "Factory"
 * 
 * @author Michael Kalkusch
 *
 */
public class VirtualArrayIteratorFactory {

//	/**
//	 * Reference to the actual iterator.
//	 */
//	protected IVirtualArrayIterator refSelectionIterator;
	
	/**
	 * 
	 */
	public VirtualArrayIteratorFactory() {
	
	}

	public IVirtualArrayIterator iterator( IVirtualArray setSelection ) {
		
		assert setSelection != null : "can not handle null-pointer IVirtualArray";
		
		IVirtualArrayIterator refSelectionIterator = null;
		
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
			
			throw new CaleydoRuntimeException("VirtualArrayProxyIterator.Constructor with unsuppoerte selection type: [" +
					setSelection.getSelectionType() + "] !",
					CaleydoRuntimeExceptionType.VIRTUALARRAY );
		}
		
		return refSelectionIterator;
	}	

}
