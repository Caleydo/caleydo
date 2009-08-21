package org.caleydo.plex;

import java.util.ArrayList;

import org.caleydo.core.net.Connection;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.serialize.ApplicationInitData;

/**
 * GroupwareManager for Caleydo applications running in the deskotheque
 * environment. 
 * @see <a href="http://studierstube.icg.tu-graz.ac.at/deskotheque/">Deskotheque</a>
 * @author Werner Puff
 */
public class DeskothequeManager
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
	 * Creates an initalized {@link DeskothequeManager}
	 */
	public DeskothequeManager() {
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
	public void getHomeGroupwareClient() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getPublicGroupwareClient() {
		// TODO Auto-generated method stub

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
	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	@Override
	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
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
