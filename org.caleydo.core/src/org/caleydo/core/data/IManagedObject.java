package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Interface to access managed objects inside the Caleydo framework.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface IManagedObject {

	/**
	 * Get the manager that created the item.
	 * 
	 * @return reference to a specific manager
	 */
	public IGeneralManager getManager();	
}
