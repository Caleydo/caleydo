package org.caleydo.core.net;

import java.io.InputStream;
import java.io.StringReader;
import java.net.SocketException;
import java.nio.channels.ClosedByInterruptException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.eclipse.core.runtime.ILog;

/**
 * <p>
 * Responsible to receive incoming events from the network.
 * </p>
 * <p>
 * Instances of this class should be run as thread. This threads are responsible for receiving events from the
 * network, deserializing them and sending them to the connected {@link IEventPublisher}.
 * </p>
 * 
 * @author Werner Puff
 */
public class NetworkEventReceiver
	extends EventPublisher
	implements Runnable {

	ILog log = GeneralManager.get().getLogger(); 
	
	/** Related {@link NetworkManager} for this {@link NetworkEventReceiver} */
	private NetworkManager networkManager;

	/** Related {@link Connection} for this {@link NetworkEventReceiver} */
	private Connection connection;
	
	/** input stream of the connection to read events from */
	private InputStream inputStream;
	
	/** name of the connected client */
	private String name;

	/** flag for stopping the execution of receiving events from the connected caleydo application */
	private boolean stop = false;
	
	@Override
	public void run() {
		StringBuffer buffer = new StringBuffer();;
		while (!stop) {
			try {
				byte[] bytes = new byte[10000];
				int charsRead = inputStream.read(bytes);
				String chunk = new String(bytes, 0, charsRead);
				buffer.append(chunk);
				int delimiterIndex = buffer.indexOf("\r\n\r\n"); 
				while(delimiterIndex > -1) {
					String message = buffer.substring(0, delimiterIndex);
					buffer.delete(0, delimiterIndex + 4);
					System.out.println("incoming message: " + message);
					handleNetworkEvent(message);
					delimiterIndex = buffer.indexOf("\r\n\r\n");
				}
			} catch (SocketException ex) {
				// ex.printStackTrace();
				networkManager.disposeConnection(connection);
				stop();
			} catch (ClosedByInterruptException ex) {
				ex.printStackTrace();
				// nothing to do, probably the thread needs to stop its execution
			} catch (StringIndexOutOfBoundsException ex) {
				networkManager.disposeConnection(connection);
				stop();
			} catch (Exception ex) {
				ex.printStackTrace();
				// continue execution when unexpected exceptions occure
			}
		}
	}

	/**
	 * Handles an incoming event in its serialized form. Usually this event was received
	 * from the network and has to be dispatched into the local event system.
	 * 
	 * @param eventString serialized event to handle
	 */
	public void handleNetworkEvent(String eventString) {
		// log.log(new Status(Status.INFO, GeneralManager.PLUGIN_ID, "NetworkEventReceiver.handleNetworkEvent(): received event=" + eventString));
		StringReader xmlInputReader = new StringReader(eventString);
		JAXBContext jc = GeneralManager.get().getSerializationManager().getEventContext();
		try {
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			AEvent event = (AEvent) unmarshaller.unmarshal(xmlInputReader);
			event.setSender(this);
			triggerEvent(event);
		}
		catch (JAXBException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Sets the signal to stop the execution to true which causes a running thread to stop.
	 */
	public void stop() {
		stop = true;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
