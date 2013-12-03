/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * a customized dnd transfer type, which serializes an object using {@link javax.xml.bind.JAXB}
 *
 * @author Samuel Gratzl
 *
 */
public class CaleydoJAXBTransfer extends ByteArrayTransfer {
	private static final Logger log = Logger.create(CaleydoJAXBTransfer.class);
	private static final String TYPE_NAME = "caleydo-transfer-format";//$NON-NLS-1$

	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Singleton instance.
	 */
	private static final CaleydoJAXBTransfer instance = new CaleydoJAXBTransfer();

	/**
	 * Creates a new transfer object.
	 */
	private CaleydoJAXBTransfer() {
		super();
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static CaleydoJAXBTransfer getInstance() {
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
		if (invalid(data)) {
			return;
		}
		try {
			Marshaller m = context().createMarshaller();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			m.marshal(data, out);
			out.close();
			super.javaToNative(out.toByteArray(), transferData);
		} catch (IOException | JAXBException e) {
			log.error("can't serialize: " + data, e);
		}
	}

	/**
	 * @param data
	 * @return
	 */
	private static boolean invalid(Object data) {
		if (data == null)
			return true;
		Class<? extends Object> c = data.getClass();
		if (!c.isAnnotationPresent(XmlRootElement.class) || !c.isAnnotationPresent(XmlType.class))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc) Method declared on Transfer.
	 */
	@Override
	public Object nativeToJava(TransferData transferData) {
		byte[] data = ((byte[]) super.nativeToJava(transferData));
		if (data.length == 0)
			return null;
		try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
			Unmarshaller unmarshaller = context().createUnmarshaller();
			return unmarshaller.unmarshal(in);
		} catch (IOException | JAXBException e) {
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
		return !invalid(data);
	}
}