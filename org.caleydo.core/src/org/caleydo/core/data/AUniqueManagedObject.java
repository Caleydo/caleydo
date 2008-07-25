package org.caleydo.core.data;

import java.io.Serializable;

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
implements IUniqueManagedObject, Serializable {

	/**
	 * Reference to manager, who created this object.
	 */
	protected transient IGeneralManager generalManager;
	
	/**
	 * Constructor.
	 */
	protected AUniqueManagedObject( final int iUniqueId, 
			final IGeneralManager generalManager ) {

		super(iUniqueId);
		
		assert generalManager != null: "General Manager is NULL";
		
		this.generalManager = generalManager;

	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IManagedObject#getManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return this.generalManager;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IManagedObject#setGeneralManager(org.caleydo.core.manager.IGeneralManager)
	 */
	public void setGeneralManager(final IGeneralManager generalManager)
	{
		this.generalManager = generalManager;
	}
}
