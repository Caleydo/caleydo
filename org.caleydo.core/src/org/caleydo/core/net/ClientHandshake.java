package org.caleydo.core.net;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Handshake message sent from a Caleydo Application that connects to a server.
 * After sending a valid {@link ClientHandshake} message the Server should answer
 * with a {@link ServerHandshake} message.
 * 
 * @author Werner Puff
 */
@XmlRootElement(name = "clientHandshake")
@XmlType(name = "ClientHandshake")
public class ClientHandshake {

	/** request-type to signal that the server that this client wants to connect */
	public static final String REQUEST_CONNECT = "connect";
	
	/** request-type to signal that the connection is established */ 
	public static final String REQUEST_CONNECTION_ESTABLISHED = "connection_established";
	
	/** request-type to signal client is successfully synchronized with the server data */ 
	public static final String CLIENT_SYNCHRONIZED = "client_synchronized";
	
	/** the network name the client chooses for itself */
	private String clientNetworkName;

	/** caleydo version of the client */
	private String version;

	/** request type */
	private String requestType;
	
	public String getClientNetworkName() {
		return clientNetworkName;
	}

	public void setClientNetworkName(String name) {
		this.clientNetworkName = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String toString() {
		String s = "ClientHandshake { ";
		s += "clientNetworkName=" + clientNetworkName + ", ";
		s += "version=" + version + " ";
		s += "}";
		return s;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
}
