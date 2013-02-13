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
package org.caleydo.view.tourguide.v2.r.model;

import static org.caleydo.view.tourguide.v2.r.model.Values.clamp01;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
/**
 * normalization based on linear interpolation between mapping samples
 *
 * @author Samuel Gratzl
 *
 */
public class InteractiveNormalization implements Function<IValue, IValue>, Iterable<Map.Entry<Float, Float>> {
	private final SortedMap<Float, Float> mapping = new TreeMap<>();

	public void put(float from, float to) {
		mapping.put(from, to);
	}

	public void update(float oldFrom, float oldTo, float from, float to) {
		mapping.remove(oldFrom);
		mapping.put(from, to);
	}

	@Override
	public Iterator<Entry<Float, Float>> iterator() {
		return Iterators.unmodifiableIterator(mapping.entrySet().iterator());
	}

	public int size() {
		return mapping.size();
	}

	public void remove(float from) {
		mapping.remove(from);
	}

	public void clear() {
		mapping.clear();
	}

	@Override
	public IValue apply(IValue in) {
		if (mapping.isEmpty()) // default clamping
			return clamp01(in);

		if (!in.hasMultiple()) {
			return Values.of(apply(in.asFloat()));
		} else {
			float[] ins = in.asFloats();
			float[] r = new float[ins.length];
			for(int i = 0; i< ins.length; ++i)
				r[i] = apply(ins[i]);
			return Values.of(in.getRepr(), r);
		}
	}

	private float apply(float in) {
		if (mapping.isEmpty()) // default clamping
			return clamp01(in);

		if (mapping.containsKey(in))
			return mapping.get(in);

		SortedMap<Float, Float> before = mapping.headMap(in);
		if (before.isEmpty()) // outside of the range
			return Float.NaN;
		Float start = before.lastKey();
		Float startTo = before.get(start);
		SortedMap<Float, Float> after = mapping.tailMap(in);
		if (after.isEmpty())
			return Float.NaN;
		Float end = after.firstKey();
		Float endTo = after.get(end);

		// linear interpolation between start and end
		float v = (in - start) / (end - start); // to ratio
		// to mapped value
		float r = startTo + v * (endTo - startTo);

		// finally clamp
		return clamp01(r);
	}

	public static void main(String[] args) {
		InteractiveNormalization t = new InteractiveNormalization();
		t.put(0, 0);
		t.put(1, 1);

		System.out.println("test 0->0");
		test(0, t.apply(0));
		test(1, t.apply(1));
		test(0.5f, t.apply(0.5f));
		test(0.75f, t.apply(0.75f));
		test(Float.NaN, t.apply(-0.1f));
		test(Float.NaN, t.apply(1.1f));

		t.clear();
		System.out.println("test 0->1");
		t.put(0, 1);
		t.put(1, 0);
		test(1, t.apply(0));
		test(0, t.apply(1));
		test(0.25f, t.apply(0.75f));
		test(Float.NaN, t.apply(-0.1f));
		test(Float.NaN, t.apply(1.1f));

		System.out.println("test -1->1, 0->0, +1->1");
		t.put(0, 0);
		t.put(1, 1);
		t.put(-1, 1);

		test(0, t.apply(0));
		test(1, t.apply(1));
		test(1, t.apply(-1));
		test(0.5f, t.apply(0.5f));
		test(0.1f, t.apply(-0.1f));
		test(Float.NaN, t.apply(1.1f));
	}

	private static void test(float expected, float actual) {
		if (expected != actual && !(Float.isNaN(expected) && Float.isNaN(actual)))
			System.err.println("wrong: " + expected + " actual: " + actual);
	}

}
