package org.caleydo.core.view.swt.collab;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.net.StandardGroupwareManager;
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
		StandardGroupwareManager groupwareManager = new StandardGroupwareManager();
		groupwareManager.setNetworkName("Server-0");
		GeneralManager.get().setGroupwareManager(groupwareManager);
		groupwareManager.startServer();
	}

}
