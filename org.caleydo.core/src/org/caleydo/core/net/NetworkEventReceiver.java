package org.caleydo.core.net;

import java.io.InputStream;
import java.io.StringReader;
import java.net.SocketException;

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

	/** input stream of the connection to read events from */
	private InputStream inputStream;
	
	/** name of the connected client */
	private String name;

	@Override
	public void run() {
		boolean stop = false;

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
					System.out.println("incoming messageX: " + message);
					handleNetworkEvent(message);
					delimiterIndex = buffer.indexOf("\r\n\r\n");
				}
			} catch (SocketException ex) {
				ex.printStackTrace();
				// TODO shut down this connection
				stop = true;
			} catch (Exception ex) {
				ex.printStackTrace();
				// TODO exception handling
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
		JAXBContext jc = GeneralManager.get().getNetworkManager().getJaxbContext();
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

}
