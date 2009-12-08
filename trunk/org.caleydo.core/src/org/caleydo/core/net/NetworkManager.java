package org.caleydo.core.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.event.ClientListEvent;
import org.caleydo.core.net.event.ClientListListener;
import org.caleydo.core.net.event.ConnectToServerEvent;
import org.caleydo.core.net.event.ConnectToServerListener;
import org.caleydo.core.serialize.ApplicationInitData;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.view.swt.collab.RedrawCollabViewEvent;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

/**
 * Manages incoming and outgoing connections and the message-transmission configurations.
 * 
 * @author Werner Puff
 */
public class NetworkManager
	implements IListenerOwner {

	/** utility object for logging, initialized during constructor */
	private ILog log;

	/** Default given network name */
	public static final String DEFAULT_NETWORK_NAME = "CaleydoApp";

	/**
	 * version-id to validate connections of 2 caleydo applications TODO version should be read from a global
	 * variable, maybe general manager?
	 */
	public static final String VERSION = "1.2.3";

	/** port to listen for incoming connections */
	private int listenPort = 12345;

	/** name of this caleydo application within the network */
	private String networkName;

	/** helper class for read/write network operations */
	private NetworkUtils networkUtils;

	/** global {@link EventFilterBridge} to bridge all events to the outgoing network event dispatching system */
	private EventFilterBridge outgoingEventBridge;

	/** {@link EventPublisher} to dispatch outgoing events to each of the connected caleydo applications */
	private EventPublisher globalOutgoingPublisher;

	/** global {@link EventFilterBridge} to bridge all events to the incoming network event dispatching system */
	private EventFilterBridge incomingEventBridge;

	/** {@link EventPublisher} to dispatch incoming events from each of the connected caleydo applications */
	private EventPublisher globalIncomingPublisher;

	/** list of connected caleydo applications */
	private List<Connection> connections;

	/** {@link IGeneralManager} of this caleydo application */
	private IGeneralManager generalManager;

	/** Central {@link IEventPublisher} of this caleydo application */
	private IEventPublisher centralEventPublisher;

	/** status information indicator */
	private ENetworkStatus status;

	/** caleydo's network server */
	private Server server;

	/** {@link Thread} the server runs in */
	private Thread serverThread;

	/** counter for the number of clients connected used to generate unique network-ids */
	private int connectionCounter;

	/** {@link AEventListener} to listen for {@link ConnectToServerEvent}s */
	private ConnectToServerListener connectToServerListener;

	/** {@link AEventListener} to listen for {@link ClientListEvent}s if in client mode */
	private ClientListListener clientListListener;

	/** list of names of all clients connected to the same server as this application */
	private List<String> clientNames;

	/** timeout in ms for a client to send his {@link ClientHandshake} after connecting to this server */
	private int connectingTimeout;

	/**
	 * Default constructor that creates a new initialized {@link NetworkManager}
	 */
	public NetworkManager() {
		networkName = DEFAULT_NETWORK_NAME;

		generalManager = GeneralManager.get();
		log = generalManager.getLogger();
		centralEventPublisher = generalManager.getEventPublisher();

		connections = new ArrayList<Connection>();
		connectingTimeout = 5000;
		connectionCounter = 0;

		status = ENetworkStatus.STATUS_STOPPED;

		clientNames = null;
	}

	/**
	 * Initializes and starts the network services. Started network services mean that incoming connections
	 * are handled and it is possible to create outgoing connections to other caleydo clients.
	 */
	public void startNetworkService() {
		// TODO check if already started

		networkUtils = new NetworkUtils();

		createGlobalPublisher();

		registerEventListeners();
		// createTestClientConnection();

		status = ENetworkStatus.STATUS_STARTED;
		RedrawCollabViewEvent event = new RedrawCollabViewEvent();
		centralEventPublisher.triggerEvent(event);
	}

	/**
	 * Stops all network threads and frees all obtained resources
	 */
	public void stopNetworkService() {
		status = ENetworkStatus.STATUS_STOPPED;

		unregisterEventListeners();
		for (Connection connection : connections) {
			connection.dispose();
		}
		connections.clear();

		RedrawCollabViewEvent event = new RedrawCollabViewEvent();
		centralEventPublisher.triggerEvent(event);
	}

	/**
	 * Registers the event listeners to the central event publishing system.
	 */
	private void registerEventListeners() {
		connectToServerListener = new ConnectToServerListener();
		connectToServerListener.setHandler(this);
		centralEventPublisher.addListener(ConnectToServerEvent.class, connectToServerListener);
	}

	/**
	 * Unregisters the event listeners from the central event publishing system.
	 */
	private void unregisterEventListeners() {
		if (connectToServerListener != null) {
			centralEventPublisher.removeListener(connectToServerListener);
			connectToServerListener = null;
		}
		if (clientListListener != null) {
			centralEventPublisher.removeListener(clientListListener);
			clientListListener = null;
		}
	}

	/**
	 * Starts the network server listening for incoming connections.
	 */
	public void startServer() {
		// networkName = "CaleydoServer" + "-" + connectionCounter;
		server = new Server(this, listenPort);
		serverThread = new Thread(server, "Server");
		serverThread.start();
		status = ENetworkStatus.STATUS_SERVER;
		RedrawCollabViewEvent event = new RedrawCollabViewEvent();
		centralEventPublisher.triggerEvent(event);
	}

	/**
	 * Creates the {@link EventFilterBridge}s and {@link EventPublisher}s to send and receive network events.
	 */
	private void createGlobalPublisher() {
		globalOutgoingPublisher = new EventPublisher();
		outgoingEventBridge = new EventFilterBridge();
		outgoingEventBridge.setName("globalOutgoingEventBridge");
		outgoingEventBridge.setBridgeLocalEvents(true);
		outgoingEventBridge.setBridgeRemoteEvents(true);
		outgoingEventBridge.setTargetEventPublisher(globalOutgoingPublisher);

		globalIncomingPublisher = new EventPublisher();
		incomingEventBridge = new EventFilterBridge();
		incomingEventBridge.setName("globalIncomingEventBridge");
		incomingEventBridge.setBridgeRemoteEvents(true);
		incomingEventBridge.setTargetEventPublisher(centralEventPublisher);

		Collection<Class<? extends AEvent>> eventTypes = getEventBridgeConfiguration();
		for (Class<? extends AEvent> eventClass : eventTypes) {
			centralEventPublisher.addListener(eventClass, outgoingEventBridge);
			globalIncomingPublisher.addListener(eventClass, incomingEventBridge);
		}
	}

	/**
	 * Creates connection for a client that tries to connect to this caleydo server.
	 * 
	 * @param socket
	 *            valid {@link Socket} to the client
	 */
	public void createConnection(Socket socket) {
		Connection connection = new Connection(this);
		try {
			connection.connect(socket);
			connections.add(connection);
			createEventSystem(connection);

			RedrawCollabViewEvent event = new RedrawCollabViewEvent();
			centralEventPublisher.triggerEvent(event);

			publishClientList();
		}
		catch (ConnectException ex) {
			try {
				socket.close();
			}
			catch (IOException ioex) {
				// socket seems dead, nothing to do about it
			}
		}
	}

	/**
	 * Sends the client list to all connected clients. Should be used on a caleydo server application whenever
	 * a client connects or disconnects.
	 */
	private void publishClientList() {
		ClientListEvent clientListEvent = new ClientListEvent();
		ArrayList<String> clientNames = new ArrayList<String>();
		clientNames.add(networkName);
		for (Connection con : connections) {
			clientNames.add(con.getRemoteNetworkName());
		}
		clientListEvent.setClientNames(clientNames);
		globalOutgoingPublisher.triggerEvent(clientListEvent);
	}

	/**
	 * Connects a client to a server running at the given address.
	 * 
	 * @param address
	 *            {@link String}-representation of the internet-address of the caleydo-server-application
	 */
	public ApplicationInitData createConnection(String address) {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(address);
		}
		catch (UnknownHostException ex) {
			throw new RuntimeException("Could not resolve host '" + address + "'.");
		}
		return createConnection(inetAddress);
	}

	/**
	 * Connects a client to a server running at the given address.
	 * 
	 * @param address
	 *            {@link InetAddress} of the caleydo-server-application
	 */
	public ApplicationInitData createConnection(InetAddress inetAddress) {
		Connection connection = new Connection(this);
		ApplicationInitData initData;
		try {
			clientListListener = new ClientListListener();
			clientListListener.setHandler(this);
			centralEventPublisher.addListener(ClientListEvent.class, clientListListener);

			initData = connection.connect(inetAddress, listenPort);

			connections.add(connection);
			createEventSystem(connection);

			status = ENetworkStatus.STATUS_CLIENT;

			RedrawCollabViewEvent event = new RedrawCollabViewEvent();
			centralEventPublisher.triggerEvent(event);
		}
		catch (ConnectException ex) {
			log.log(new Status(Status.INFO, GeneralManager.PLUGIN_ID, "Could not connect to server", ex));
			if (clientListListener != null) {
				centralEventPublisher.removeListener(clientListListener);
				clientListListener = null;
			}
			throw new RuntimeException("Could not connect to server", ex);
		}
		return initData;
	}

	/**
	 * Connects an existing {@link Connection} to the event system of the local caleydo application.
	 * 
	 * @param connection
	 *            the initialized {@link Connection} to the remote caleydo application
	 */
	private void createEventSystem(Connection connection) {
		EventFilterBridge outgoingClientBridge = connection.getOutgoingBridge();

		EventFilterBridge incomingClientBridge = connection.getIncomingBridge();
		NetworkEventReceiver incomingClientPublisher = connection.getIncomingPublisher();

		Collection<Class<? extends AEvent>> eventTypes = getEventBridgeConfiguration(connection);
		for (Class<? extends AEvent> eventClass : eventTypes) {
			incomingClientPublisher.addListener(eventClass, incomingClientBridge);
			globalOutgoingPublisher.addListener(eventClass, outgoingClientBridge);
		}
	}

	/**
	 * Disconnects an {@link Connection} from the event system of the local caleydo application.
	 * 
	 * @param connection
	 *            a {@link Connection} that needs to shutdown
	 */
	private void disposeEventSystem(Connection connection) {
		EventFilterBridge outgoingClientBridge = connection.getOutgoingBridge();

		EventFilterBridge incomingClientBridge = connection.getIncomingBridge();
		NetworkEventReceiver incomingClientPublisher = connection.getIncomingPublisher();

		incomingClientPublisher.removeListener(incomingClientBridge);
		globalOutgoingPublisher.removeListener(outgoingClientBridge);
	}

	/**
	 * TODO docs
	 */
	public void createTestClientConnection() {

		Connection connection = new Connection(this);
		connections.add(connection);

		EventFilterBridge outgoingClientBridge = connection.getOutgoingBridge();
		outgoingClientBridge.setName("outgoingTestBridge");

		EventFilterBridge incomingClientBridge = connection.getIncomingBridge();
		incomingClientBridge.setName("incomingTestBridge");
		NetworkEventReceiver incomingClientPublisher = connection.getIncomingPublisher();

		Collection<Class<? extends AEvent>> eventTypes = getEventBridgeConfiguration(connection);
		for (Class<? extends AEvent> eventClass : eventTypes) {
			incomingClientPublisher.addListener(eventClass, incomingClientBridge);
			globalOutgoingPublisher.addListener(eventClass, outgoingClientBridge);
		}

	}

	/**
	 * Disconnects the given {@link Connection} from the remote caleydo-application and frees all obtained
	 * resources for that {@link Connection}.
	 * 
	 * @param connection
	 *            existing {@link Connection} to disconnect and dispose.
	 */
	public void disposeConnection(Connection connection) {
		if (!connections.remove(connection)) {
			throw new RuntimeException("The specified connection is not managed by this NetworkManager");
		}
		if (status == ENetworkStatus.STATUS_SERVER) {
			publishClientList();
		}
		disposeEventSystem(connection);
		connection.dispose();

	}

	/**
	 * Retrieves the global event-bridge configuration. This list contains all events that should be
	 * transmitted to connected caleydo applications by default. TODO read events from configuration file.
	 * 
	 * @return event-classes to transmit over the network
	 */
	public Collection<Class<? extends AEvent>> getEventBridgeConfiguration() {
		return SerializationManager.getSerializeableEventTypes();
	}

	/**
	 * Retrieves client specific event-bridge configuration. TODO client specific event-type configuration
	 * 
	 * @param connection
	 *            the {@link Connection} object to get the event-type configuration for.
	 * @return event-classes to transmit over the network
	 */
	public Collection<Class<? extends AEvent>> getEventBridgeConfiguration(Connection connection) {
		return getEventBridgeConfiguration();
	}

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}

	/**
	 * Getter for {@link NetworkManager#outgoingEventBridge}
	 * 
	 * @return {@link NetworkManager#outgoingEventBridge}
	 */
	public EventFilterBridge getOutgoingEventBridge() {
		return outgoingEventBridge;
	}

	/**
	 * Setter for {@link NetworkManager#outgoingEventBridge}
	 * 
	 * @param {@link NetworkManager#outgoingEventBridge}
	 */
	public void setOutgoingEventBridge(EventFilterBridge outgoingEventBridge) {
		this.outgoingEventBridge = outgoingEventBridge;
	}

	/**
	 * Getter for {@link NetworkManager#globalOutgoingPublisher}
	 * 
	 * @return {@link NetworkManager#globalOutgoingPublisher}
	 */
	public EventPublisher getGlobalOutgoingPublisher() {
		return globalOutgoingPublisher;
	}

	/**
	 * Setter for {@link NetworkManager#globalOutgoingPublisher}
	 * 
	 * @param {@link NetworkManager#globalOutgoingPublisher}
	 */
	public void setGlobalOutgoingPublisher(EventPublisher globalOutgoingPublisher) {
		this.globalOutgoingPublisher = globalOutgoingPublisher;
	}

	/**
	 * Getter for {@link NetworkManager#centralEventPublisher}
	 * 
	 * @return {@link NetworkManager#centralEventPublisher}
	 */
	public IEventPublisher getCentralEventPublisher() {
		return centralEventPublisher;
	}

	/**
	 * Setter for {@link NetworkManager#centralEventPublisher}
	 * 
	 * @param {@link NetworkManager#centralEventPublisher}
	 */
	public void setCentralEventPublisher(IEventPublisher centralEventPublisher) {
		this.centralEventPublisher = centralEventPublisher;
	}

	/**
	 * Getter for {@link NetworkManager#connections}
	 * 
	 * @return {@link NetworkManager#connections}
	 */
	public List<Connection> getConnections() {
		return connections;
	}

	/**
	 * Setter for {@link NetworkManager#connections}
	 * 
	 * @param {@link NetworkManager#connections}
	 */
	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	/**
	 * Getter for {@link NetworkManager#incomingEventBridge}
	 * 
	 * @return {@link NetworkManager#incomingEventBridge}
	 */
	public EventFilterBridge getIncomingEventBridge() {
		return incomingEventBridge;
	}

	/**
	 * Setter for {@link NetworkManager#incomingEventBridge}
	 * 
	 * @param {@link NetworkManager#incomingEventBridge}
	 */
	public void setIncomingEventBridge(EventFilterBridge incomingEventBridge) {
		this.incomingEventBridge = incomingEventBridge;
	}

	/**
	 * Getter for {@link NetworkManager#globalIncomingPublisher}
	 * 
	 * @return {@link NetworkManager#globalIncomingPublisher}
	 */
	public EventPublisher getGlobalIncomingPublisher() {
		return globalIncomingPublisher;
	}

	/**
	 * Setter for {@link NetworkManager#globalIncomingPublisher}
	 * 
	 * @param {@link NetworkManager#globalIncomingPublisher}
	 */
	public void setGlobalIncomingPublisher(EventPublisher globalIncomingPublisher) {
		this.globalIncomingPublisher = globalIncomingPublisher;
	}

	/**
	 * Getter for {@link NetworkManager#networkName}
	 * 
	 * @return {@link NetworkManager#networkName}
	 */
	public String getNetworkName() {
		return networkName;
	}

	/**
	 * Setter for {@link NetworkManager#networkName}
	 * 
	 * @param {@link NetworkManager#networkName}
	 */
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	/**
	 * Getter for {@link NetworkManager#connectionCounter}
	 * 
	 * @return {@link NetworkManager#connectionCounter}
	 */
	public int getConnectionCounter() {
		return connectionCounter;
	}

	/**
	 * Increases the connection counter by one to provide unique client-IDs
	 */
	public int increaseConnectionCounter() {
		return ++connectionCounter;
	}

	/**
	 * Getter for {@link NetworkManager#networkUtils}
	 * 
	 * @return {@link NetworkManager#networkUtils}
	 */
	public NetworkUtils getNetworkUtils() {
		return networkUtils;
	}

	/**
	 * Setter for {@link NetworkManager#networkUtils}
	 * 
	 * @param {@link NetworkManager#networkUtils}
	 */
	public void setNetworkUtils(NetworkUtils networkUtils) {
		this.networkUtils = networkUtils;
	}

	/**
	 * Getter for {@link NetworkManager#connectingTimeout}
	 * 
	 * @return {@link NetworkManager#connectingTimeout}
	 */
	public int getConnectingTimeout() {
		return connectingTimeout;
	}

	/**
	 * Setter for {@link NetworkManager#connectingTimeout}
	 * 
	 * @param {@link NetworkManager#connectingTimeout}
	 */
	public void setConnectingTimeout(int connectingTimeout) {
		this.connectingTimeout = connectingTimeout;
	}

	/**
	 * Getter for {@link NetworkManager#server}
	 * 
	 * @return {@link NetworkManager#server}
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Setter for {@link NetworkManager#server}
	 * 
	 * @param {@link NetworkManager#server}
	 */
	public void setServer(Server server) {
		this.server = server;
	}

	/**
	 * Getter for {@link NetworkManager#serverThread}
	 * 
	 * @return {@link NetworkManager#serverThread}
	 */
	public Thread getServerThread() {
		return serverThread;
	}

	/**
	 * Setter for {@link NetworkManager#serverThread}
	 * 
	 * @param {@link NetworkManager#serverThread}
	 */
	public void setServerThread(Thread serverThread) {
		this.serverThread = serverThread;
	}

	/**
	 * Getter for {@link NetworkManager#status}
	 * 
	 * @return {@link NetworkManager#status}
	 */
	public ENetworkStatus getStatus() {
		return status;
	}

	/**
	 * Setter for {@link NetworkManager#status}
	 * 
	 * @param {@link NetworkManager#status}
	 */
	public void setStatus(ENetworkStatus status) {
		this.status = status;
	}

	/**
	 * Getter for {@link NetworkManager#clientNames}
	 * 
	 * @return {@link NetworkManager#clientNames}
	 */
	public List<String> getClientNames() {
		return clientNames;
	}

	/**
	 * Setter for {@link NetworkManager#clientNames}
	 * 
	 * @param {@link NetworkManager#clientNames}
	 */
	public void setClientNames(List<String> clientNames) {
		this.clientNames = clientNames;
		System.out.println("clientNames=" + clientNames);
	}

}
