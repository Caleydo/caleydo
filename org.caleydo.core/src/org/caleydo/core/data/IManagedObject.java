package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Interface to access prometheus.data.manager.CollectionManager
 * 
 * @author Michael Kalkusch
 *
 */
public interface IManagedObject {

	/**
	 * Get the manager that created the item.
	 * 
	 * @return reference to ACollectionManager
	 */
	public IGeneralManager getManager();	

}
