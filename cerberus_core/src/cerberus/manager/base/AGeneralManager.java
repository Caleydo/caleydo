/**
 * 
 */
package cerberus.manager.base;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ISingelton;
import cerberus.manager.singleton.SingletonManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author Michael Kalkusch
 *
 */
public abstract class AGeneralManager implements IGeneralManager {

	protected final IGeneralManager refGeneralManager;
	
	protected final ISingelton refSingelton;
	
	/**
	 * Get ISingelton from refGeneralManager. 
	 * If refGeneralManager did not define a ISingelton a new one is created.
	 */
	public AGeneralManager( IGeneralManager refGeneralManager ) {
		this.refGeneralManager = refGeneralManager;
				
		ISingelton dummySingelton = refGeneralManager.getSingelton();
		
		if ( dummySingelton != null ) {
			this.refSingelton = dummySingelton;
		} else {
			this.refSingelton = new SingletonManager( this );	
		}
		
	}

	/**
	 * Creates a new ISingelton. 
	 */
	public AGeneralManager( IGeneralManager refGeneralManager,
			SingletonManager refSingeltonManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		/** Check for inconsistency... */
		if ( refGeneralManager.getSingelton() != null ) {
			throw new CerberusRuntimeException("AGeneralManager() refGeneralManager already has a  proper ISingelton!");
		}
		
		this.refSingelton = refSingeltonManager;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#getGeneralManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return this.refGeneralManager;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#getSingelton()
	 */
	public final ISingelton getSingelton() {
		return this.refSingelton;
	}


	/* (non-Javadoc)
	 * @see cerberus.manager.IGeneralManager#createNewId(cerberus.manager.type.ManagerObjectType)
	 */
	public final int createNewId(ManagerObjectType setNewBaseType) {
		return createNewIdByManager( setNewBaseType.getGroupType() );
	}
	
	public final int createNewIdByManager(ManagerType setManagerType) {
		
		switch ( setManagerType ) {
			case COMMAND:
			
			default: throw new CerberusRuntimeException("AGeneralManager: Can not handle type [" +
					setManagerType.toString() + "]");
		}				
	}
	
	public void destroyOnExit() {
		
	}

}
