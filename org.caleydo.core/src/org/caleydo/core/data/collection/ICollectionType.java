package org.caleydo.core.data.collection;

import org.caleydo.core.util.ICaleydoDefaultType;

/**
 * Base calls for types used to define collections.
 * 
 * @author Michael Kalkusch
 *
 */
public interface ICollectionType <T> 
extends ICaleydoDefaultType <T> {

	/**
	 * Tells if this type provides data.
	 * 
	 * @return TRUE if type provides data.
	 */
	public boolean isDataType();
	
}
