/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.model;

import java.util.AbstractList;
import java.util.List;

/**
 * sub list variant, not checking any concurrent modifications
 * 
 * @author Samuel Gratzl
 * 
 */
public class CustomSubList<T> extends AbstractList<T> {
	private final int offset;
	private final List<T> backend;
	private final int size;

	public CustomSubList(List<T> backend, int offset, int size) {
		super();
		this.backend = backend;
		this.offset = offset;
		this.size = size;
	}

	/**
	 * @return the backend, see {@link #backend}
	 */
	public List<T> getBackend() {
		return backend;
	}

	/**
	 * @return the offset, see {@link #offset}
	 */
	public int getOffset() {
		return offset;
	}

	@Override
	public T get(int index) {
		return backend.get(offset + index);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public T set(int index, T element) {
		return backend.set(offset + index, element);
	}
}
