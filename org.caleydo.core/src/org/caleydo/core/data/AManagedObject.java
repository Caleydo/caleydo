package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;

/**
 * Abstract class stores reference to IGeneralManager.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public abstract class AManagedObject 
extends AUniqueObject
implements IManagedObject {

	/**
	 * Reference to manager, who created this object.
	 */
	protected IGeneralManager generalManager;
	
	/**
	 * Constructor.
	 */
	protected AManagedObject(final int iUniqueID,
			final IGeneralManager generalManager) {

		super(iUniqueID);
		
		this.generalManager = generalManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IManagedObject#getGeneralManager()
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
