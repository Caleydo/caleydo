package org.caleydo.core.net;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.view.CanvasConnectionMap;
import org.caleydo.core.serialize.ApplicationInitData;

/**
 * GroupwareManager for standard networking environment where each user
 * uses its own desktop for caleydo. One of the caleydo appliations is started as
 * server and all others as clients connected to this server.
 * @author Werner Puff
 */
public class StandardGroupwareManager
	implements IGroupwareManager {

	/** network manager to manage the network connections and network traffic */
	private NetworkManager networkManager;

	/** the address or domain-name of server in case of beeing a client */ 
	private String serverAddress;
	
	/** the name of this application in the network */
	private String networkName;

	/** initialization data for the application read from a server */
	private ApplicationInitData initData;
	
	/**
	 * Creates an initalized {@link StandardGroupwareManager}
	 */
	public StandardGroupwareManager() {
		networkManager = new NetworkManager();
	}
	
	@Override
	public String[] getAvailableGroupwareClients() {
		String[] names;
		switch (networkManager.getStatus()) {
			case STATUS_CLIENT:
				ArrayList<String> clientNameList = new ArrayList<String>(networkManager.getClientNames());
				clientNameList.remove(networkManager.getNetworkName());
				names = clientNameList.toArray(new String[clientNameList.size()]);
			break;
			case STATUS_SERVER:
				java.util.List<Connection> connections = networkManager.getConnections();
				names = new String[connections.size()];
				int i = 0;
				for (Connection connection : connections) {
					names[i++] = connection.getRemoteNetworkName(); 
				}
			break;
			default:
				names = new String[0];
				
		}
		return names;
	}

	@Override
	public String getHomeGroupwareClient() {
		throw new RuntimeException("obtain view not supported in standard network mode");
	}

	@Override
	public String getPublicGroupwareClient() {
		throw new RuntimeException("get public view not supported in standard network mode");
	}

	@Override
	public void sendConnectionLines(HashMap<EIDType, CanvasConnectionMap> canvasConnections) {
		// throw new RuntimeException("no connection line drawing standard network mode");
	}

	@Override
	public void startClient() {
		networkManager.setNetworkName(networkName);
		networkManager.startNetworkService();
		initData = networkManager.createConnection(serverAddress);
	}

	@Override
	public void startServer() {
		networkManager.setNetworkName(networkName);
		networkManager.startNetworkService();
		networkManager.startServer();
	}

	@Override
	public void stop() {
		switch (networkManager.getStatus()) {
			case STATUS_CLIENT:
			case STATUS_SERVER:
			case STATUS_STARTED:
				networkManager.stopNetworkService();
				break;
			case STATUS_STOPPED:
				// nothing to do because no network services are running
			default:
				throw new IllegalStateException("unknown network status: " + networkManager.getStatus());
		}
	}

	@Override
	public void run() {
		
	}
	
	@Override
	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	@Override
	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	@Override
	public boolean isGroupwareConnectionLinesEnabled() {
		return false;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public ApplicationInitData getInitData() {
		return initData;
	}
	
}
