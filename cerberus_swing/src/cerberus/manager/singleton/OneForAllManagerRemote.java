/**
 * 
 */
package cerberus.manager.singleton;

import cerberus.manager.singleton.OneForAllManager;
import cerberus.manager.dcomponent.DComponentSwingFactoryManager;

/**
 * @author michael
 *
 */
public class OneForAllManagerRemote extends OneForAllManager {

	/**
	 * @param sef_SingeltonManager
	 */
	public OneForAllManagerRemote(SingletonManager sef_SingeltonManager) {

		super(sef_SingeltonManager);
		// TODO Auto-generated constructor stub
	}

	public void initAll() {
		super.initAll();
		refDComponentManager = new DComponentSwingFactoryManager(this);
	}
}
