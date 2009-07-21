package org.caleydo.core.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class NetworkUtils {

	/** {@link JAXBContext} to marshall/unmarshal the handshake messages */
	JAXBContext handshakeJAXBContext;
	
	public NetworkUtils() {
		try {
			handshakeJAXBContext = JAXBContext.newInstance(ClientHandshake.class, ServerHandshake.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext for client/server handshake messages");
		}
	}

	/**
	 * Reads a handshake message (either {@link ClientHandshake} or {@link ServerHandshake}) from the given {@link InputStream}
	 * @param inputStream {@link InputStream} to read the message from 
	 * @return message as xml-{@link String} 
	 * @throws IOException if any read error occurs during the read operation
	 */
	public Object readHandshake(InputStream inputStream) throws IOException, JAXBException {
		boolean stop = false;
		int totalRead = 0;
		byte[] buffer = new byte[1000];
		while (!stop) {
			int lastRead = inputStream.read(buffer, totalRead, 1000 - totalRead);
			totalRead += lastRead;
			if (totalRead > 4) {
				String checkEnd = new String(buffer, totalRead - 4, 4);
				if (checkEnd.equals("\r\n\r\n")) {
					stop = true;
				}
			}
		}

		Unmarshaller unmarshaller = handshakeJAXBContext.createUnmarshaller();
		Object handshake = unmarshaller.unmarshal(new ByteArrayInputStream(buffer, 0, totalRead));
		
		return handshake;
	}

	/**
	 * Writes a handshake message (either {@link ClientHandshake} or {@link ServerHandshake}) to the given {@link OutputStream} 
	 * @param handshake message to write
	 * @param outputStream {@link OutputStream} to write the message to
	 */
	public void writeHandshake(Object handshake, OutputStream outputStream) {
		try {
			Marshaller marshaller = handshakeJAXBContext.createMarshaller();
			marshaller.marshal(handshake, outputStream);
			byte[] out = "\r\n\r\n".getBytes();
			outputStream.write(out);
		} catch (JAXBException ex) {
			throw new RuntimeException("error while marshalling handshake message: " + handshake, ex);
		} catch (IOException ex) {
			throw new RuntimeException("error while writing handshake message: " + handshake, ex);
		}
	}
	
	public JAXBContext getHandshakeJAXBContext() {
		return handshakeJAXBContext;
	}

	public void setHandshakeJAXBContext(JAXBContext handshakeJAXBContext) {
		this.handshakeJAXBContext = handshakeJAXBContext;
	}
}
