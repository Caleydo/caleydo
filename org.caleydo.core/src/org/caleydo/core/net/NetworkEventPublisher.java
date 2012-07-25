/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;

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
	extends EventPublisher
	implements Runnable {

	/** queue for the events to sent */
	private BlockingQueue<AEvent> eventQueue;

	/** {@link NetworkManager} this instance is bound to */
	private NetworkManager networkManager = null;

	/** Related {@link Connection} for this {@link NetworkEventReceiver} */
	private Connection connection;

	/** {@link OutputStream} to write the serialized events to */
	private OutputStream outputStream;

	/** name of the connected client */
	private String name;

	/** flag for stopping the execution of sending events to the connected caleydo application */
	private boolean stop = false;

	/**
	 * Default Constructor
	 */
	public NetworkEventPublisher() {
		eventQueue = new LinkedBlockingQueue<AEvent>();
	}

	/**
	 * Not supported. This {@link EventPublisher}-implementation does not support listeners.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void addListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		throw new UnsupportedOperationException("adding listeners to NetEventPublishers is not possible.");
	}

	/**
	 * Not supported. This {@link EventPublisher}-implementation does not support listeners.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void removeListener(Class<? extends AEvent> eventClass, AEventListener<?> listener) {
		throw new UnsupportedOperationException("removing listeners to NetEventPublishers is not possible.");
	}

	/**
	 * Not supported. This {@link EventPublisher}-implementation does not support listeners.
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
		System.out.println("NetworkEventPublisher.triggerEvent(): event=" + event);
		eventQueue.add(event);
	}

	/**
	 * Sends the queued events to the connected caleydo application.
	 */
	@Override
	public void run() {
		Marshaller marshaller = null;
		try {
			JAXBContext jaxbContext = GeneralManager.get().getSerializationManager().getEventContext();
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("could not create xml marshaller", ex);
		}

		while (!stop) {
			try {
				AEvent event = eventQueue.take();
				// System.out.println("NetworkEventPublisher.run(): received event=" + event);

				StringWriter xmlOutputWriter = new StringWriter();
				try {
					marshaller.marshal(event, xmlOutputWriter);
					String xmlOutput = xmlOutputWriter.getBuffer().toString();
					// System.out.println("NetworkEventPublisher.run(): transmitting xml event=" + xmlOutput);
					try {
						outputStream.write(xmlOutput.getBytes());
						outputStream.write("\r\n\r\n".getBytes());
						outputStream.flush();
					}
					catch (IOException ex) {
						ex.printStackTrace();
						networkManager.disposeConnection(connection);
						stop();
					}

					// TestSerializationEvent testSerializationEvent = new TestSerializationEvent();
					// testSerializationEvent.setSerializedText(xmlOutput);
					// eventPublisher.triggerEvent(testSerializationEvent);

				}
				catch (JAXBException ex) {
					ex.printStackTrace();
				}
			}
			catch (InterruptedException ex) {
				// ex.printStackTrace();
				// nothing to do, probably the thread needs to stop its execution
			}
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

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
