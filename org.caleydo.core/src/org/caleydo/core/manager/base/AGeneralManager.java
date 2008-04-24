package org.caleydo.core.manager.base;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISingleton;
import org.caleydo.core.manager.singleton.SingletonManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.manager.type.ManagerType;

import org.caleydo.core.util.exception.CaleydoRuntimeException;

/**
 * Abstract class for general manager
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AGeneralManager 
implements IGeneralManager {

	protected final IGeneralManager generalManager;
	
	protected final ISingleton singelton;
	
	/**
	 * Get ISingelton from refGeneralManager. 
	 * If refGeneralManager did not define a ISingelton a new one is created.
	 */
	public AGeneralManager(IGeneralManager generalManager ) {

		this.generalManager = generalManager;
				
		ISingleton dummySingelton = generalManager.getSingleton();
		
		if ( dummySingelton != null ) {
			this.singelton = dummySingelton;
		} else {
			this.singelton = new SingletonManager();	
		}
		
	}

	/**
	 * Creates a new ISingelton. 
	 */
	public AGeneralManager(IGeneralManager generalManager,
			SingletonManager singeltonManager) {
		
		this.generalManager = generalManager;
		
		/** Check for inconsistency... */
		if (generalManager.getSingleton() != null ) 
		{
			throw new CaleydoRuntimeException("AGeneralManager() refGeneralManager already has a  proper ISingelton!");
		}
		
		this.singelton = singeltonManager;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getGeneralManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return this.generalManager;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#getSingelton()
	 */
	public final ISingleton getSingleton() {
		return this.singelton;
	}


	/* (non-Javadoc)
	 * @see org.caleydo.core.manager.IGeneralManager#createNewId(org.caleydo.core.manager.type.ManagerObjectType)
	 */
	public final int createNewId(ManagerObjectType setNewBaseType) {
		return createNewIdByManager( setNewBaseType.getGroupType() );
	}
	
	public final int createNewIdByManager(ManagerType setManagerType) {
		
		switch ( setManagerType ) {
			case COMMAND:
			
			default: throw new CaleydoRuntimeException("AGeneralManager: Can not handle type [" +
					setManagerType.toString() + "]");
		}				
	}
	
	public void destroyOnExit() {
		
	}
}
