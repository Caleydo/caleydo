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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A {@link Server} should run in an own {@link Thread} to listen for incoming client connections.
 */
public class Server
	implements Runnable {

	/** {@link NetworkManager} related to this {@link Server} */
	private NetworkManager networkManager;

	/** port to this server should listen for incoming connections */
	private int port;

	/** {@link ServerSocket} to listen at */
	ServerSocket serverSocket;

	/**
	 * Creates a new instance for listening on the specified port.
	 * 
	 * @param networkManager
	 *            {@link NetworkManager} that manages this {@link Server} and is responsible for creating the
	 *            {@link Connection}s for incoming client connections.
	 * @param port
	 *            port to listen for incoming connections
	 */
	public Server(NetworkManager networkManager, int port) {
		this.port = port;
		this.networkManager = networkManager;
	}

	/**
	 * Listens for incoming client-connections and creates {@link Connection}-instances with the help of the
	 * related {@link NetworkManager}.
	 */
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
		}
		catch (IOException ex) {
			System.out.println();
			ex.printStackTrace();
			// TODO handling for shut down
			return;
		}

		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				networkManager.createConnection(clientSocket);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Getter for {@link Server#networkManager}
	 * 
	 * @return {@link Server#networkManager}
	 */
	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	/**
	 * Getter for {@link Server#networkManager}
	 * 
	 * @return {@link Server#networkManager}
	 */
	public int getPort() {
		return port;
	}

}
