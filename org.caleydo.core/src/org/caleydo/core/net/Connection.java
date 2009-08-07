package org.caleydo.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.selection.VirtualArray;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.usecase.AUseCase;
import org.caleydo.core.serialize.ApplicationInitData;
import org.caleydo.core.view.opengl.canvas.storagebased.EVAType;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Status;

/**
 * Holds all related data and classes for the connection to another caleydo application. TODO split into
 * incoming and outgoing connection with common super-class
 * 
 * @author Werner Puff
 */
public class Connection {

	/** utility object for logging */
	ILog log = GeneralManager.get().getLogger(); 
	
	/** {@link NetworkManager} for managing this connection. */
	private NetworkManager networkManager;
	
	/**
	 * The {@link EventFilterBridge} to bridge events from the central outgoing network
	 * {@link EventPublisher} to the connected caleydo application.
	 */
	private EventFilterBridge outgoingBridge;

	/** The {@link NetworkEventPublisher} to transmit outgoing events to the connected client */
	private NetworkEventPublisher outgoingPublisher;

	/** {@link Thread} of the {@link NetworkEventPublisher} to send events */
	private Thread senderThread;
	
	/**
	 * The {@link EventFilterBridge} to bridge events from the {@link NetworkEventReceiver} to the central
	 * incoming network {@link EventPublisher} to the connected caleydo application.
	 */
	private EventFilterBridge incomingBridge;

	/** The {@link NetworkEventPublisher} to transmit outgoing events to the connected client */
	private NetworkEventReceiver incomingPublisher;

	/** {@link Thread} of the {@link NetworkEventReceiver} to receive events */
	private Thread receiverThread;

	/** Socket to the connected Caleydo application */
	private Socket socket;
	
	/** {@link OutputStream} to the connected Caleydo application*/
	private OutputStream outputStream;

	/** {@link InputStream} from the connected Caleydo application */
	private InputStream inputStream;

	/** network name of the remote application */
	private String remoteNetworkName;
	
	/**
	 * Creates a stand-alone Connection instance ready to be connected to a local event system and the client
	 * specified by its {@link InetAddress}. The connection between caleydo's event system and this connection
	 * has to be done separately (usually done within the {@link NetworkManager}.
	 * 
	 * @param networkManager
	 *            {@link NetworkManager} responsible to manage this connection
	 */
	public Connection(NetworkManager networkManager) {
		init(networkManager);
	}

	/**
	 * Initializes this {@link Connection} by creating the event related framework.
	 * 
	 * @param networkManager
	 *            {@link NetworkManager} responsible to manage this connection
	 */
	private void init(NetworkManager networkManager) {
		this.networkManager = networkManager;
		
		incomingPublisher = new NetworkEventReceiver();
		incomingPublisher.setName("networkReceiver");
		incomingPublisher.setNetworkManager(networkManager);
		incomingPublisher.setConnection(this);
		
		incomingBridge = new EventFilterBridge();
		incomingBridge.setName("incomingClientBridge");
		incomingBridge.setBridgeRemoteEvents(true);
		incomingBridge.setTargetEventPublisher(networkManager.getGlobalIncomingPublisher());

		outgoingPublisher = new NetworkEventPublisher();
		outgoingPublisher.setName("networkPublisher");
		outgoingPublisher.setNetworkManager(networkManager);
		outgoingPublisher.setConnection(this);

		outgoingBridge = new EventFilterBridge();
		outgoingBridge.setName("outgoingClientBridge");
		outgoingBridge.setBridgeLocalEvents(true);
		outgoingBridge.setBridgeRemoteEvents(true);
		outgoingBridge.setTargetEventPublisher(outgoingPublisher);
		outgoingBridge.addBlockedSender(incomingPublisher);
	}

	/**
	 * Creates an {@link Socket}-connection to a server running on the given address,  
	 * performs handshakes and initializes this connection.  
	 * @param clientSocket {@link Socket} to connect through
	 * @throws ConnectException if a error during handshaking occurs
	 */
	public ApplicationInitData connect(InetAddress address, int port) throws ConnectException {
		log.log(new Status(Status.INFO, GeneralManager.PLUGIN_ID, "connect(): address=" + address));
		ApplicationInitData initData;
		try { 
			socket = new Socket(address, port);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			
			NetworkUtils networkUtils = networkManager.getNetworkUtils();

			ClientHandshake clientHandshake = new ClientHandshake();
			clientHandshake.setClientNetworkName(networkManager.getNetworkName());
			clientHandshake.setVersion(NetworkManager.VERSION);
			clientHandshake.setRequestType(ClientHandshake.REQUEST_CONNECT);
			networkUtils.writeHandshake(clientHandshake, outputStream);
			
			ServerHandshake serverHandshake = (ServerHandshake) networkUtils.readHandshake(inputStream);
			log.log(new Status(Status.INFO, GeneralManager.PLUGIN_ID, "connect(): serverHandshake=" + serverHandshake));

			if (serverHandshake.getError() != null) {
				// TODO connection error handling
				throw new Exception("connection error: " + serverHandshake.getError());
			}
			
			networkManager.setNetworkName(serverHandshake.getClientNetworkName());
			clientHandshake = new ClientHandshake();
			clientHandshake.setClientNetworkName(networkManager.getNetworkName());
			clientHandshake.setVersion(NetworkManager.VERSION);
			clientHandshake.setRequestType(ClientHandshake.REQUEST_CONNECTION_ESTABLISHED);
			networkUtils.writeHandshake(clientHandshake, outputStream);

			initData = (ApplicationInitData) networkUtils.readHandshake(inputStream);
			
			start(serverHandshake.getServerNetworkName());
		} catch (Exception e) {
			throw new ConnectException(e);
		}

		return initData;
	}

	/**
	 * Performs handshakes with the client which tries to connect and initializes this connection.  
	 * @param clientSocket {@link Socket} to connect through
	 * @throws ConnectException if a error during handshaking occurs
	 */
	public void connect(Socket clientSocket) throws ConnectException {
		socket = clientSocket;
		try {
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
		} catch (Exception ex) {
			throw new ConnectException("error while getting input/output streams", ex);
		}
		
		try {
			socket.setSoTimeout(networkManager.getConnectingTimeout());
		} catch (SocketException ex) {
			throw new ConnectException("error while trying to set read-timeout", ex);
		}

		try {
			ClientHandshake clientHandshake = readClientHandshake(inputStream);
			System.out.println("received clientHandshake=" + clientHandshake);
			ServerHandshake serverHandshake = validate(clientHandshake);
			sendServerHandshake(serverHandshake, outputStream);
			clientHandshake = readClientHandshake(inputStream);
			if (!clientHandshake.getRequestType().equals(ClientHandshake.REQUEST_CONNECTION_ESTABLISHED)) {
				throw new ConnectException("Client refused connection.");
			}
			sendServerInitializationData(outputStream);
			socket.setSoTimeout(0);
			start(clientHandshake.getClientNetworkName());
		} catch (SocketTimeoutException stEx) {
			byte[] out = "Timeout.\r\n".getBytes();
			try {
				outputStream.write(out);
				outputStream.flush();
			}
			catch (IOException ex) {
				// client doesnt seem to be connected anymore, nothing to do
			}
			throw new ConnectException("Client timed out during handshake.");
		} catch (IOException ex) {
			throw new ConnectException("Communication error during handshake.", ex);
		} catch (JAXBException ex) {
			throw new ConnectException("XML serialization error", ex);
		}
	}

	/**
	 * Reads a incoming handshake message from the client. 
	 * @param is Socket based stream to read the message from  
	 * @return message send from the client
	 * @throws SocketTimeoutException If the client does not respond in time
	 * @throws IOException If a general error during the read operation occurs
	 * @throws JAXBException If a XML-serialization error occurs
	 */
	private ClientHandshake readClientHandshake(InputStream is) throws SocketTimeoutException, IOException,
		JAXBException {

		NetworkUtils utils = networkManager.getNetworkUtils();
		ClientHandshake clientHandshake = (ClientHandshake) utils.readHandshake(is);
		
		return clientHandshake;
	}

	/**
	 * Sends a {@link ServerHandshake} message to a client
	 * @param serverHandshake message to send
	 * @param outputStream Socket based stream to write the message to
	 * @throws IOException If a general error during the read operation occurs
	 * @throws JAXBException If a XML-serialization error occurs
	 */
	private void sendServerHandshake(ServerHandshake serverHandshake, OutputStream outputStream)
	throws JAXBException, IOException {
		NetworkUtils utils = networkManager.getNetworkUtils();
		utils.writeHandshake(serverHandshake, outputStream);
	}

	/**
	 * Collects the data to initialize a new connected client and sends it to the client
	 * @param outputStream {@link OutputStream} of the socket for sending to client 
	 */
	private void sendServerInitializationData(OutputStream outputStream) {
		ApplicationInitData initData = new ApplicationInitData();

		AUseCase useCase = (AUseCase) GeneralManager.get().getUseCase();
		
		initData.setUseCase((AUseCase) GeneralManager.get().getUseCase());
		initData.setSetFileContent(SetUtils.loadSetFile(useCase.getLoadDataParameters()));
		
		HashMap<EVAType, VirtualArray> virtualArrayMap = new HashMap<EVAType, VirtualArray>(); 
		virtualArrayMap.put(EVAType.CONTENT, (VirtualArray) useCase.getVA(EVAType.CONTENT));
		virtualArrayMap.put(EVAType.CONTENT_CONTEXT, (VirtualArray) useCase.getVA(EVAType.CONTENT_CONTEXT));
		virtualArrayMap.put(EVAType.CONTENT_EMBEDDED_HM, (VirtualArray) useCase.getVA(EVAType.CONTENT_EMBEDDED_HM));
		virtualArrayMap.put(EVAType.STORAGE, (VirtualArray) useCase.getVA(EVAType.STORAGE));
		initData.setVirtualArrayMap(virtualArrayMap);

		NetworkUtils utils = networkManager.getNetworkUtils();
		utils.writeHandshake(initData, outputStream);
	}

	/**
	 * Checks if the handshake message received from the client is valid and
	 * creates a {@link ServerHandshake} message for sending to the client.
	 * @param clientHandshake received handshake message from the client
	 * @return new {@link ServerHandshake} message to send to the client
	 */
	private ServerHandshake validate(ClientHandshake clientHandshake) {
		ServerHandshake serverHandshake = new ServerHandshake();

		String newClientName = clientHandshake.getClientNetworkName() + "-" + networkManager.increaseConnectionCounter();
		serverHandshake.setClientNetworkName(newClientName);
		serverHandshake.setServerNetworkName(networkManager.getNetworkName());
		serverHandshake.setVersion(NetworkManager.VERSION);
		
		if (!NetworkManager.VERSION.equals(clientHandshake.getVersion())) {
			serverHandshake.setError(ServerHandshake.ERROR_VERSION_CONFLICT);
		}

		return serverHandshake;
	}

	/**
	 * initializes the names of this {@link Connection} and its {@link EventFilterBridge}s
	 * @param remoteNetworkName the network name of the remote application
	 */
	private void start(String remoteNetworkName) {
		this.remoteNetworkName = remoteNetworkName;

		outgoingPublisher.setName(remoteNetworkName);
		outgoingBridge.setName(remoteNetworkName);
		incomingPublisher.setName(remoteNetworkName + "-incoming");
		incomingBridge.setName(remoteNetworkName);

		outgoingPublisher.setOutputStream(outputStream);
		senderThread = new Thread(outgoingPublisher, "outgoing-" + remoteNetworkName);

		incomingPublisher.setInputStream(inputStream);
		receiverThread = new Thread(incomingPublisher, "incoming-" + remoteNetworkName);

		senderThread.start();
		receiverThread.start();
	}

	/**
	 * Disposes this connection by stopping the sender and receiver thread
	 * and closing the socket-connections.
	 */
	public void dispose() {
		if (senderThread != null && senderThread.isAlive()) {
			outgoingPublisher.stop();
			senderThread.interrupt();
			outgoingPublisher.setConnection(null);
			outgoingPublisher.setOutputStream(null);
		}

		if (receiverThread != null && receiverThread.isAlive()) {
			incomingPublisher.stop();
			receiverThread.interrupt();
			incomingPublisher.setConnection(null);
			incomingPublisher.setInputStream(null);
		}

		if (socket != null || socket.isConnected()) {
			try {
				socket.close();
			} catch (IOException ex) {
				// nothing to do here, we are closing the socket anyways
			}
		}
		
		socket = null;
		inputStream = null;
		outputStream = null;
		
		outgoingBridge = null;
		outgoingPublisher = null;
		senderThread = null;

		incomingBridge = null;
		incomingPublisher = null;
		receiverThread = null;
		
		networkManager = null;
		remoteNetworkName = null;
	}
	
	/**
	 * Getter for {@link Connection#outgoingBridge}
	 * @return {@link Connection#outgoingBridge}
	 */
	public EventFilterBridge getOutgoingBridge() {
		return outgoingBridge;
	}

	/**
	 * Setter for {@link Connection#outgoingBridge}
	 * @param {@link Connection#outgoingBridge}
	 */
	public void setOutgoingBridge(EventFilterBridge outgoingBridge) {
		this.outgoingBridge = outgoingBridge;
	}

	/**
	 * Getter for {@link Connection#outgoingPublisher}
	 * @return {@link Connection#outgoingPublisher}
	 */
	public NetworkEventPublisher getOutgoingPublisher() {
		return outgoingPublisher;
	}

	/**
	 * Setter for {@link Connection#outgoingPublisher}
	 * @param {@link Connection#outgoingPublisher}
	 */
	public void setOutgoingPublisher(NetworkEventPublisher publisher) {
		this.outgoingPublisher = publisher;
	}

	/**
	 * Getter for {@link Connection#incomingPublisher}
	 * @return {@link Connection#incomingPublisher}
	 */
	public NetworkEventReceiver getIncomingPublisher() {
		return incomingPublisher;
	}

	/**
	 * Setter for {@link Connection#incomingPublisher}
	 * @param {@link Connection#incomingPublisher}
	 */
	public void setIncomingPublisher(NetworkEventReceiver incomingPublisher) {
		this.incomingPublisher = incomingPublisher;
	}

	/**
	 * Getter for {@link Connection#incomingBridge}
	 * @return {@link Connection#incomingBridge}
	 */
	public EventFilterBridge getIncomingBridge() {
		return incomingBridge;
	}

	/**
	 * Setter for {@link Connection#outgoingBridge}
	 * @param {@link Connection#outgoingBridge}
	 */
	public void setIncomingBridge(EventFilterBridge bridge) {
		this.outgoingBridge = bridge;
	}

	/**
	 * Getter for {@link Connection#remoteNetworkName}
	 * @return {@link Connection#remoteNetworkName}
	 */
	public String getRemoteNetworkName() {
		return remoteNetworkName;
	}

	/**
	 * Setter for {@link Connection#remoteNetworkName}
	 * @param {@link Connection#remoteNetworkName}
	 */
	public void setRemoteNetworkName(String remoteNetworkName) {
		this.remoteNetworkName = remoteNetworkName;
	}

}
