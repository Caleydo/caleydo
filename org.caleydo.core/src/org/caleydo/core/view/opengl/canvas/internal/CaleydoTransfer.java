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
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.ITransferSerializer;
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
	private static final String EXTENSION_POINT = "org.caleydo.core.view.dnd.TransferSerializer";

	private static Collection<ITransferSerializer> serializers = ExtensionUtils.findImplementation(EXTENSION_POINT,
			"class", ITransferSerializer.class);

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
			switch (mode) {
			case XML:
				o.writeChar('X');
				Marshaller m = context().createMarshaller();
				m.marshal(data, o);
				break;
			case SERIALIZABLE:
				o.writeChar('S');
				o.writeObject(data);
				break;
			case CUSTOM:
				o.writeChar('C');
				ITransferSerializer s = findSerializer(data.getClass());
				assert s != null;
				o.writeInt(s.getId().length());
				o.writeChars(s.getId());
				s.write(data, o);
				break;
			default:
				break;
			}
			o.close();
			super.javaToNative(out.toByteArray(), transferData);
		} catch (IOException | JAXBException e) {
			log.error("can't serialize: " + data, e);
		}
	}


	private enum EMode {
		INVALID, XML, SERIALIZABLE, CUSTOM
	}

	/**
	 * @param data
	 * @return
	 */
	private static EMode extractMode(Object data) {
		if (data == null)
			return EMode.INVALID;
		Class<? extends Object> c = data.getClass();
		if (findSerializer(c) != null)
			return EMode.CUSTOM;
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
			switch (c) {
			case 'X':
				Unmarshaller unmarshaller = context().createUnmarshaller();
				return unmarshaller.unmarshal(in);
			case 'S':
				return in.readObject();
			case 'C':
				int l = in.readInt();
				String id = readChars(in, l);
				ITransferSerializer s = findSerializer(id);
				if (s == null)
					throw new ClassNotFoundException("can't find serializer with id: " + s);
				return s.read(in);
			default:
				throw new IOException("invalid encoding type: " + c);
			}
		} catch (IOException | JAXBException | ClassNotFoundException e) {
			log.error("can't deserialize: " + Arrays.toString(data), e);
			// can't get here
			return null;
		}
	}

	/**
	 * @param id
	 * @return
	 */
	private static ITransferSerializer findSerializer(String id) {
		for (ITransferSerializer s : serializers)
			if (s.getId().equals(id))
				return s;
		return null;
	}

	/**
	 * @param class1
	 * @return
	 */
	private static ITransferSerializer findSerializer(Class<?> clazz) {
		for (ITransferSerializer s : serializers)
			if (s.apply(clazz))
				return s;
		return null;
	}

	/**
	 * @param in
	 * @param l
	 * @return
	 * @throws IOException
	 */
	private static String readChars(ObjectInputStream in, int l) throws IOException {
		char[] r = new char[l];
		for (int i = 0; i < l; ++i)
			r[i] = in.readChar();
		return new String(r);
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