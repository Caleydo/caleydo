package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Abstract class stores reference to IGeneralManager.
 * Stored the reference to IGeneralManager in protected final variable.
 * 
 * @author Michael Kalkusch
 *
 * @see org.caleydo.core.data.AUniqueReManagedObject
 * @see org.caleydo.core.data.xml.MementiItemXML
 */
public abstract class AManagedObject 
implements IManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected final IGeneralManager generalManager;
	
	/**
	 * Constructor.
	 */
	protected AManagedObject( final  IGeneralManager setGeneralManager ) {
		
		assert setGeneralManager != null: "SetFlatSimple() with null pointer";
		
		generalManager = setGeneralManager;

	}

	/* (non-Javadoc)
	 * @see prometheus.data.collection.UniqueManagedInterface#getManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return this.generalManager;
	}
}
