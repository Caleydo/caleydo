package org.caleydo.plex;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.serialize.ApplicationInitData;

import DKT.GroupwareClientAppIPrx;
import DKT.GroupwareClientAppIPrxHelper;
import DKT.GroupwareInformation;
import DKT.MasterApplicationIPrx;
import DKT.ResourceManagerIPrx;
import DKT.ServerApplicationIPrx;
import DKT.ServerApplicationIPrxHelper;
import Ice.Communicator;
import Ice.ObjectAdapter;

/**
 * GroupwareManager for Caleydo applications running in the deskotheque
 * environment. 
 * @see <a href="http://studierstube.icg.tu-graz.ac.at/deskotheque/">Deskotheque</a>
 * @author Werner Puff
 */
public class DeskothequeManager
	implements IGroupwareManager {

	/** default port for incoming ice-connections */
	public static final int DEFAULT_LISTENING_PORT = 8050;
	
	public static final int DKT_SERVER_PORT = 8011;
	
	/** network manager to manage the network connections and network traffic */
	private NetworkManager networkManager;

	/** the address or domain-name of server in case of beeing a client */ 
	private String serverAddress;
	
	/** the name of this application in the network */
	private String networkName;

	/** initialization data for the application read from a server */
	private ApplicationInitData initData;

	private int left;
	
	private int top;
	
	private int width;
	
	private int height;
	
	private Communicator communicator;
	
	private ObjectAdapter adapter;

	private GroupwareClientAppIPrx groupwareClientPrx;
	
	private MasterApplicationIPrx masterPrx;

	private ServerApplicationIPrx serverPrx;
	
	private ResourceManagerIPrx resourceManagerPrx;
	
	private GroupwareInformation groupwareInformation;
	
	/**
	 * Creates an initalized {@link DeskothequeManager}
	 */
	public DeskothequeManager() {
		System.out.println("DeskothequeManager() called");
		networkManager = new NetworkManager();
	}
	
	@Override
	public String[] getAvailableGroupwareClients() {
		return resourceManagerPrx.getAvailableGroupwareClients(groupwareInformation.deskoXID);
	}

	@Override
	public String getHomeGroupwareClient() {
		return resourceManagerPrx.getHomeGroupwareClient(groupwareInformation.deskoXID);
	}

	@Override
	public String getPublicGroupwareClient() {
		return resourceManagerPrx.getPublicGroupwareClient(groupwareInformation.deskoXID);
	}

	@Override
	public void startClient() {
		connectToDeskotheque();

		networkManager.setNetworkName(groupwareInformation.deskoXID);
		networkManager.startNetworkService();
		
		// FIXME obtain server address from deskotheque
		initData = networkManager.createConnection("127.0.0.1");

		// FIXME
		networkManager.setNetworkName(groupwareInformation.deskoXID);
	}

	@Override
	public void startServer() {
		try {
			connectToDeskotheque();
			networkManager.setNetworkName(groupwareInformation.deskoXID);
			networkManager.startNetworkService();
			networkManager.startServer();
		} catch (Exception ex) {
			ex.printStackTrace();
			stop();
		}
		
	}

	private void connectToDeskotheque() {
		communicator = Ice.Util.initialize();

		createAdapter(communicator);

		GroupwareClient groupwareClient = new GroupwareClient();
		Ice.ObjectPrx objPrx = adapter.add(groupwareClient, communicator.stringToIdentity("GroupwareClientAppI"));
		groupwareClientPrx = GroupwareClientAppIPrxHelper.checkedCast(objPrx);

		adapter.activate();

		obtainMasterPrx();
		registerGroupwareServer();
		obtainResourceManagerPrx();
	}

	private void registerGroupwareServer() {
		groupwareInformation = 
			masterPrx.registerGroupwareClient(groupwareClientPrx, "Caleydo", serverPrx, left, top, width, height);

		System.out.println("Groupware information: displayID: "
			+ groupwareInformation.displayID + ", is private: " + groupwareInformation.isPrivate
			+ ", deskoXID: " + groupwareInformation.deskoXID);
	}
	
	private void obtainResourceManagerPrx() {
		// obtaining resource manager proxy
		resourceManagerPrx = masterPrx.getResourceManagerProxy();
	}

	
	private void obtainMasterPrx() {
		// get local host name 
		String hostname = ""; 
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName(); 
			System.out.println("hostname="+hostname); 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		
		// get virtual display 
		String displayVar = System.getenv("DISPLAY"); 
		System.out.println("DISPLAY=" + displayVar); 
		// the display variable is in the format: :0.0 or :1
		// - but we want a single integer instead 
		if (displayVar == null) { // for windows machines the display-var defaults to "0" 
			displayVar = "0";
		} else if(displayVar.length() >= 2) {
			displayVar = displayVar.substring(1, 2);
		}
		
		// the server name is of the form ServerAppI-<hostname>-<xDisplay>
		String serverName = "ServerAppI-" + hostname + "-" + displayVar; 
		// the server endpoint is of the form "tcp -h <hostname> -p 8011"
		// the port 8011 is defined by Deskotheque so we have to 
		// hardcode that value here 
		String serverEndPoint = "tcp -h " + hostname + " -p " + DKT_SERVER_PORT;

		System.out.println("trying to get server from '" + serverName + ":" + serverEndPoint + "'");
		Ice.ObjectPrx proxy = communicator.stringToProxy(serverName + ":" + serverEndPoint);
		ServerApplicationIPrx serverPrx = ServerApplicationIPrxHelper.checkedCast(proxy);

		masterPrx = serverPrx.getMasterProxy();
	}
	
	private void createAdapter(Communicator communicator) {

		int port = DEFAULT_LISTENING_PORT;

		while(adapter == null) {
			try {
				System.out.println("Using port " + port);
				adapter = communicator.createObjectAdapterWithEndpoints("GroupwareClient", "default -p " + port);
			} catch (Ice.SocketException e) {
				// e.printStackTrace();
				System.out.println("Port " + port + " already in use");
			}
			port ++;
		}
	}

	@Override
	public void stop() {

		// release groupware resources
		if (groupwareClientPrx != null) {
			groupwareClientPrx = null;
		}
		if (masterPrx != null) {
			masterPrx = null;
		}
		if (resourceManagerPrx != null) {
			resourceManagerPrx = null;
		}
		if (adapter != null) {
			adapter = null;
		}
		if (communicator != null) {
			communicator.shutdown();
		}
		
		switch (networkManager.getStatus()) {
			case STATUS_CLIENT:
			case STATUS_SERVER:
			case STATUS_STARTED:
				networkManager.stopNetworkService();
				break;
			case STATUS_STOPPED:
				// nothing to do because no network services are running
			break;
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
