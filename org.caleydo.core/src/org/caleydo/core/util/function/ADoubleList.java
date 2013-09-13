/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.AbstractList;
import java.util.BitSet;

/**
 * basic implementation of a {@link IDoubleList}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class ADoubleList extends AbstractList<Double> implements IDoubleList {

	@Override
	public final IDoubleListView map(IDoubleFunction f) {
		return new TransformedDoubleListView(this, f);
	}

	@Override
	public final double reduce(double start, IDoubleReduction r) {
		return reduceImpl(this, start, r);
	}

	private double reduceImpl(ADoubleList list, double start, IDoubleReduction r) {
		double result = start;
		for (IDoubleIterator it = list.iterator(); it.hasNext();) {
			result = r.reduce(result, it.nextPrimitive());
		}
		return result;
	}

	@Override
	public final IDoubleList filter(IDoublePredicate p) {
		return filterImpl(this, p);
	}

	public static IDoubleList filterImpl(ADoubleList list, IDoublePredicate p) {
		BitSet si = new BitSet();
		int i = 0;
		for (IDoubleIterator it = list.iterator(); it.hasNext(); i++) {
			if (p.apply(it.nextPrimitive()))
				si.set(i);
		}
		int s = si.cardinality();
		double[] data = new double[s];
		i = 0;
		for (int j = si.nextSetBit(0); j >= 0; j = si.nextSetBit(j + 1)) {
			data[i++] = list.getPrimitive(j);
		}
		return new ArrayDoubleList(data);
	}

	@Override
	public final Double get(int index) {
		return getPrimitive(index);
	}

	@Override
	public double[] toPrimitiveArray() {
		int s = size();
		double[] data = new double[s];
		for (int i = 0; i < s; ++i)
			data[i] = getPrimitive(i);
		return data;
	}

	@Override
	public IDoubleSizedIterator iterator() {
		return new IDoubleSizedIterator() {
			int cursor = 0;

			@Override
			public int size() {
				return ADoubleList.this.size();
			}

			@Override
			public void remove() {
				ADoubleList.this.remove(cursor - 1);
			}

			@Override
			public Double next() {
				return nextPrimitive();
			}

			@Override
			public boolean hasNext() {
				return cursor < size();
			}

			@Override
			public double nextPrimitive() {
				return get(cursor++);
			}
		};
	}

}
