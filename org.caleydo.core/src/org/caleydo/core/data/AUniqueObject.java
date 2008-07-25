package org.caleydo.core.data;

import java.io.Serializable;

import org.caleydo.core.manager.IGeneralManager;


/**
 * Abstract class providing methods defined in IUniqueManagedObject.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AUniqueObject 
implements IUniqueObject, Serializable {

	/**
	 * Unique Id
	 */
	protected int iUniqueId;

	/**
	 * Constructor.
	 */
	protected AUniqueObject(final int iUniqueId) {

		this.iUniqueId = iUniqueId;
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
