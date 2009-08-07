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
	 * @param networkManager {@link NetworkManager} that manages this {@link Server} and
	 * is responsible for creating the {@link Connection}s for incoming client connections.
	 * @param port port to listen for incoming connections
	 */
	public Server(NetworkManager networkManager, int port) {
		this.port = port;
		this.networkManager = networkManager;
	}

	/**
	 * Listens for incoming client-connections and creates {@link Connection}-instances
	 * with the help of the related {@link NetworkManager}. 
	 */
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException ex) {
			System.out.println();
			ex.printStackTrace();
			// TODO handling for shut down
			return;
		}

		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				networkManager.createConnection(clientSocket);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Getter for {@link Server#networkManager}
	 * @return {@link Server#networkManager}
	 */
	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	/**
	 * Getter for {@link Server#networkManager}
	 * @return {@link Server#networkManager}
	 */
	public int getPort() {
		return port;
	}

}
