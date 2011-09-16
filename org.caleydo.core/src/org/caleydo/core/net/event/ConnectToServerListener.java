package org.caleydo.core.net.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.net.NetworkManager;

public class ConnectToServerListener
	extends AEventListener<NetworkManager> {

	@Override
	public void handleEvent(AEvent event) {
		if (event instanceof ConnectToServerEvent) {
			ConnectToServerEvent connectToServerEvent = (ConnectToServerEvent) event;
			String address = connectToServerEvent.getAddress();

			// int port = connectToServerEvent.getPort();

			handler.createConnection(address);
		}
	}

}
