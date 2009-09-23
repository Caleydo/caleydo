package org.caleydo.core.net.event;

import org.caleydo.core.manager.event.AEvent;

public class ConnectToServerEvent
	extends AEvent {

	/** hostname/ip-address to connect to */
	String address = null;

	/** port to connect to */
	int port = -1;

	@Override
	public boolean checkIntegrity() {
		if (address == null) {
			return false;
		}
		return true;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
