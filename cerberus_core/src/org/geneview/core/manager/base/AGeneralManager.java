/**
 * 
 */
package org.geneview.core.manager.base;

import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ISingelton;
import org.geneview.core.manager.singleton.SingletonManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.manager.type.ManagerType;

import org.geneview.core.util.exception.GeneViewRuntimeException;

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
			throw new GeneViewRuntimeException("AGeneralManager() refGeneralManager already has a  proper ISingelton!");
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
			
			default: throw new GeneViewRuntimeException("AGeneralManager: Can not handle type [" +
					setManagerType.toString() + "]");
		}				
	}
	
	public void destroyOnExit() {
		
	}

}
