package org.caleydo.core.net.event;

import java.util.List;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.event.AEvent;

/**
 * Event for distributing the list of all clients to each of the connected clients. This event should be send
 * from the server to its clients whenever a new client connects or an existing client disconnects.
 * 
 * @author Werner Puff
 */
@XmlType
@XmlRootElement
public class ClientListEvent
	extends AEvent {

	/** list of clients */
	private List<String> clientNames;

	@Override
	public boolean checkIntegrity() {
		if (clientNames == null) {
			return false;
		}
		return true;
	}

	/**
	 * Getter for {@link ClientListEvent#clientNames}
	 * 
	 * @return {@link ClientListEvent#clientNames}
	 */
	@XmlElementWrapper
	public List<String> getClientNames() {
		return clientNames;
	}

	/**
	 * Setter for {@link ClientListEvent#clientNames}
	 * 
	 * @param clientNames
	 *            new {@link ClientListEvent#clientNames} to set
	 */
	public void setClientNames(List<String> clientNames) {
		this.clientNames = clientNames;
	}

}
