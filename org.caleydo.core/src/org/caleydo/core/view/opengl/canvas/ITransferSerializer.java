/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.view.opengl.canvas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.common.base.Predicate;

/**
 * a transfer serialized is reponsible for serializing and deserializing a dnd transfered object
 *
 * @author Samuel Gratzl
 *
 */
public interface ITransferSerializer extends Predicate<Class<?>> {

	/**
	 * return unique id
	 *
	 * @return
	 */
	String getId();

	/**
	 * @param data
	 * @param o
	 * @throws IOException
	 */
	void write(Object data, ObjectOutputStream o) throws IOException;

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	Object read(ObjectInputStream in) throws ClassNotFoundException, IOException;

	/**
	 * predicate whether a specific class type is supported
	 *
	 * @param clazz
	 * @return
	 */
	@Override
	boolean apply(Class<?> clazz);

}
