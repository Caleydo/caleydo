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
//import prometheus.data.collection.SelectionType;

import cerberus.util.exception.CerberusExceptionType;
import cerberus.util.exception.CerberusRuntimeException;

/**
 * Automatical creates the suitable iterator for several ISelection's.
 * 
 * Desing Pattern "Factory"
 * 
 * @author Michael Kalkusch
 *
 */
public class SelectionIteratorFactory {

//	/**
//	 * Reference to the actual iterator.
//	 */
//	protected SelectionIterator refSelectionIterator;
	
	/**
	 * 
	 */
	public SelectionIteratorFactory() {
	
	}

	public SelectionIterator iterator( ISelection setSelection ) {
		
		assert setSelection != null : "can not handle null-pointer ISelection";
		
		SelectionIterator refSelectionIterator = null;
		
		switch ( setSelection.getSelectionType() ) {
		
		case SELECTION_SINGLE_BLOCK:
			refSelectionIterator = new SelectionSingleBlockIterator( setSelection );
			break;
			
		case SELECTION_MULTI_BLOCK:
			refSelectionIterator = new SelectionMultiBlockIterator( setSelection );
			break;
			
		case SELECTION_MULTI_BLOCK_RLE:
			
		case SELECTION_RANDOM_BLOCK:
			
		default:
			
			throw new CerberusRuntimeException("SelectionProxyIterator.Constructor with unsuppoerte selection type: [" +
					setSelection.getSelectionType() + "] !",
					CerberusExceptionType.VIRTUALARRAY );
		}
		
		return refSelectionIterator;
	}	

}
