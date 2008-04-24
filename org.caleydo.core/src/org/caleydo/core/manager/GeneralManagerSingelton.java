/**
 * 
 */
package org.caleydo.core.manager;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.singleton.OneForAllManager;

/**
 * Create or assign a Singleton.
 * Per default a org.caleydo.core.manager.singelton.OneForAllManager is created.
 * Also another IGeneralManager can be assigned 
 *  
 * @author Michael Kalkusch
 *
 */
public final class GeneralManagerSingelton {

	private static IGeneralManager refGeneralManagerSingelton = null;
	
	public synchronized static final IGeneralManager getInstance() {
		if ( refGeneralManagerSingelton == null ) {
			refGeneralManagerSingelton = new OneForAllManager(null);
		}
		return refGeneralManagerSingelton;
	}
	
	/**
	 * This method must be called before the first access 
	 * @param setIGeneralManager
	 * @return
	 */
	public synchronized static final boolean assignInstance(IGeneralManager setIGeneralManager) {
				
		if ( refGeneralManagerSingelton == null ) {		
			
			/** Test if setIGeneralManager!=null and if a valid Singleton-object exists.. */
			if ((setIGeneralManager!= null)&&
					( setIGeneralManager.getSingleton()!= null )) {		
				
				refGeneralManagerSingelton = setIGeneralManager;
				return true;				
			} //if ( setIGeneralManager!= null) {..}
			else {
				
				/** create a new instance with OneForAllManager */
				refGeneralManagerSingelton = new OneForAllManager(null);
				return true;
			} //if ( setIGeneralManager!= null) {..} else {..}
			
		} //if ( refGeneralManagerSingelton == null ) {
		
		return false;
	}
}
