/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import java.util.List;

import com.google.common.base.Function;

/**
 *
 * @author Samuel Gratzl
 *
 * @param <T>
 */
public final class MappedDoubleList<T> extends ADoubleList {
	private final List<T> data;
	private final Function<? super T, Double> f;

	public MappedDoubleList(List<T> data, Function<? super T, Double> f) {
		this.data = data;
		this.f = f;
	}

	@Override
	public double getPrimitive(int index) {
		return f.apply(data.get(index));
	}

	@Override
	public int size() {
		return data.size();
	}

	/**
	 * @return the data, see {@link #data}
	 */
	public List<T> getData() {
		return data;
	}
}