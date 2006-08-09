/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;

/**
 * Interface to access prometheus.data.manager.CollectionManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IUniqueManagedObject 
extends IUniqueObject {

	/**
	 * Get the manager that created the item.
	 * 
	 * @return reference to CollectionManager
	 */
	public GeneralManager getManager();	
	
	/**
	 * Get the type of this object.
	 * 
	 * @return type of this object
	 */
	public ManagerObjectType getBaseType();
	

}
