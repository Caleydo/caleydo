/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.function;

import java.util.AbstractList;
import java.util.BitSet;

/**
 * basic implementation of a {@link IFloatList}
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AFloatList extends AbstractList<Float> implements IFloatList {

	@Override
	public final IFloatListView map(IFloatFunction f) {
		return new TransformedFloatListView(this, f);
	}

	@Override
	public float reduce(float start, IFloatReduction r) {
		return reduceImpl(this, start, r);
	}

	private float reduceImpl(AFloatList list, float start, IFloatReduction r) {
		float result = start;
		for (IFloatIterator it = list.iterator(); it.hasNext();) {
			result = r.reduce(result, it.nextPrimitive());
		}
		return result;
	}

	@Override
	public IFloatList filter(IFloatPredicate p) {
		return filterImpl(this, p);
	}

	public static IFloatList filterImpl(AFloatList list, IFloatPredicate p) {
		BitSet si = new BitSet();
		int i = 0;
		for (IFloatIterator it = list.iterator(); it.hasNext(); i++) {
			if (p.apply(it.nextPrimitive()))
				si.set(i);
		}
		int s = si.cardinality();
		float[] data = new float[s];
		i = 0;
		for (int j = si.nextSetBit(0); j >= 0; j = si.nextSetBit(j + 1)) {
			data[i++] = list.getPrimitive(j);
		}
		return new ArrayFloatList(data);
	}

	@Override
	public final Float get(int index) {
		return getPrimitive(index);
	}

	@Override
	public IFloatIterator iterator() {
		return new IFloatIterator() {
			int cursor = 0;

			@Override
			public void remove() {
				AFloatList.this.remove(cursor - 1);
			}

			@Override
			public Float next() {
				return nextPrimitive();
			}

			@Override
			public boolean hasNext() {
				return cursor < size();
			}

			@Override
			public float nextPrimitive() {
				return get(cursor++);
			}
		};
	}

}
