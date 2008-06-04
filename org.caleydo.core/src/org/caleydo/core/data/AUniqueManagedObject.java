package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Abstract class providing methods defined in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class AUniqueManagedObject 
extends AUniqueObject
implements IUniqueManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected final IGeneralManager generalManager;
	
	/**
	 * 
	 */
	protected AUniqueManagedObject( final int iUniqueId, 
			final IGeneralManager generalManager ) {

		super(iUniqueId, generalManager);
		
		assert generalManager != null: "General Manager is NULL";
		
		this.generalManager = generalManager;

	}

	public final IGeneralManager getManager() {
		return this.generalManager;
	}
}
