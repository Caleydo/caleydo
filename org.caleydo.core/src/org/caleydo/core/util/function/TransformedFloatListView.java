/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * a {@link IFloatListView} that applies a {@link IFloatFunction} to the underlying data
 *
 * @author Samuel Gratzl
 *
 */
public final class TransformedFloatListView extends AFloatList implements IFloatListView {
	private final AFloatList underlying;
	private final IFloatFunction f;

	public TransformedFloatListView(AFloatList underlying, IFloatFunction f) {
		this.underlying = underlying;
		this.f = f;
	}

	@Override
	public float getPrimitive(int index) {
		return f.apply(underlying.getPrimitive(index));
	}

	@Override
	public Float remove(int index) {
		return underlying.remove(index);
	}

	@Override
	public int size() {
		return underlying.size();
	}

	@Override
	public IFloatList toList() {
		return new ArrayFloatList(toPrimitiveArray());
	}

	@Override
	public float[] toPrimitiveArray() {
		int s = size();
		float[] data = new float[s];
		for (int i = 0; i < s; ++i)
			data[i] = getPrimitive(i);
		return data;
	}
}

