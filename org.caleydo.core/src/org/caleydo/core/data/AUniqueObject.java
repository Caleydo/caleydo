package org.caleydo.core.data;

import org.caleydo.core.manager.IGeneralManager;


/**
 * Abstract class providing methods defined in IUniqueManagedObject.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AUniqueObject 
implements IUniqueObject {

	/**
	 * Unique Id
	 */
	protected int iUniqueId;
	
	protected IGeneralManager generalManager;

	/**
	 * Constructor.
	 */
	protected AUniqueObject(final int iUniqueId,
			final IGeneralManager generalManager) {

		this.iUniqueId = iUniqueId;
		this.generalManager = generalManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueObject#getId()
	 */
	public final int getId() {
		
		return this.iUniqueId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.data.IUniqueObject#setId(int)
	 */
	public final void setId(final int iSetDNetEventId) {
		
		this.iUniqueId = iSetDNetEventId;
	}
}
