package org.caleydo.core.net;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Handshake message sent from a Caleydo Server Application to a Caleydo Client Application after validation
 * of the {@link ClientHandshake} sent by the client.
 * 
 * @author Werner Puff
 */
@XmlRootElement(name = "serverHandshake")
@XmlType(name = "ServerHandshake")
public class ServerHandshake {

	public static final String ERROR_VERSION_CONFLICT = "Server and Client versions do not match";

	/** the network name of the server */
	String serverNetworkName;

	/** the network name the client is given by the server */
	String clientNetworkName;

	/** caleydo version of the server */
	String version;

	/** possible client handshake message validation error, <code>null</code> for no errors */
	String error;

	public String getServerNetworkName() {
		return serverNetworkName;
	}

	public void setServerNetworkName(String serverNetworkName) {
		this.serverNetworkName = serverNetworkName;
	}

	public String getClientNetworkName() {
		return clientNetworkName;
	}

	public void setClientNetworkName(String clientNetworkName) {
		this.clientNetworkName = clientNetworkName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String toString() {
		String s = "ServerHandshake { ";
		s += "serverNetworkName=" + serverNetworkName + ", ";
		s += "clientNetworkName=" + clientNetworkName + ", ";
		s += "version=" + version + " ";
		s += "}";
		return s;
	}

}
