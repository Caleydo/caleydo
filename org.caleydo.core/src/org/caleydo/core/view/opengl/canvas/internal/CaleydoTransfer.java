/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * a customized dnd transfer type, which serializes an object using {@link javax.xml.bind.JAXB} or {@link Serializable}
 *
 * @author Samuel Gratzl
 *
 */
public class CaleydoTransfer extends ByteArrayTransfer {
	private static final Logger log = Logger.create(CaleydoTransfer.class);
	private static final String TYPE_NAME = "caleydo-transfer-format";//$NON-NLS-1$

	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Singleton instance.
	 */
	private static final CaleydoTransfer instance = new CaleydoTransfer();

	/**
	 * Creates a new transfer object.
	 */
	private CaleydoTransfer() {
		super();
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static CaleydoTransfer getInstance() {
		return instance;
	}

	/*
	 * (non-Javadoc) Method declared on Transfer.
	 */
	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/*
	 * (non-Javadoc) Returns the type names.
	 *
	 * @return the list of type names
	 */
	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/*
	 * (non-Javadoc) Method declared on Transfer.
	 */
	@Override
	public void javaToNative(Object data, TransferData transferData) {
		EMode mode = extractMode(data);
		if (mode == EMode.INVALID)
			return;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(out);
			if (mode == EMode.XML) {
				o.writeChar('X');
				Marshaller m = context().createMarshaller();
				m.marshal(data, o);
			} else {
				o.writeChar('S');
				o.writeObject(data);
			}
			o.close();
			super.javaToNative(out.toByteArray(), transferData);
		} catch (IOException | JAXBException e) {
			log.error("can't serialize: " + data, e);
		}
	}

	private enum EMode {
		INVALID, XML, SERIALIZABLE
	}

	/**
	 * @param data
	 * @return
	 */
	private static EMode extractMode(Object data) {
		if (data == null)
			return EMode.INVALID;
		Class<? extends Object> c = data.getClass();
		if (c.isAnnotationPresent(XmlRootElement.class) || c.isAnnotationPresent(XmlType.class))
			return EMode.XML;
		if (data instanceof Serializable)
			return EMode.SERIALIZABLE;
		return EMode.INVALID;
	}

	/*
	 * (non-Javadoc) Method declared on Transfer.
	 */
	@Override
	public Object nativeToJava(TransferData transferData) {
		byte[] data = ((byte[]) super.nativeToJava(transferData));
		if (data == null || data.length == 0)
			return null;
		try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
			char c = in.readChar();
			if (c == 'X') {
				Unmarshaller unmarshaller = context().createUnmarshaller();
				return unmarshaller.unmarshal(in);
			} else if (c == 'S')
				return in.readObject();
			throw new IOException("invalid encoding type: " + c);
		} catch (IOException | JAXBException | ClassNotFoundException e) {
			log.error("can't deserialize: " + Arrays.toString(data), e);
			// can't get here
			return null;
		}
	}

	private static JAXBContext context() {
		return SerializationManager.get().getProjectContext();
	}

	/**
	 * @param info
	 * @return
	 */
	public static boolean isValid(Object data) {
		return extractMode(data) != EMode.INVALID;
	}
}