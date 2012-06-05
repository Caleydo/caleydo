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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.serialize.SerializationData;

/**
 * Utility class for reading and writing handshake messages between client and server applications. The
 * messages are serialized and de-serialized using JAXB.
 * 
 * @author Werner Puff
 */
public class NetworkUtils {

	/** {@link JAXBContext} to marshal/unmarshal the handshake messages */
	JAXBContext handshakeJAXBContext;

	/**
	 * Creates and initializes a new {@link NetworkUtils} instance.
	 */
	public NetworkUtils() {
		try {
			handshakeJAXBContext =
				JAXBContext
					.newInstance(ClientHandshake.class, ServerHandshake.class, SerializationData.class);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext for client/server handshake messages");
		}
	}

	/**
	 * Reads a handshake message (either {@link ClientHandshake} or {@link ServerHandshake}) from the given
	 * {@link InputStream}
	 * 
	 * @param inputStream
	 *            {@link InputStream} to read the message from
	 * @return message as xml-{@link String}
	 * @throws IOException
	 *             if any read error occurs during the read operation
	 */
	public Object readHandshake(InputStream inputStream) throws IOException, JAXBException {
		int delimiterIndex = -1;
		StringBuffer buffer = new StringBuffer();

		while (delimiterIndex == -1) {
			byte[] bytes = new byte[10000];
			int charsRead = inputStream.read(bytes);
			String chunk = new String(bytes, 0, charsRead);
			buffer.append(chunk);
			delimiterIndex = buffer.indexOf("\r\n\r\n");
		}

		Unmarshaller unmarshaller = handshakeJAXBContext.createUnmarshaller();
		StringReader reader = new StringReader(buffer.toString());

		// System.out.println("incoming serverHandshake message:\n" + buffer);
		Object handshake = unmarshaller.unmarshal(reader);

		return handshake;
	}

	/**
	 * Writes a handshake message (either {@link ClientHandshake} or {@link ServerHandshake}) to the given
	 * {@link OutputStream}
	 * 
	 * @param handshake
	 *            message to write
	 * @param outputStream
	 *            {@link OutputStream} to write the message to
	 */
	public void writeHandshake(Object handshake, OutputStream outputStream) {
		try {
			Marshaller marshaller = handshakeJAXBContext.createMarshaller();
			marshaller.marshal(handshake, outputStream);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			byte[] out = "\r\n\r\n".getBytes();
			outputStream.write(out);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("error while marshalling handshake message: " + handshake, ex);
		}
		catch (IOException ex) {
			throw new RuntimeException("error while writing handshake message: " + handshake, ex);
		}
	}

	/**
	 * Getter for {@link NetworkUtils#handshakeJAXBContext}
	 * 
	 * @return {@link NetworkUtils#handshakeJAXBContext}
	 */
	public JAXBContext getHandshakeJAXBContext() {
		return handshakeJAXBContext;
	}

	/**
	 * Setter for {@link NetworkUtils#handshakeJAXBContext}
	 * 
	 * @param {@link NetworkUtils#handshakeJAXBContext}
	 */
	public void setHandshakeJAXBContext(JAXBContext handshakeJAXBContext) {
		this.handshakeJAXBContext = handshakeJAXBContext;
	}
}
