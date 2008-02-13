package org.geneview.core.data;

import org.geneview.core.manager.IGeneralManager;

/**
 * Abstract class providing methods defined in IUniqueManagedObject.
 * Stores reference to creator of item in private variable.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class AUniqueManagedObject 
extends AUniqueItem
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

		super( iUniqueId );
		
		assert generalManager != null: "General Manager is NULL";
		
		this.generalManager = generalManager;

	}

	public final IGeneralManager getManager() {
		return this.generalManager;
	}
}
