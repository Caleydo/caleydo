package org.caleydo.core.view.opengl.util.hierarchy;

import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;


/**
 * Class responsible for managing the hierarchies in the bucket.
 * 
 * @author Marc Streit
 */
public class RemoteElementManager
extends AManager<RemoteLevelElement>
{
	private static RemoteElementManager remoteElementManager;
	
	/**
	 * Constructor.
	 * 
	 */
	private RemoteElementManager()
	{
	}
	
	/**
	 * Returns the manager as a singleton object. When first called the
	 * manager is created (lazy).
	 */
	public static RemoteElementManager get()
	{
		if (remoteElementManager == null)
		{
			remoteElementManager = new RemoteElementManager();
		}
		return remoteElementManager;
	}
}
