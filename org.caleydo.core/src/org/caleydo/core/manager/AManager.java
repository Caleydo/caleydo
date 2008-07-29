package org.caleydo.core.manager;

import java.io.Serializable;

import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;

/**
 * Base class for manager classes, that connect to the IGeneralManager.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class AManager 
implements IManager, Serializable
{
	private static final long serialVersionUID = 1L;

	protected final transient IGeneralManager generalManager;
	
	protected final EManagerType managerType;
	
	protected int iUniqueId_current;
	
	/**
	 * Constructor.
	 */
	protected AManager(final IGeneralManager generalManager,
			final int iUniqueId_type_offset,
			final EManagerType managerType ) {
		
		assert generalManager != null : "Can not handle null refernence to IGeneralManager!";

		this.generalManager = generalManager;
		this.managerType = managerType;
						
		iUniqueId_current = calculateInitialUniqueId( iUniqueId_type_offset);
	}
	
	public int calculateInitialUniqueId( final int iUniqueId_type_offset ) {
		
		return iUniqueId_type_offset * 
			IGeneralManager.iUniqueId_TypeOffsetMultiplyer;
	}
		
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#createNewId(org.caleydo.core.data.manager.BaseManagerType)
	 */
	public int createId(EManagerObjectType setNewBaseType) {
		
		iUniqueId_current += IGeneralManager.iUniqueId_Increment;
		
		return iUniqueId_current;
	}
	
	/**
	 * Set a new 
	 * @param setNewBaseType
	 * @param iCurrentId
	 * @return
	 */
	public boolean setCreateNewId(EManagerType setNewBaseType, final int iCurrentId ) {

		if ( iCurrentId < iUniqueId_current )
		{
			return false;
		}
		
		iUniqueId_current = iCurrentId;
		
		return true;
	}
}
