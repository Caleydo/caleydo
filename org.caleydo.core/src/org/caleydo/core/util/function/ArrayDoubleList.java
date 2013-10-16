/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.Arrays;

/**
 * a {@link IDoubleList} based on an array
 *
 * @author Samuel Gratzl
 *
 */
public class ArrayDoubleList extends ADoubleList {
	private final double[] data;

	public ArrayDoubleList(double[] data) {
		this.data = data;
	}

	@Override
	public double getPrimitive(int index) {
		return data[index];
	}

	@Override
	public int size() {
		return data.length;
	}

	@Override
	public double[] toPrimitiveArray() {
		return Arrays.copyOf(data, data.length);
	}
}
