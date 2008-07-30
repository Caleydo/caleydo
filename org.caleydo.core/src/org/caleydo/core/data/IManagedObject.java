package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Interface to access managed objects inside the Caleydo framework.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public interface IManagedObject
	extends IUniqueObject
{

	/**
	 * Get the manager that created the item.
	 * 
	 * @return reference to the general manager
	 */
	public IGeneralManager getGeneralManager();

	/**
	 * Set the general manager from external. This is needed after serialization
	 * to update the general manager which was not exported.
	 * 
	 * @param generalManager
	 */
	public void setGeneralManager(final IGeneralManager generalManager);
}
