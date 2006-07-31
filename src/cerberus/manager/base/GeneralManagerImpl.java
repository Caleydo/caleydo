/**
 * 
 */
package cerberus.manager.base;

import cerberus.manager.GeneralManager;
import cerberus.manager.Singelton;
import cerberus.manager.singelton.SingeltonManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;

import cerberus.util.exception.CerberusRuntimeException;

/**
 * @author kalkusch
 *
 */
public abstract class GeneralManagerImpl implements GeneralManager {

	protected final GeneralManager refGeneralManager;
	
	protected final Singelton refSingelton;
	
	/**
	 * Get Singelton from refGeneralManager. 
	 * If refGeneralManager did not define a Singelton a new one is created.
	 */
	public GeneralManagerImpl( GeneralManager refGeneralManager ) {
		this.refGeneralManager = refGeneralManager;
				
		Singelton dummySingelton = refGeneralManager.getSingelton();
		
		if ( dummySingelton != null ) {
			this.refSingelton = dummySingelton;
		} else {
			this.refSingelton = new SingeltonManager( this );	
		}
		
	}

	/**
	 * Creates a new Singelton. 
	 */
	public GeneralManagerImpl( GeneralManager refGeneralManager,
			SingeltonManager refSingeltonManager) {
		
		this.refGeneralManager = refGeneralManager;
		
		/** Check for inconsistency... */
		if ( refGeneralManager.getSingelton() != null ) {
			throw new CerberusRuntimeException("GeneralManagerImpl() refGeneralManager already has a  proper Singelton!");
		}
		
		this.refSingelton = refSingeltonManager;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.GeneralManager#getGeneralManager()
	 */
	public final GeneralManager getGeneralManager() {
		return this.refGeneralManager;
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.GeneralManager#getSingelton()
	 */
	public final Singelton getSingelton() {
		return this.refSingelton;
	}


	/* (non-Javadoc)
	 * @see cerberus.manager.GeneralManager#createNewId(cerberus.manager.type.ManagerObjectType)
	 */
	public final int createNewId(ManagerObjectType setNewBaseType) {
		return createNewIdByManager( setNewBaseType.getGroupType() );
	}
	
	public final int createNewIdByManager(ManagerType setManagerType) {
		
		switch ( setManagerType ) {
			case COMMAND:
			
			default: throw new CerberusRuntimeException("GeneralManagerImpl: Can not handle type [" +
					setManagerType.toString() + "]");
		}				
	}

}
