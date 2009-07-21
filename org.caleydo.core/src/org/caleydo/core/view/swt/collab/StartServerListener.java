package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.NetworkManager;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Listener for the "start server" button that triggers the creation of a Server-Thread within the network
 * framework.
 * 
 * @author Werner Puff
 */
public class StartServerListener
	implements Listener {

	@Override
	public void handleEvent(Event event) {
		NetworkManager networkManager = GeneralManager.get().getNetworkManager();
		networkManager.startServer();
	}

}
