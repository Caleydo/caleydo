/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

/**
 * a {@link IDoubleListView} that applies a {@link IDoubleFunction} to the underlying data
 *
 * @author Samuel Gratzl
 *
 */
public final class TransformedDoubleListView extends ADoubleList implements IDoubleListView {
	private final ADoubleList underlying;
	private final IDoubleFunction f;

	public TransformedDoubleListView(ADoubleList underlying, IDoubleFunction f) {
		this.underlying = underlying;
		this.f = f;
	}

	@Override
	public double getPrimitive(int index) {
		return f.apply(underlying.getPrimitive(index));
	}

	@Override
	public Double remove(int index) {
		return underlying.remove(index);
	}

	@Override
	public int size() {
		return underlying.size();
	}

	@Override
	public IDoubleList toList() {
		return new ArrayDoubleList(toPrimitiveArray());
	}
}

