/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.data;

import cerberus.manager.IGeneralManager;
import cerberus.data.IManagedObject;

/**
 * Abstract class stores reference to IGeneralManager.
 * Stored the reference to IGeneralManager in protected final variable.
 * 
 * @author Michael Kalkusch
 *
 * @see cerberus.data.AUniqueReManagedObject
 * @see prometheus.data.xml.MementiItemXML
 */
public abstract class AManagedObject 
implements IManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected final IGeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected AManagedObject( final  IGeneralManager setGeneralManager ) {
		
		assert setGeneralManager != null: "SetFlatSimple() with null pointer";
		
		refGeneralManager = setGeneralManager;

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.UniqueManagedInterface#getManager()
	 */
	public final IGeneralManager getManager() {
		return this.refGeneralManager;
	}
	
}
