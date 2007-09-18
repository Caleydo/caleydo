/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data.collection;

import org.geneview.core.util.IGeneViewDefaultType;

/**
 * Base calss for typse used to define collections.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICollectionType <T> 
extends IGeneViewDefaultType <T> {

	/**
	 * Tells if this type provides data.
	 * 
	 * @return TRUE if type provides data.
	 */
	public boolean isDataType();
	
}
