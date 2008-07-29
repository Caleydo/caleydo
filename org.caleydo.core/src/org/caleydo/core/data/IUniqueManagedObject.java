package org.caleydo.core.data;

import org.caleydo.core.data.IManagedObject;
import org.caleydo.core.manager.type.EManagerObjectType;

/**
 * Interface to access prometheus.data.manager.CollectionManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IUniqueManagedObject 
extends IUniqueObject, IManagedObject {

	
	/**
	 * Get the type of this object.
	 * 
	 * @return type of this object
	 */
	public EManagerObjectType getBaseType();
	
}
