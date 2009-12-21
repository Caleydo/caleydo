package org.caleydo.plex;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.view.selection.AddConnectionLineVerticesEvent;
import org.caleydo.core.manager.event.view.selection.ClearConnectionsEvent;
import org.caleydo.core.manager.event.view.selection.ClearTransformedConnectionsEvent;
import org.caleydo.core.manager.execution.ADisplayLoopEventHandler;
import org.caleydo.core.manager.execution.DisplayLoopExecution;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.CanvasConnectionMap;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.manager.view.SelectionPoint2D;
import org.caleydo.core.manager.view.SelectionPoint2DList;
import org.caleydo.core.net.ENetworkStatus;
import org.caleydo.core.net.IGroupwareManager;
import org.caleydo.core.net.NetworkManager;
import org.caleydo.core.serialize.ApplicationInitData;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import DKT.ConnectionLineVertex;
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
 * 
 * @see <a
 *      href="http://studierstube.icg.tu-graz.ac.at/deskotheque/">Deskotheque</a>
 * @author Werner Puff
 */
public class DeskothequeManager extends ADisplayLoopEventHandler
		implements
			IGroupwareManager {

	/** default port for incoming ice-connections */
	public static final int DEFAULT_LISTENING_PORT = 8050;

	public static final int DKT_SERVER_PORT = 8011;

	/** reference for common usage */
	private IGeneralManager generalManager;

	/** reference for common usage */
	private IEventPublisher eventPublisher;

	/** network manager to manage the network connections and network traffic */
	private NetworkManager networkManager;

	/** the address or domain-name of server in case of beeing a client */
	private String serverAddress;

	/** initialization data for the application read from a server */
	private ApplicationInitData initData;

	/** ice utility object for communication */
	private Communicator communicator;

	/** ice utility object for remote object access */
	private ObjectAdapter adapter;

	/** ice proxy for {@link GroupwareClient} */
	private GroupwareClientAppIPrx groupwareClientPrx;

	/** ice proxy for accessing deskotheque's Master */
	private MasterApplicationIPrx masterPrx;

	/** ice proxy for accessing deskotheque's Server */
	private ServerApplicationIPrx serverPrx;

	/** ice proxy for accessing deskotheque's ResourceManager */
	private ResourceManagerIPrx resourceManagerPrx;

	/**
	 * groupware information for this caleydo application as retrieved from
	 * deskotheque
	 */
	private GroupwareInformation groupwareInformation;

	/**
	 * <code>true</code> to indicate that groupware connection lines should be
	 * redrawn
	 */
	private boolean redrawConnectionLines = false;

	/**
	 * Stores {@link CanvasConnectionMap}s with connection lines in display
	 * coordinates and the related network-name of the display.
	 */
	HashMap<EIDType, CanvasConnectionMap> displayConnectionsByType;

	AddConnectionLineVerticesListener addConnectionLinePointsListener;
	ClearConnectionsListener clearConnectionsListener;
	ClearTransformedConnectionsListener clearTransformedConnectionsListener;

	/**
	 * Creates an initalized {@link DeskothequeManager}
	 */
	public DeskothequeManager() {
		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();

		System.out.println("DeskothequeManager() called");
		displayConnectionsByType = new HashMap<EIDType, CanvasConnectionMap>();
		networkManager = new NetworkManager();
	}

	@Override
	public String[] getAvailableGroupwareClients() {
		return resourceManagerPrx
				.getAvailableGroupwareClients(groupwareInformation.deskoXID);
	}

	@Override
	public String getHomeGroupwareClient() {
		return resourceManagerPrx
				.getHomeGroupwareClient(groupwareInformation.deskoXID);
	}

	@Override
	public String getPublicGroupwareClient() {
		return resourceManagerPrx
				.getPublicGroupwareClient(groupwareInformation.deskoXID);
	}

	@Override
	public void startClient() {
		connectToDeskotheque();

		networkManager.setNetworkName(groupwareInformation.deskoXID);
		networkManager.startNetworkService();

		// FIXME obtain server address from deskotheque
		initData = networkManager.createConnection(serverAddress);

		// FIXME
		networkManager.setNetworkName(groupwareInformation.deskoXID);
		registerEventListeners();
	}

	/**
	 * Starts a caleydo server and registers this application as a server at
	 * deskotheque
	 */
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
		registerEventListeners();
	}

	/**
	 * Connects to deskotheque with the ice-interface of deskotheque.
	 */
	private void connectToDeskotheque() {
		communicator = Ice.Util.initialize();

		createAdapter(communicator);

		GroupwareClient groupwareClient = new GroupwareClient();
		Ice.ObjectPrx objPrx = adapter.add(groupwareClient, communicator
				.stringToIdentity("GroupwareClientAppI"));
		groupwareClientPrx = GroupwareClientAppIPrxHelper.checkedCast(objPrx);

		adapter.activate();

		obtainMasterPrx();
		registerGroupwareServer();
		obtainResourceManagerPrx();
	}

	/**
	 * registers this caleydo application at deskotheque as a server
	 */
	private void registerGroupwareServer() {

		int left = 0;
		int top = 0;
		int width = 800;
		int height = 600;

		try {
			Rectangle rectangle = Display.getDefault().getActiveShell().getBounds();
			left = rectangle.x;
			top = rectangle.y;
			width = rectangle.width;
			height = rectangle.height;
		} catch (Exception e) {
			System.out.println("could not get window position, defaulting to 0-0");
		}
		
		System.out.println("GroupwareClientPrx: " + groupwareClientPrx + 
				" - serverPrx: " + serverPrx + " - extents: " + left + 
				", " + top + " - " + width + "x" + height);

		groupwareInformation = masterPrx.registerGroupwareClient(
				groupwareClientPrx, "Caleydo", serverPrx, left, top, width,
				height);

		// System.out.println("Groupware information: displayID: "
		// + groupwareInformation.displayID + ", is private: " +
		// groupwareInformation.isPrivate
		// + ", deskoXID: " + groupwareInformation.deskoXID);
	}

	/**
	 * Retrieves an ice-proxy for deskotheque's ResourceManager for
	 * communication with deskotheque.
	 */
	private void obtainResourceManagerPrx() {
		// obtaining resource manager proxy
		resourceManagerPrx = masterPrx.getResourceManagerProxy();
	}

	/**
	 * Retrieves an ice-proxy for deskotheque's Master for communication with
	 * deskotheque.
	 */
	private void obtainMasterPrx() {
		// get local host name
		String hostname = "";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getHostName();
			System.out.println("hostname=" + hostname);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// get virtual display
		String displayVar = System.getenv("DISPLAY");
		// the display variable is in the format: :0.0 or :1
		// - but we want a single integer instead
		if (displayVar == null) { // for windows machines the display-var
									// defaults to "0"
			displayVar = "0";
		} else if (displayVar.length() >= 2) {
			displayVar = displayVar.substring(1, 2);
		}

		// the server name is of the form ServerAppI-<hostname>-<xDisplay>
		String serverName = "ServerAppI-" + hostname + "-" + displayVar;
		// the server endpoint is of the form "tcp -h <hostname> -p 8011"
		// the port 8011 is defined by Deskotheque so we have to
		// hardcode that value here
		String serverEndPoint = "tcp -h " + hostname + " -p " + DKT_SERVER_PORT;

		System.out.println("trying to get server from '" + serverName + ":"
				+ serverEndPoint + "'");
		Ice.ObjectPrx proxy = communicator.stringToProxy(serverName + ":"
				+ serverEndPoint);
		serverPrx = ServerApplicationIPrxHelper
				.checkedCast(proxy);

		masterPrx = serverPrx.getMasterProxy();
	}

	/**
	 * Creates the ICE adapter for communication with deskotheque
	 * 
	 * @param communicator
	 */
	private void createAdapter(Communicator communicator) {

		int port = DEFAULT_LISTENING_PORT;

		while (adapter == null) {
			try {
				System.out.println("Using port " + port);
				adapter = communicator.createObjectAdapterWithEndpoints(
						"GroupwareClient", "default -p " + port);
			} catch (Ice.SocketException e) {
				// e.printStackTrace();
				System.out.println("Port " + port + " already in use");
			} catch (Exception e) {
				System.out.println("general exception, Port " + port + " already in use");
			}
			port++;
		}
	}

	/**
	 * Releases all obtained resources (especially the ICE based communication
	 * resources) and stops the network services.
	 */
	@Override
	public void stop() {

		unregisterEventListeners();
		if (resourceManagerPrx != null) {
			resourceManagerPrx.unregisterGroupwareClient(networkManager.getNetworkName());
		}

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
			case STATUS_CLIENT :
			case STATUS_SERVER :
			case STATUS_STARTED :
				networkManager.stopNetworkService();
				break;
			case STATUS_STOPPED :
				// nothing to do because no network services are running
				break;
			default :
				throw new IllegalStateException("unknown network status: "
						+ networkManager.getStatus());
		}
	}

	/**
	 * Sends information for connection lines as an event. The server within the
	 * connected groupware application is responsible for collecting the
	 * information and drawing the lines with help of deskotheque
	 */
	@Override
	public void sendConnectionLines(
			HashMap<EIDType, CanvasConnectionMap> canvasConnections) {

		for (Entry<EIDType, CanvasConnectionMap> ccm : canvasConnections
				.entrySet()) {
			final EIDType idType = ccm.getKey();
			for (Entry<Integer, SelectionPoint2DList> canvasPoints : ccm
					.getValue().entrySet()) {
				final Integer connectionID = canvasPoints.getKey();
				final SelectionPoint2DList pointList = canvasPoints.getValue();
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						SelectionPoint2DList displayPoints = canvasPointsToDisplay(pointList);
						sendConnectionLineEvent(idType, connectionID,
								displayPoints);
					}
				});
			}
		}
	}

	/**
	 * Transforms the given list of selection vertices from canvas coordinates
	 * to display coordinates
	 * 
	 * @param canvasPoints
	 *            list of canvas-vertices of connection lines
	 * @return list of selection vertices in display coordinates
	 */
	private SelectionPoint2DList canvasPointsToDisplay(
			SelectionPoint2DList canvasPoints) {
		SelectionPoint2DList displayPoints = new SelectionPoint2DList();
		for (SelectionPoint2D p : canvasPoints) {
			IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
			AGLEventListener view = vm.getGLEventListener(p.getViewID());
			Composite composite = view.getParentGLCanvas().getParentComposite();
			Point dp = composite.toDisplay(p.getPoint());
			SelectionPoint2D displayPoint = new SelectionPoint2D(networkManager
					.getNetworkName(), p.getViewID(), dp);
			displayPoints.add(displayPoint);
		}
		return displayPoints;
	}

/**
	 * Helper method to send {@link AddConnectionLineVerticesEvent
	 * @param idType
	 * @param connectionID
	 * @param points
	 */
	private void sendConnectionLineEvent(EIDType idType, Integer connectionID,
			SelectionPoint2DList points) {
		AddConnectionLineVerticesEvent event = new AddConnectionLineVerticesEvent();
		event.setIdType(idType);
		event.setConnectionID(connectionID);
		event.setPoints(points);
		event.setSender(null);
		eventPublisher.triggerEvent(event);
	}

	/**
	 * Tells deskotheque to draw connection lines for all known selection
	 * vertices for the {@link EIDType#EXPRESSION_INDEX}
	 */
	private void drawConnectionLines() {
		CanvasConnectionMap ccm = displayConnectionsByType
				.get(EIDType.EXPRESSION_INDEX);
		if (ccm != null) {
			for (Entry<Integer, SelectionPoint2DList> conDisplayPoints : ccm
					.entrySet()) {
				SelectionPoint2DList displayPoints = conDisplayPoints
						.getValue();
				final Integer connectionID = conDisplayPoints.getKey();
				final ConnectionLineVertex vertices[] = new ConnectionLineVertex[displayPoints
						.size()];
				int i = 0;
				for (SelectionPoint2D p : displayPoints) {
					ConnectionLineVertex vertex = new ConnectionLineVertex();
					vertex.x = p.getPoint().x;
					vertex.y = p.getPoint().y;
					vertex.clientID = p.getDeskoXID();
					System.out.println("vertex.clientID = " + vertex.clientID);
					vertices[i++] = vertex;
					System.out.print("(" + vertex.x + ", " + vertex.y + "), ");
				}
				System.out.println();
				Runnable drawer = new Runnable() {
					public void run() {
						masterPrx.drawConnectionLines(networkManager.getNetworkName(), vertices, connectionID);
					}
				};
				Thread drawThread = new Thread(drawer);
				drawThread.start();
			}
		}
	}

	/**
	 * Adds connection-line points for groupware related connection line
	 * drawing.
	 * 
	 * @param idType
	 *            type of selection the given connection line references to
	 * @param connectionID
	 *            unique id for connection lines
	 * @param newPoints
	 *            the list selection vertices in display coordinates to add
	 */
	public void addConnectionLineVertices(EIDType idType, int connectionID,
			SelectionPoint2DList newPoints) {

		CanvasConnectionMap dcm = displayConnectionsByType.get(idType);
		if (dcm == null) {
			dcm = new CanvasConnectionMap();
			displayConnectionsByType.put(idType, dcm);
		}

		SelectionPoint2DList pointList = dcm.get(connectionID);
		if (pointList == null) {
			pointList = new SelectionPoint2DList();
			dcm.put(connectionID, pointList);
		}

		if (newPoints != null) {
			pointList.addAll(newPoints);
		}
		redrawConnectionLines = true;
	}

	/**
	 * Clears all known connection lines for the given {@link EIDType}
	 * 
	 * @param idType
	 *            {@link EIDType} to remove the connection lines of
	 */
	public void clearConnections(EIDType idType) {
		CanvasConnectionMap ccm = displayConnectionsByType.get(idType);
		if (ccm != null) {
			ccm.clear();
		}
	}

	/**
	 * Clears all known connection lines
	 */
	public void clearConnections() {
		displayConnectionsByType.clear();
	}

	/**
	 * Execution method for
	 * {@link DisplayLoopExecution#executeMultiple(Runnable)}. During the
	 * execution groupware connection lines are propagated to other instances,
	 * event handling ist done and groupware connection lines are drawn with
	 * help of deskotheque.
	 */
	@Override
	public void run() {
		ConnectedElementRepresentationManager cerm = GeneralManager.get()
				.getViewGLCanvasManager()
				.getConnectedElementRepresentationManager();
		if (cerm.isNewCanvasVertices()) {
			cerm.setNewCanvasVertices(false);
			sendConnectionLines(cerm.getCanvasConnectionsByType());
		}
		processEvents();
		if (redrawConnectionLines
				&& networkManager.getStatus() == ENetworkStatus.STATUS_SERVER) {
			drawConnectionLines();
			redrawConnectionLines = false;
		}
	}

	/**
	 * Registers the event listeners to the central event system.
	 */
	private void registerEventListeners() {
		addConnectionLinePointsListener = new AddConnectionLineVerticesListener();
		addConnectionLinePointsListener.setHandler(this);
		eventPublisher.addListener(AddConnectionLineVerticesEvent.class,
				addConnectionLinePointsListener);

		clearConnectionsListener = new ClearConnectionsListener();
		clearConnectionsListener.setHandler(this);
		eventPublisher.addListener(ClearConnectionsEvent.class,
				clearConnectionsListener);

		clearTransformedConnectionsListener = new ClearTransformedConnectionsListener();
		clearTransformedConnectionsListener.setHandler(this);
		eventPublisher.addListener(ClearTransformedConnectionsEvent.class,
				clearTransformedConnectionsListener);
	}

	/**
	 * Unregisters the event listeners and releases the resources.
	 */
	private void unregisterEventListeners() {
		if (addConnectionLinePointsListener != null) {
			eventPublisher.removeListener(addConnectionLinePointsListener);
			addConnectionLinePointsListener = null;
		}
		if (clearConnectionsListener != null) {
			eventPublisher.removeListener(clearConnectionsListener);
			clearConnectionsListener = null;
		}
		if (clearTransformedConnectionsListener != null) {
			eventPublisher.removeListener(clearTransformedConnectionsListener);
			clearTransformedConnectionsListener = null;
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

	/**
	 * As deskotheque is able to draw visual connection lines this method always
	 * returns <code>true</code>
	 * 
	 * @return <code>true</code>
	 */
	@Override
	public boolean isGroupwareConnectionLinesEnabled() {
		return true;
	}

	/**
	 * Gets server address to use for caleydo clients to connect to.
	 * 
	 * @return caleydo application server ip address
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * Sets server address to use for caleydo clients to connect to.
	 * 
	 * @param serverAddress
	 *            caleydo application server ip address
	 */
	@Override
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	@Override
	public ApplicationInitData getInitData() {
		return initData;
	}

}
