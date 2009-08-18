package org.caleydo.core.net.event;

import java.util.List;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.net.NetworkManager;

public class ClientListListener
	extends AEventListener<NetworkManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ClientListEvent) {
			ClientListEvent clientListEvent = (ClientListEvent) event;
			List<String> clientNames = clientListEvent.getClientNames();
			
			// int port = connectToServerEvent.getPort();

			handler.setClientNames(clientNames);
		}
	}

}
