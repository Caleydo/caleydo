/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
	public final float[] computeStats() {
		return computeStats(this.iterator());
	}

	public static float[] computeStats(IFloatIterator it) {
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		int count = 0;
		float sum = 0;
		float sqrsum=0;
		boolean any = false;
		while (it.hasNext()) {
			float v = it.nextPrimitive();
			if (Float.isNaN(v))
				continue;
			count++;
			sum+=v;
			sqrsum+=v*v;
			if (v < min)
				min = v;
			if (max < v)
				max = v;
			any = true;
		}
		if (!any)
			return new float[] { Float.NaN, Float.NaN, Float.NaN, 0, 0, 0 };
		return new float[] { min, max, sum / count, count, sum, sqrsum };
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
