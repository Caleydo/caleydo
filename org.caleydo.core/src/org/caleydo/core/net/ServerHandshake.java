/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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

	@Override
	public String toString() {
		String s = "ServerHandshake { ";
		s += "serverNetworkName=" + serverNetworkName + ", ";
		s += "clientNetworkName=" + clientNetworkName + ", ";
		s += "version=" + version + " ";
		s += "}";
		return s;
	}

}
