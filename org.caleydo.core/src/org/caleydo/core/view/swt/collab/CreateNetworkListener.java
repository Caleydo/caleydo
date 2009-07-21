package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.NetworkManager;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * {@link AEventListener} that creates the caleydo network framework.
 *  
 * @author Werner Puff
 */
public class CreateNetworkListener
	implements Listener {

	private ILog log = GeneralManager.get().getLogger();
	
	@Override
	public void handleEvent(Event event) {
		log.log(new Status(Status.INFO, GeneralManager.PLUGIN_ID, "create network"));
		NetworkManager networkManager = GeneralManager.get().getNetworkManager();
		networkManager.startNetworkService();
	}

}

