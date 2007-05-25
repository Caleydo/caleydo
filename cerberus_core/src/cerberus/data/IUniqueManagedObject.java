/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.data.IManagedObject;
import cerberus.manager.type.ManagerObjectType;

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
	public ManagerObjectType getBaseType();
	
}
