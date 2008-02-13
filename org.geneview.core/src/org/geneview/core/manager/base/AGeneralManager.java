package org.geneview.core.manager.base;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ISingelton;
import org.geneview.core.manager.singleton.SingletonManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;

import org.geneview.core.util.exception.GeneViewRuntimeException;

/**
 * Abstract class for general manager
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class AGeneralManager 
implements IGeneralManager {

	protected final IGeneralManager generalManager;
	
	protected final ISingelton singelton;
	
	/**
	 * Get ISingelton from refGeneralManager. 
	 * If refGeneralManager did not define a ISingelton a new one is created.
	 */
	public AGeneralManager(IGeneralManager generalManager ) {

		this.generalManager = generalManager;
				
		ISingelton dummySingelton = generalManager.getSingelton();
		
		if ( dummySingelton != null ) {
			this.singelton = dummySingelton;
		} else {
			this.singelton = new SingletonManager( this );	
		}
		
	}

	/**
	 * Creates a new ISingelton. 
	 */
	public AGeneralManager(IGeneralManager generalManager,
			SingletonManager singeltonManager) {
		
		this.generalManager = generalManager;
		
		/** Check for inconsistency... */
		if (generalManager.getSingelton() != null ) 
		{
			throw new GeneViewRuntimeException("AGeneralManager() refGeneralManager already has a  proper ISingelton!");
		}
		
		this.singelton = singeltonManager;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#getGeneralManager()
	 */
	public final IGeneralManager getGeneralManager() {
		return this.generalManager;
	}

	/* (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#getSingelton()
	 */
	public final ISingelton getSingelton() {
		return this.singelton;
	}


	/* (non-Javadoc)
	 * @see org.geneview.core.manager.IGeneralManager#createNewId(org.geneview.core.manager.type.ManagerObjectType)
	 */
	public final int createNewId(ManagerObjectType setNewBaseType) {
		return createNewIdByManager( setNewBaseType.getGroupType() );
	}
	
	public final int createNewIdByManager(ManagerType setManagerType) {
		
		switch ( setManagerType ) {
			case COMMAND:
			
			default: throw new GeneViewRuntimeException("AGeneralManager: Can not handle type [" +
					setManagerType.toString() + "]");
		}				
	}
	
	public void destroyOnExit() {
		
	}
}
