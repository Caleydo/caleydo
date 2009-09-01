package org.caleydo.plex;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.event.view.selection.AddConnectionLinePointEvent;
import org.caleydo.core.manager.event.view.selection.ClearConnectionsEvent;
import org.caleydo.core.manager.execution.ADisplayLoopEventHandler;
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
 * @see <a href="http://studierstube.icg.tu-graz.ac.at/deskotheque/">Deskotheque</a>
 * @author Werner Puff
 */
public class DeskothequeManager 
	extends ADisplayLoopEventHandler
	implements IGroupwareManager {

	/** default port for incoming ice-connections */
	public static final int DEFAULT_LISTENING_PORT = 8050;
	
	public static final int DKT_SERVER_PORT = 8011;
	
	private IGeneralManager generalManager;
	
	private IEventPublisher eventPublisher;
	
	/** network manager to manage the network connections and network traffic */
	private NetworkManager networkManager;

	/** the address or domain-name of server in case of beeing a client */ 
	private String serverAddress;
	
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

	private boolean redrawConnectionLines = false;

	/**
	 * Stores {@link CanvasConnectionMap}s with connection lines in display coordinates and
	 * the related network-name of the display. 
	 */
	HashMap<EIDType, CanvasConnectionMap> displayConnectionsByType;

	AddConnectionLinePointsListener addConnectionLinePointsListener;
	
	ClearConnectionsListener clearConnectionsListener;
	
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
		registerEventListeners();
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
		registerEventListeners();
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
		
		unregisterEventListeners();
	}

	@Override
	public void sendConnectionLines(HashMap<EIDType, CanvasConnectionMap> canvasConnections) {

		for (Entry<EIDType, CanvasConnectionMap> ccm : canvasConnections.entrySet()) {
			final EIDType idType = ccm.getKey(); 
			for (Entry<Integer, SelectionPoint2DList> canvasPoints : ccm.getValue().entrySet()) {
				final Integer connectionID = canvasPoints.getKey();
				final SelectionPoint2DList pointList = canvasPoints.getValue();  
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						SelectionPoint2DList displayPoints = canvasPointsToDisplay(pointList);
						sendConnectionLineEvent(idType, connectionID, displayPoints);
					}
				});
			}
		}
	}
	
	private SelectionPoint2DList canvasPointsToDisplay(SelectionPoint2DList canvasPoints) {
		SelectionPoint2DList displayPoints = new SelectionPoint2DList();
		for (SelectionPoint2D p : canvasPoints) {
			IViewManager vm = GeneralManager.get().getViewGLCanvasManager();
			AGLEventListener view = vm.getGLEventListener(p.getViewID());
			Composite composite = view.getParentGLCanvas().getParentComposite();
			Point dp = composite.toDisplay(p.getPoint());
			SelectionPoint2D displayPoint = new SelectionPoint2D(networkManager.getNetworkName(), p.getViewID(), dp);
			displayPoints.add(displayPoint);
		}
		return displayPoints;
	}
	
	private void sendConnectionLineEvent(EIDType idType, Integer connectionID, SelectionPoint2DList points) {
		AddConnectionLinePointEvent event = new AddConnectionLinePointEvent();
		event.setIdType(idType);
		event.setConnectionID(connectionID);
		event.setPoints(points);
		event.setSender(null);
		eventPublisher.triggerEvent(event);
	}

	private void drawConnectionLines() {
//		for (CanvasConnectionMap ccm : displayConnectionsByType.values()) {
		CanvasConnectionMap ccm = displayConnectionsByType.get(EIDType.EXPRESSION_INDEX);
			for (Entry<Integer, SelectionPoint2DList> conDisplayPoints : ccm.entrySet()) {
				SelectionPoint2DList displayPoints = conDisplayPoints.getValue();
				final Integer connectionID = conDisplayPoints.getKey();
				final ConnectionLineVertex vertices[] = new ConnectionLineVertex[displayPoints.size()];
				int i = 0;
				System.out.print("dm.drawConnectionLines(): call desko ");
				for (SelectionPoint2D p : displayPoints) {
					ConnectionLineVertex vertex = new ConnectionLineVertex();
					vertex.x = p.getPoint().x;
					vertex.y = p.getPoint().y;
					vertices[i++] = vertex;
					System.out.print("("+vertex.x+", "+vertex.y+"), ");
				}
				System.out.println();
				Runnable drawer = new Runnable() {
					public void run() {
						masterPrx.drawConnectionLine(vertices, connectionID);
					}
				};
				Thread drawThread = new Thread(drawer);
				drawThread.start();
				System.out.println("new draw thread");
			}
//		}
		
	}

	/**
	 * Adds connection-line points for groupware related connection line drawing. 
	 * @param idType
	 * @param connectionID
	 * @param points
	 */
	public void addConnectionLinePoints(EIDType idType, int connectionID, SelectionPoint2DList newPoints) {
		System.out.println("desko: addPoints() called, newPoints=" + newPoints);

		CanvasConnectionMap dcm = displayConnectionsByType.get(idType);
		if (dcm == null) {
			System.out.println("desko: addPoints() new dcm");
			dcm = new CanvasConnectionMap();
			displayConnectionsByType.put(idType, dcm);
		}
		
		SelectionPoint2DList pointList = dcm.get(connectionID);
		if (pointList == null) {
			System.out.println("desko: addPoints() new pointList");
			pointList = new SelectionPoint2DList();
			dcm.put(connectionID, pointList);
		}
		System.out.println("desko: addPoints() pointList="+pointList);

		if (newPoints == null) {
			System.out.println("desko: newPoints=null");
		} else {
			pointList.addAll(newPoints);
		}
		redrawConnectionLines = true;
	}
	
	public void clearConnections(EIDType idType) {
		System.out.println("desko: clearConnections(), idType="+idType);
		CanvasConnectionMap ccm = displayConnectionsByType.get(idType);
		if (ccm != null) {
			ccm.clear();
		}
	}
	
	@Override
	public void run() {
		ConnectedElementRepresentationManager cerm = GeneralManager.get().getViewGLCanvasManager().getConnectedElementRepresentationManager();
		if (cerm.isNewCanvasVertices()) {
			cerm.setNewCanvasVertices(false);
			sendConnectionLines(cerm.getCanvasConnectionsByType());
		}
		processEvents();
		if (redrawConnectionLines && networkManager.getStatus() == ENetworkStatus.STATUS_SERVER) {
			drawConnectionLines();
			redrawConnectionLines = false;
		}
	}
	
	private void registerEventListeners() {
		addConnectionLinePointsListener = new AddConnectionLinePointsListener();
		addConnectionLinePointsListener.setHandler(this);
		eventPublisher.addListener(AddConnectionLinePointEvent.class, addConnectionLinePointsListener);

		clearConnectionsListener = new ClearConnectionsListener();
		clearConnectionsListener.setHandler(this);
		eventPublisher.addListener(ClearConnectionsEvent.class, clearConnectionsListener);
	}
	
	private void unregisterEventListeners() {
		if (addConnectionLinePointsListener != null) {
			eventPublisher.removeListener(addConnectionLinePointsListener);
			addConnectionLinePointsListener = null;
		}
		if (clearConnectionsListener != null) {
			eventPublisher.removeListener(clearConnectionsListener);
			clearConnectionsListener = null;
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

	@Override
	public boolean isGroupwareConnectionLinesEnabled() {
		return true;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public ApplicationInitData getInitData() {
		return initData;
	}

}
