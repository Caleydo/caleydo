/*
 * Project: GenView
 *  
 */
package org.geneview.core.manager.base;

import org.geneview.core.manager.IAbstractManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ISingelton;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;

/**
 * Base class for manager classes, that connect to the IGeneralManager.
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AAbstractManager implements IAbstractManager  {

	private final ManagerType refManagerType;
	
	protected int iUniqueId_current;
	
	/**
	 * Reference to IGeneralManager set via Constructor
	 */
	protected final IGeneralManager refGeneralManager;
	
	/**
	 * Reference to ISingelton assinged via Constructor
	 */
	protected final ISingelton refSingelton;
	
	/**
	 * 
	 */
	protected AAbstractManager( final IGeneralManager setGeneralManager,
			final int iUniqueId_type_offset,
			final ManagerType setManagerType ) {
		
		assert setGeneralManager != null : "Can not handle null refernence to IGeneralManager!";
		
		refGeneralManager = setGeneralManager;
		
		refManagerType = setManagerType;
		
		refSingelton = refGeneralManager.getSingelton();
				
		iUniqueId_current = calculateInitialUniqueId( iUniqueId_type_offset);
	}
	
	public int calculateInitialUniqueId( final int iUniqueId_type_offset ) {
		return iUniqueId_type_offset * 
			IGeneralManager.iUniqueId_TypeOffsetMultiplyer +
			refGeneralManager.getSingelton() .getNetworkPostfix();
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.AbstractManagerImpl#getGeneralManager()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.IAbstractManager#getGeneralManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return refGeneralManager;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.IAbstractManager#getSingelton()
	 */
	public final ISingelton getSingelton() {
		return refSingelton;
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.AbstractManagerImpl#getGeneralManager()
	 */
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.IAbstractManager#setGeneralManager(org.geneview.core.manager.IGeneralManager)
	 */
	public final void setGeneralManager( IGeneralManager setGeneralManager ) {
		throw new RuntimeException("AAbstractManager::setGeneralManager() is not supported!");
	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.manager.IAbstractManager#setSingelton(org.geneview.core.manager.singelton.SingeltonManager)
	 */
	public final void setSingelton( ISingelton setSingeltonManager ) {
		throw new RuntimeException("AAbstractManager::setSingelton() is not supported!");
	}

//	/* (non-Javadoc)
//	 * @see org.geneview.core.data.manager.GeneralManager#createNewId(org.geneview.core.data.manager.BaseManagerType)
//	 */
//	public final int createNewId(ManagerObjectType setNewBaseType) {
//		
//		iUniqueId_current += IGeneralManager.iUniqueId_Increment;
//		
//		return iUniqueId_current;
//	}
	
	/* (non-Javadoc)
	 * @see org.geneview.core.data.manager.GeneralManager#createNewId(org.geneview.core.data.manager.BaseManagerType)
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
		return refManagerType;
	}
	
	/* 
	 * Remove "final" as soon as required by derived class.
	 */
	public void destroyOnExit() {
		
	}
}
