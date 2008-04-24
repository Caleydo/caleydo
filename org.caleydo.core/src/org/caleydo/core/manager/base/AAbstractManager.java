package org.caleydo.core.manager.base;

import org.caleydo.core.manager.IAbstractManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISingleton;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;

/**
 * Base class for manager classes, that connect to the IGeneralManager.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AAbstractManager 
implements IAbstractManager  {

	private final ManagerType managerType;
	
	protected int iUniqueId_current;
	
	/**
	 * Reference to IGeneralManager set via Constructor
	 */
	protected final IGeneralManager generalManager;
	
	/**
	 * Reference to ISingelton assigned via Constructor
	 */
	protected final ISingleton singelton;
	
	/**
	 * Constructor.
	 */
	protected AAbstractManager(final IGeneralManager generalManager,
			final int iUniqueId_type_offset,
			final ManagerType managerType ) {
		
		assert generalManager != null : "Can not handle null refernence to IGeneralManager!";
		
		this.generalManager = generalManager;
		this.managerType = managerType;
		
		singelton = generalManager.getSingleton();
				
		iUniqueId_current = calculateInitialUniqueId( iUniqueId_type_offset);
	}
	
	public int calculateInitialUniqueId( final int iUniqueId_type_offset ) {
		
		return iUniqueId_type_offset * 
			IGeneralManager.iUniqueId_TypeOffsetMultiplyer +
			generalManager.getSingleton().getNetworkPostfix();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IAbstractManager#getGeneralManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return generalManager;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.IAbstractManager#getSingelton()
	 */
	public final ISingleton getSingleton() {
		return singelton;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.IAbstractManager#setGeneralManager(org.caleydo.core.manager.IGeneralManager)
	 */
	public final void setGeneralManager( IGeneralManager setGeneralManager ) {
		throw new RuntimeException("AAbstractManager::setGeneralManager() is not supported!");
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.IAbstractManager#setSingelton(org.caleydo.core.manager.singelton.SingeltonManager)
	 */
	public final void setSingleton( ISingleton setSingletonManager ) {
		throw new RuntimeException("AAbstractManager::setSingelton() is not supported!");
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.manager.GeneralManager#createNewId(org.caleydo.core.data.manager.BaseManagerType)
	 */
	public int createId(ManagerObjectType setNewBaseType) {
		
		iUniqueId_current += IGeneralManager.iUniqueId_Increment;
		
		return iUniqueId_current;
	}
	
	/**
	 * Set a new 
	 * @param setNewBaseType
	 * @param iCurrentId
	 * @return
	 */
	public boolean setCreateNewId(ManagerType setNewBaseType, final int iCurrentId ) {

		if ( iCurrentId < iUniqueId_current )
		{
			return false;
		}
		
		iUniqueId_current = iCurrentId;
		
		return true;
	}

	public final IGeneralManager getManagerByBaseType(ManagerObjectType managerType) {
		assert false : "Do not call this method. use singelton only.";
		return null;
	}
	
	public final ManagerType getManagerType() {
		return managerType;
	}
	
	/* 
	 * Remove "final" as soon as required by derived class.
	 */
	public void destroyOnExit() {
		
	}
}
