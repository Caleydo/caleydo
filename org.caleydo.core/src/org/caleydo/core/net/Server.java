package org.caleydo.core.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
	implements Runnable {

	/** {@link NetworkManager} related to this {@link Server} */
	private NetworkManager networkManager;

	/** port to this server should listen for incoming connections */
	private int port;

	/** {@link ServerSocket} to listen at */
	ServerSocket serverSocket;

	public Server(NetworkManager networkManager, int port) {
		this.port = port;
		this.networkManager = networkManager;
	}

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

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public int getPort() {
		return port;
	}

}
