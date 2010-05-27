/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.model.converters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An <code>EntityResolver</code> that can be used to provide the DTDs for XML
 * parsing (e.g. X3D parsing) locally instead of letting the
 * <code>DocumentBuilder</code> download them from their respective servers.<br />
 * An <code>EntityResolver</code> can be passed to a
 * <code>DocumentBuilder</code> via the method
 * <code>setEntityResolver(EntityResolver)</code>.
 * 
 * @version 2008-01-27
 * @author Michael Sattler
 */
public class DTDResolver implements EntityResolver {

	private Map<String, byte[]> dtdBytes;

	/**
	 * Creates a resolver using the given Map to resolve the DTD files used for
	 * X3D parsing. <br />
	 * The keys in the map may either be the full System Identifiers of the DTDs
	 * (e.g. "http://www.web3d.org/specifications/x3d-3.0.dtd") or just the file
	 * names (e.g. "x3d-3.0.dtd").<br />
	 * The values may be either byte arrays or InputStreams. If byte arrays are
	 * used, they are used by-reference. If InputStreams are passed, they will
	 * be read and the contents will be stored.
	 * 
	 * @param dtdInput
	 *            The mapping the names of DTD files to their InputStreams
	 */
	public DTDResolver(Map<String, ?> dtdInput) {
		this.dtdBytes = new HashMap<String, byte[]>();
		for (Map.Entry<String, ?> entry : dtdInput.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof byte[]) {
				dtdBytes.put(entry.getKey(), (byte[]) value);
			} else if (value instanceof InputStream) {
				try {
					byte[] bytes = getBytes((InputStream) value);
					dtdBytes.put(entry.getKey(), bytes);
				} catch (IOException e) {
					System.err
							.println("Unable to get DTD bytes from stream for "
									+ entry.getKey() + "!");
				}
			} else {
				System.err.println("Invalid value in DTD map for key \""
						+ entry.getKey()
						+ "\"; neither byte[] nor InputStream!");
			}
		}
//		System.out.println(this.dtdBytes);
	}

	/**
	 * Reads the given InputStream to the end and stores the bytes read in a
	 * byte array.
	 * 
	 * @param in
	 *            The InputStream to read the bytes from
	 * @return The byte array containing the bytes from the InputStream
	 * @throws IOException
	 *             In case an error occurs during reading
	 */
	private byte[] getBytes(InputStream in) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] temp = new byte[1024];
		int count = in.read(temp);
		while (count > 0) {
			byteOut.write(temp, 0, count);
			count = in.read(temp);
		}
		in.close();
		byteOut.close();
		return byteOut.toByteArray();
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {

		//        System.out.println("publicId: " + publicId + "\nsystemId: " + systemId);

		if (systemId == null) {
			return null;
		}

		// Try to find the InputStream by the full System Identifier
		byte[] bytes = dtdBytes.get(systemId);
		if (bytes != null) {
			InputStream in = new ByteArrayInputStream(bytes);
			if (in != null) {
				return new InputSource(in);
			}
		}

		// Try to find the InputStream by the file name only
		int lastSlash = systemId.lastIndexOf('/');
		String fileName = systemId.substring(lastSlash + 1);
		InputSource source = null;
		bytes = dtdBytes.get(fileName);
		if (bytes != null) {
			InputStream in = new ByteArrayInputStream(bytes);
			if (in != null) {
				source = new InputSource(in);
			}
		}
		return source;
	}

}
