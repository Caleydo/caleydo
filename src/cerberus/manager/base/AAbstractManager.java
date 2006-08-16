/**
 * 
 */
package cerberus.manager.base;

import cerberus.manager.IAbstractManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.type.ManagerObjectType;

/**
 * Base class for manager classes, that connect to the IGeneralManager.
 * 
 * @author kalkusch
 *
 */
public abstract class AAbstractManager implements IAbstractManager  {

	protected int iUniqueId_current;
	
	/**
	 * Reference to IGeneralManager set via Constructor
	 */
	protected final IGeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected AAbstractManager( final IGeneralManager setGeneralManager,
			final int iUniqueId_type_offset ) {
		refGeneralManager = setGeneralManager;
		
		iUniqueId_current = iUniqueId_type_offset * 
		IGeneralManager.iUniqueId_TypeOffset +
		setGeneralManager.getSingelton().getNetworkPostfix();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	/* (non-Javadoc)
	 * @see cerberus.manager.IAbstractManager#getGeneralManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return refGeneralManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.IAbstractManager#getSingelton()
	 */
	public final ISingelton getSingelton() {
		return refGeneralManager.getSingelton();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	/* (non-Javadoc)
	 * @see cerberus.manager.IAbstractManager#setGeneralManager(cerberus.manager.IGeneralManager)
	 */
	public final void setGeneralManager( IGeneralManager setGeneralManager ) {
		throw new RuntimeException("AAbstractManager::setGeneralManager() is not supported!");
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.IAbstractManager#setSingelton(cerberus.manager.singelton.SingeltonManager)
	 */
	public final void setSingelton( ISingelton setSingeltonManager ) {
		throw new RuntimeException("AAbstractManager::setSingelton() is not supported!");
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#createNewId(cerberus.data.manager.BaseManagerType)
	 */
	public final int createNewId(ManagerObjectType setNewBaseType) {
		
		iUniqueId_current += IGeneralManager.iUniqueId_Increment;
		
		return iUniqueId_current;
	}

	public final IGeneralManager getManagerByBaseType(ManagerObjectType managerType) {
		assert false : "Do not call this methode. use singelton only.";
		return null;
	}
}
