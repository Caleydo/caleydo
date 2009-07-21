package org.caleydo.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.swt.collab.TestSerializationEvent;

/**
 * <p>
 * Responsible to transmit all incoming events to the related client on the network.
 * </p>
 * <p>
 * Instances of this class should be run as thread. This threads are responsible for sending the queued events
 * to the related client. As all events are sent to the related client, listeners are not supported. Any calls
 * to listener specific methods will result in a {@link UnsupportedOperationException}
 * </p>
 * 
 * @author Werner Puff
 */
public class NetworkEventPublisher
	implements IEventPublisher, Runnable {

	/** queue for the events to sent */
	private BlockingQueue<AEvent> eventQueue;

	/** {@link NetworkManager} this instance is bound to */
	private NetworkManager networkManager = null;

	/** {@link OutputStream} to write the serialized events to */ 
	private OutputStream outputStream;

	/** name of the connected client */
	private String name;
	
	/**
	 * Default Constructor
	 */
	public NetworkEventPublisher() {
		eventQueue = new LinkedBlockingQueue<AEvent>();
	}

	/**
	 * Not supported. This {@link IEventPublisher}-implementation does not support listeners.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void addListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		throw new UnsupportedOperationException("adding listeners to NetEventPublishers is not possible.");
	}

	/**
	 * Not supported. This {@link IEventPublisher}-implementation does not support listeners.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void removeListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		throw new UnsupportedOperationException("removing listeners to NetEventPublishers is not possible.");
	}

	/**
	 * Not supported. This {@link IEventPublisher}-implementation does not support listeners.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void removeListener(AEventListener<?> listener) {
		throw new UnsupportedOperationException("removing listeners to NetEventPublishers is not possible.");
	}

	/**
	 * Queues the event for sending to the related client on the network.
	 * 
	 * @param listener
	 *            the listener used to listen for the event. Usually the listener for NetworkEventPublisher
	 *            instances are of type {@link EventFilterBridge}.
	 * @param event
	 *            event to put into the queue for sending
	 */
	@Override
	public void triggerEvent(AEvent event) {
		System.out.println("NetworkEventPublisher.triggerEvent(): event="+event);
		eventQueue.add(event);
	}

	/**
	 * Sends the queued events to the related client.
	 */
	@Override
	public void run() {
		Marshaller marshaller = null;
		try {
			JAXBContext jaxbContext = networkManager.getJaxbContext();
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		} catch (JAXBException ex) {
			throw new RuntimeException("could not create xml marshaller", ex);
		}

		boolean stop = false;
		while (!stop) {
			try {
				IEventPublisher eventPublisher = GeneralManager.get().getEventPublisher();
				AEvent event = eventQueue.take();
				System.out.println("NetworkEventPublisher.run(): received event=" + event);

				StringWriter xmlOutputWriter = new StringWriter();
				try {
					marshaller.marshal(event, xmlOutputWriter);
					String xmlOutput = xmlOutputWriter.getBuffer().toString();
					System.out.println("NetworkEventPublisher.run(): transmitting xml event=" + xmlOutput);
					try {
						outputStream.write(xmlOutput.getBytes());
						outputStream.write("\r\n\r\n".getBytes());
						outputStream.flush();
					} catch (IOException ex) {
						ex.printStackTrace();
						// TODO exception handling
					}

					TestSerializationEvent testSerializationEvent = new TestSerializationEvent();
					testSerializationEvent.setSerializedText(xmlOutput);
					eventPublisher.triggerEvent(testSerializationEvent);

				} catch (JAXBException ex) {
					ex.printStackTrace();
				}
			}
			catch (InterruptedException ex) {
				stop = true;
			}
		}
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
