package org.caleydo.core.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.ClustererCanceledEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayEvent;
import org.caleydo.core.manager.event.data.ReplaceVirtualArrayInUseCaseEvent;
import org.caleydo.core.manager.event.data.StartClusteringEvent;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.CreateGUIViewEvent;
import org.caleydo.core.manager.event.view.RemoveViewSpecificItemsEvent;
import org.caleydo.core.manager.event.view.ResetAllViewsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.TriggerPropagationCommandEvent;
import org.caleydo.core.manager.event.view.ViewActivationEvent;
import org.caleydo.core.manager.event.view.browser.ChangeQueryTypeEvent;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.manager.event.view.glyph.GlyphChangePersonalNameEvent;
import org.caleydo.core.manager.event.view.glyph.GlyphSelectionBrushEvent;
import org.caleydo.core.manager.event.view.glyph.GlyphUpdatePositionModelEvent;
import org.caleydo.core.manager.event.view.glyph.RemoveUnselectedGlyphsEvent;
import org.caleydo.core.manager.event.view.glyph.SetPositionModelEvent;
import org.caleydo.core.manager.event.view.group.InterchangeGroupsEvent;
import org.caleydo.core.manager.event.view.group.MergeGroupsEvent;
import org.caleydo.core.manager.event.view.histogram.UpdateColorMappingEvent;
import org.caleydo.core.manager.event.view.infoarea.InfoAreaUpdateEvent;
import org.caleydo.core.manager.event.view.pathway.DisableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.DisableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.DisableTexturesEvent;
import org.caleydo.core.manager.event.view.pathway.EnableGeneMappingEvent;
import org.caleydo.core.manager.event.view.pathway.EnableNeighborhoodEvent;
import org.caleydo.core.manager.event.view.pathway.EnableTexturesEvent;
import org.caleydo.core.manager.event.view.radial.ChangeColorModeEvent;
import org.caleydo.core.manager.event.view.radial.GoBackInHistoryEvent;
import org.caleydo.core.manager.event.view.radial.GoForthInHistoryEvent;
import org.caleydo.core.manager.event.view.radial.SetMaxDisplayedHierarchyDepthEvent;
import org.caleydo.core.manager.event.view.radial.UpdateDepthSliderPositionEvent;
import org.caleydo.core.manager.event.view.remote.DisableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.EnableConnectionLinesEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwayEvent;
import org.caleydo.core.manager.event.view.remote.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.event.view.remote.ResetRemoteRendererEvent;
import org.caleydo.core.manager.event.view.remote.ToggleNavigationModeEvent;
import org.caleydo.core.manager.event.view.remote.ToggleZoomEvent;
import org.caleydo.core.manager.event.view.storagebased.AngularBrushingEvent;
import org.caleydo.core.manager.event.view.storagebased.ApplyCurrentSelectionToVirtualArrayEvent;
import org.caleydo.core.manager.event.view.storagebased.BookmarkEvent;
import org.caleydo.core.manager.event.view.storagebased.ChangeOrientationParallelCoordinatesEvent;
import org.caleydo.core.manager.event.view.storagebased.PreventOcclusionEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetAxisSpacingEvent;
import org.caleydo.core.manager.event.view.storagebased.ResetParallelCoordinatesEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.event.view.storagebased.UpdateViewEvent;
import org.caleydo.core.manager.event.view.storagebased.UseRandomSamplingEvent;
import org.caleydo.core.manager.event.view.storagebased.VirtualArrayUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.net.event.ConnectToServerEvent;
import org.caleydo.core.net.event.ConnectToServerListener;
import org.caleydo.core.view.opengl.canvas.radial.event.ClusterNodeSelectionEvent;
import org.caleydo.core.view.swt.collab.RedrawCollabViewEvent;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

/**
 * Manages incoming and outgoing connections and the transmission configurations.
 * 
 * @author Werner Puff
 */
public class NetworkManager 
	implements IListenerOwner {

	ILog log;
	
	/** Default given network name */ 
	public static final String DEFAULT_NETWORK_NAME = "CaleydoApp";

	/** status that indicates that no network-services have been created */
	public static final int STATUS_STOPPED = 1;
	
	/** status that indicates that network services are started, but no client or server is running */
	public static final int STATUS_STARTED= 2;
	
	/** status that indicates that network services are started and a server is running */
	public static final int STATUS_SERVER = 3;
	
	/** status that indicates that network-services are started and this application is connected to a server */
	public static final int STATUS_CLIENT = 4;
	
	/** 
	 * version-id to validate connections of 2 caleydo applications
	 * TODO version should be read from a global variable, maybe general manager? 
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

	/** central JAXBContext for serializing events and views for network transmission */
	private JAXBContext jaxbContext = null;

	/** {@link IGeneralManager} of this caleydo application */
	private IGeneralManager generalManager;

	/** Central {@link IEventPublisher} of this caleydo application */
	private IEventPublisher centralEventPublisher;

	/** status information indicator */
	private int status;
	
	/** caleydo's network server */
	private Server server;
	
	/** {@link Thread} the server runs in */
	private Thread serverThread;
	
	/** counter for the number of clients connected used to generate unique network-ids */
	private int connectionCounter;

	/** {@link AEventListener} to listen for {@link ConnectToServerEvent}s */
	ConnectToServerListener connectToServerListener;
	
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
		
		status = STATUS_STOPPED;
	}

	/**
	 * Initializes and starts the network services. Started network services mean that incoming connections
	 * are handled and it is possible to create outgoing connections to other caleydo clients.
	 */
	public void startNetworkService() {
		// TODO check if already started

		networkUtils = new NetworkUtils();
		
		createGlobalPublisher();
		createJAXBContext();
		registerEventListeners();
		// createTestClientConnection();
		
		status = STATUS_STARTED;
		RedrawCollabViewEvent event = new RedrawCollabViewEvent();
		centralEventPublisher.triggerEvent(event);
	}

	/**
	 * Releases all obtained network related resources.
	 */
	public void stopNetworkService() {
		unregisterEventListeners();
		status = STATUS_STOPPED;
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
	}
	
	/**
	 * 
	 */
	public void startServer() {
		networkName = "CaleydoServer" + "-" + connectionCounter;
		server = new Server(this, listenPort);
		serverThread = new Thread(server, "Server");
		serverThread.start();
		status = STATUS_SERVER;
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
	 * Stops all network threads and frees all obtained resources
	 */
	public void stopServices() {
		// TODO implementation
		status = STATUS_STOPPED;
		RedrawCollabViewEvent event = new RedrawCollabViewEvent();
		centralEventPublisher.triggerEvent(event);
	}

	/**
	 * Creates the {@link JAXBContext} to serialize the network traffic.
	 * @return JAXBContext for network related serialization
	 */
	private JAXBContext createJAXBContext() {

		Collection<Class<? extends AEvent>>eventTypes = NetworkManager.getAllEventTypes();
		Class<?>[] classes = new Class<?>[eventTypes.size()];
		classes = eventTypes.toArray(classes);

		try {
			jaxbContext = JAXBContext.newInstance(classes);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create jaxb context.", ex);
		}

		return jaxbContext;
	}

	/**
	 * Creates connection for a client that tries to connect to this caleydo server.
	 * @param socket valid {@link Socket} to the client 
	 */
	public void createConnection(Socket socket) {
		Connection connection = new Connection(this);
		try {
			connection.connect(socket);
			connections.add(connection);
			createEventSystem(connection);
			RedrawCollabViewEvent event = new RedrawCollabViewEvent();
			centralEventPublisher.triggerEvent(event);
		} catch (ConnectException ex) {
			try {
				socket.close();
			} catch (IOException ioex) {
				// socket seems dead, nothing to do about it
			}
		}
	}

	/**
	 * TODO docs
	 */
	public void createConnection(String address) {
		InetAddress inetAddress; 
		try {
			inetAddress = InetAddress.getByName(address);
		} catch (UnknownHostException ex) {
			throw new RuntimeException("Could not resolve host '" + address + "'.");
		}
		createConnection(inetAddress);
	}

	/**
	 * TODO docs
	 */
	public void createConnection(InetAddress inetAddress) {
		Connection connection = new Connection(this);
		try {
			connection.connect(inetAddress, listenPort);
			connections.add(connection);
			createEventSystem(connection);
			status = STATUS_CLIENT;
			RedrawCollabViewEvent event = new RedrawCollabViewEvent();
			centralEventPublisher.triggerEvent(event);
		} catch (ConnectException ex) {
			log.log(new Status(Status.INFO, GeneralManager.PLUGIN_ID, "Could not connect to server", ex));
		}
	}
	
	/**
	 * Creates the event related stack to dispatch events to and retrieve events from
	 * a connected Caleydo application.
	 * @param connection the initialized {@link Connection} to the remote caleydo application
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
	 * Retrieves the global event-bridge configuration. This list contains all events that should be
	 * transmitted to connected caleydo applications by default.
	 * 
	 * @return event-classes to transmit over the network
	 */
	public Collection<Class<? extends AEvent>> getEventBridgeConfiguration() {
		return NetworkManager.getAllEventTypes();
	}

	/**
	 * TODO client specific event-type configuration
	 * 
	 * @param connection
	 *            the {@link Connection} object to get the event-type configuration for.
	 * @return event-classes to transmit over the network
	 */
	public Collection <Class<? extends AEvent>> getEventBridgeConfiguration(Connection connection) {
		return getEventBridgeConfiguration();
	}

	@Override
	public synchronized void queueEvent(AEventListener<? extends IListenerOwner> listener, AEvent event) {
		listener.handleEvent(event);
	}

	/**
	 * Generates and retruns a Collection of all known events.
	 * 
	 * @return event-classes to transmit over the network
	 */
	public static Collection<Class<? extends AEvent>> getAllEventTypes() {
		Collection<Class<? extends AEvent>> eventTypes = new ArrayList<Class<? extends AEvent>>();

		eventTypes.add(LoadPathwayEvent.class);
		eventTypes.add(SelectionCommandEvent.class);
		eventTypes.add(SelectionUpdateEvent.class);
		eventTypes.add(UpdateColorMappingEvent.class);
		eventTypes.add(CreateGUIViewEvent.class);
		eventTypes.add(EnableConnectionLinesEvent.class);
		eventTypes.add(DisableConnectionLinesEvent.class);
		eventTypes.add(LoadPathwaysByGeneEvent.class);
		eventTypes.add(ResetRemoteRendererEvent.class);
		eventTypes.add(ToggleNavigationModeEvent.class);
		eventTypes.add(ToggleZoomEvent.class);
		eventTypes.add(ChangeOrientationParallelCoordinatesEvent.class);
		eventTypes.add(PreventOcclusionEvent.class);
		eventTypes.add(UseRandomSamplingEvent.class);
		eventTypes.add(AngularBrushingEvent.class);
		eventTypes.add(ApplyCurrentSelectionToVirtualArrayEvent.class);
		eventTypes.add(BookmarkEvent.class);
		eventTypes.add(ChangeColorModeEvent.class);
		eventTypes.add(GoBackInHistoryEvent.class);
		eventTypes.add(GoForthInHistoryEvent.class);
		eventTypes.add(SetMaxDisplayedHierarchyDepthEvent.class);
		eventTypes.add(UpdateDepthSliderPositionEvent.class);
		eventTypes.add(RedrawViewEvent.class);
		eventTypes.add(ResetAxisSpacingEvent.class);
		eventTypes.add(ResetParallelCoordinatesEvent.class);
		eventTypes.add(UpdateViewEvent.class);
		eventTypes.add(VirtualArrayUpdateEvent.class);
		eventTypes.add(ClearSelectionsEvent.class);
		eventTypes.add(RemoveViewSpecificItemsEvent.class);
		eventTypes.add(ResetAllViewsEvent.class);
		eventTypes.add(ViewActivationEvent.class);
		eventTypes.add(TriggerPropagationCommandEvent.class);
		eventTypes.add(DisableGeneMappingEvent.class);
		eventTypes.add(DisableNeighborhoodEvent.class);
		eventTypes.add(DisableTexturesEvent.class);
		eventTypes.add(EnableGeneMappingEvent.class);
		eventTypes.add(EnableNeighborhoodEvent.class);
		eventTypes.add(EnableTexturesEvent.class);
		eventTypes.add(InfoAreaUpdateEvent.class);
		eventTypes.add(InterchangeGroupsEvent.class);
		eventTypes.add(MergeGroupsEvent.class);
		eventTypes.add(GlyphChangePersonalNameEvent.class);
		eventTypes.add(GlyphSelectionBrushEvent.class);
		eventTypes.add(GlyphUpdatePositionModelEvent.class);
		eventTypes.add(RemoveUnselectedGlyphsEvent.class);
		eventTypes.add(SetPositionModelEvent.class);
		eventTypes.add(ChangeQueryTypeEvent.class);
		eventTypes.add(ChangeURLEvent.class);
		eventTypes.add(ClustererCanceledEvent.class);
		eventTypes.add(ClusterProgressEvent.class);
		eventTypes.add(RenameProgressBarEvent.class);
		eventTypes.add(ReplaceVirtualArrayEvent.class);
		eventTypes.add(ReplaceVirtualArrayInUseCaseEvent.class);
		eventTypes.add(StartClusteringEvent.class);
		eventTypes.add(ClusterNodeSelectionEvent.class);

//		eventTypes.add(NewSetEvent.class);

		//		eventTypes.add();

		return eventTypes;
	}

	/**
	 * Returns the JAXBContext for serialization of objects that have to be transmitted over the network.
	 * Usually this are events and views.
	 * 
	 * @return JAXBContext for network related serialization
	 */
	public JAXBContext getJaxbContext() {
		return jaxbContext;
	}

	public EventFilterBridge getOutgoingEventBridge() {
		return outgoingEventBridge;
	}

	public void setOutgoingEventBridge(EventFilterBridge globalEventBridge) {
		this.outgoingEventBridge = globalEventBridge;
	}

	public EventPublisher getGlobalOutgoingPublisher() {
		return globalOutgoingPublisher;
	}

	public void setGlobalOutgoingPublisher(EventPublisher globalNetworkEventPublisher) {
		this.globalOutgoingPublisher = globalNetworkEventPublisher;
	}

	public IEventPublisher getCentralEventPublisher() {
		return centralEventPublisher;
	}

	public void setCentralEventPublisher(IEventPublisher centralEventPublisher) {
		this.centralEventPublisher = centralEventPublisher;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}

	public EventFilterBridge getIncomingEventBridge() {
		return incomingEventBridge;
	}

	public void setIncomingEventBridge(EventFilterBridge incomingEventBridge) {
		this.incomingEventBridge = incomingEventBridge;
	}

	public EventPublisher getGlobalIncomingPublisher() {
		return globalIncomingPublisher;
	}

	public void setGlobalIncomingPublisher(EventPublisher incomingNetworkEventPublisher) {
		this.globalIncomingPublisher = incomingNetworkEventPublisher;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public int getConnectionCounter() {
		return connectionCounter;
	}

	public int increaseConnectionCounter() {
		return ++connectionCounter;
	}

	public NetworkUtils getNetworkUtils() {
		return networkUtils;
	}

	public void setNetworkUtils(NetworkUtils networkUtils) {
		this.networkUtils = networkUtils;
	}

	public int getConnectingTimeout() {
		return connectingTimeout;
	}

	public void setConnectingTimeout(int connectingTimeout) {
		this.connectingTimeout = connectingTimeout;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Thread getServerThread() {
		return serverThread;
	}

	public void setServerThread(Thread serverThread) {
		this.serverThread = serverThread;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
