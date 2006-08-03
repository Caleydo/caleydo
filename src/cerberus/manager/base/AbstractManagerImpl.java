/**
 * 
 */
package cerberus.manager.base;

import cerberus.manager.AbstractManager;
import cerberus.manager.GeneralManager;
import cerberus.manager.Singelton;
import cerberus.manager.type.ManagerObjectType;

/**
 * Base class for manager classes, that connect to the GeneralManager.
 * 
 * @author kalkusch
 *
 */
public abstract class AbstractManagerImpl implements AbstractManager  {

	protected int iUniqueId_current;
	
	/**
	 * Reference to GeneralManager set via Constructor
	 */
	protected final GeneralManager refGeneralManager;
	
	/**
	 * 
	 */
	protected AbstractManagerImpl( final GeneralManager setGeneralManager,
			final int iUniqueId_type_offset ) {
		refGeneralManager = setGeneralManager;
		
		iUniqueId_current = iUniqueId_type_offset * 
		GeneralManager.iUniqueId_TypeOffset +
		setGeneralManager.getSingelton().getNetworkPostfix();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManager#getGeneralManager()
	 */
	public final GeneralManager getGeneralManager() {
		return refGeneralManager;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManager#getSingelton()
	 */
	public final Singelton getSingelton() {
		return refGeneralManager.getSingelton();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManagerImpl#getGeneralManager()
	 */
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManager#setGeneralManager(cerberus.manager.GeneralManager)
	 */
	public final void setGeneralManager( GeneralManager setGeneralManager ) {
		throw new RuntimeException("AbstractManagerImpl::setGeneralManager() is not supported!");
	}
	
	/* (non-Javadoc)
	 * @see cerberus.manager.AbstractManager#setSingelton(cerberus.manager.singelton.SingeltonManager)
	 */
	public final void setSingelton( Singelton setSingeltonManager ) {
		throw new RuntimeException("AbstractManagerImpl::setSingelton() is not supported!");
	}

	/* (non-Javadoc)
	 * @see cerberus.data.manager.GeneralManager#createNewId(cerberus.data.manager.BaseManagerType)
	 */
	public final int createNewId(ManagerObjectType setNewBaseType) {
		
		iUniqueId_current += GeneralManager.iUniqueId_Increment;
		
		return iUniqueId_current;
	}

	public final GeneralManager getManagerByBaseType(ManagerObjectType managerType) {
		assert false : "Do not call this methode. use singelton only.";
		return null;
	}
}
