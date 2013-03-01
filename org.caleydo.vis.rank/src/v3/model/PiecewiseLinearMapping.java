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
package org.caleydo.view.tourguide.v3.model;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.function.AFloatFunction;
import org.caleydo.core.util.function.FloatFunctions;

import com.google.common.collect.Iterators;

/**
 * @author Samuel Gratzl
 *
 */
public class PiecewiseLinearMapping extends AFloatFunction implements Iterable<Entry<Float, Float>> {
	private ICallback<PiecewiseLinearMapping> onChange;

	private final SortedMap<Float, Float> mapping = new TreeMap<>();
	private final float fromMin;
	private final float fromMax;

	private float actMin;
	private float actMax;

	public PiecewiseLinearMapping(float fromMin, float fromMax) {
		this.fromMin = fromMin;
		this.fromMax = fromMax;
		this.actMin = 0;
		if (!Float.isNaN(fromMin)) {
			this.actMin = fromMin;
			put(fromMin, 0);
		}
		this.actMax = 1;
		if (!Float.isNaN(fromMax)) {
			this.actMax = fromMax;
			put(fromMax, 1);
		}
	}

	public String toJavaScript() {
		StringBuilder b = new StringBuilder();
		if (mapping.isEmpty())
			b.append("clamp(value, 0, 1)");
		else if (mapping.size() == 1) {
		} else {
			Map.Entry<Float, Float> last = null;
			for (Map.Entry<Float, Float> entry : this) {
				if (last == null) {
					b.append(String.format(Locale.ENGLISH, "if (value < %.2g) return Float.NaN\n", entry.getKey()));
				} else {
					b.append(String
							.format(Locale.ENGLISH,
									"else if (inRange(value, %1$.2g, %2$.2g)) return linear(%1$.2g, %2$.2g, value, %3$.2g, %4$.2g)\n",
									last.getKey(), entry.getKey(), last.getValue(), entry.getValue()));
				}
				last = entry;
			}
			b.append(String.format("else return Float.NaN"));
		}
		return b.toString();
	}

	public void setChangeCallback(ICallback<PiecewiseLinearMapping> callback) {
		this.onChange = callback;
	}

	public void put(float from, float to) {
		mapping.put(from, to);
		fireChange();
	}

	private void fireChange() {
		if (onChange != null)
			onChange.on(this);
	}

	public void update(float oldFrom, float oldTo, float from, float to) {
		if (oldFrom == from && oldTo == to)
			return;
		mapping.remove(oldFrom);
		mapping.put(from, to);
		fireChange();
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
		fireChange();
	}

	public void clear() {
		mapping.clear();
		fireChange();
	}

	@Override
	public float apply(float in) {
		if (Float.isNaN(in))
			return in;
		if (mapping.size() < 2) {// default clamping
			if (in < actMin)
				return Float.NaN;
			if (in > actMax)
				return Float.NaN;
			return linearMapping(in, actMin, 0, actMax, 1);
		}
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

		return linearMapping(in, start, startTo, end, endTo);
	}

	private static float linearMapping(float in, float start, float startTo, float end, float endTo) {
		// linear interpolation between start and end
		float v = (in - start) / (end - start); // to ratio
		// to mapped value
		float r = startTo + v * (endTo - startTo);

		// finally clamp
		return FloatFunctions.CLAMP01.apply(r);
	}

	public void setAct(float min, float max) {
		if (Float.isNaN(fromMin))
			actMin = min;
		if (Float.isNaN(fromMax))
			actMax = max;
	}

	public float[] getMappedMin() {
		if (mapping.size() < 2)
			return new float[] { actMin, 0 };
		float k = mapping.firstKey();
		return new float[] { k, mapping.get(k) };
	}

	public float[] getMappedMax() {
		if (mapping.size() < 2)
			return new float[] { actMax, 1 };
		float k = mapping.lastKey();
		return new float[] { k, mapping.get(k) };
	}

	/**
	 * @return the actMin, see {@link #actMin}
	 */
	public float getActMin() {
		return actMin;
	}

	/**
	 * @return the actMax, see {@link #actMax}
	 */
	public float getActMax() {
		return actMax;
	}

	/**
	 * @return the fromMin, see {@link #fromMin}
	 */
	public float getFromMin() {
		return fromMin;
	}
	/**
	 * @return the fromMax, see {@link #fromMax}
	 */
	public float getFromMax() {
		return fromMax;
	}

	public boolean hasDefinedMappingBounds() {
		return !Float.isNaN(fromMin) && !Float.isNaN(fromMax);
	}

	public boolean isMappingDefault() {
		return mapping.size() < 2;
	}

	public static void main(String[] args) {
		PiecewiseLinearMapping t = new PiecewiseLinearMapping(0, 1);
		t.put(0, 0);
		t.put(1, 1);
		System.out.println(t.toJavaScript());

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
		System.out.println(t.toJavaScript());
		test(1, t.apply(0));
		test(0, t.apply(1));
		test(0.25f, t.apply(0.75f));
		test(Float.NaN, t.apply(-0.1f));
		test(Float.NaN, t.apply(1.1f));

		System.out.println("test -1->1, 0->0, +1->1");
		t.put(0, 0);
		t.put(1, 1);
		t.put(-1, 1);
		System.out.println(t.toJavaScript());

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
