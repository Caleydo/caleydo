/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package org.geneview.core.data;

import org.geneview.core.manager.IGeneralManager;
//import org.geneview.core.manager.type.ManagerObjectType;

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
