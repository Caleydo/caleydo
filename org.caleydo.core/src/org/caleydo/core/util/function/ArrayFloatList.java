/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.Arrays;

/**
 * a {@link IFloatList} based on an array
 *
 * @author Samuel Gratzl
 *
 */
public class ArrayFloatList extends AFloatList {
	private final float[] data;

	public ArrayFloatList(float[] data) {
		this.data = data;
	}

	@Override
	public float getPrimitive(int index) {
		return data[index];
	}

	@Override
	public int size() {
		return data.length;
	}

	@Override
	public float[] toPrimitiveArray() {
		return Arrays.copyOf(data, data.length);
	}
}
