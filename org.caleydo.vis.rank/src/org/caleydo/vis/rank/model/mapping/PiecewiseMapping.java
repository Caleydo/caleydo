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
package org.caleydo.vis.rank.model.mapping;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.caleydo.core.util.function.FloatStatistics;

import com.google.common.collect.Iterators;

/**
 * a special {@link ScriptedMappingFunction}, which has a defined semantic
 *
 * @author Samuel Gratzl
 *
 */
public class PiecewiseMapping extends ScriptedMappingFunction implements Iterable<Entry<Float, Float>>,
		IMappingFunction {
	private final SortedMap<Float, Float> mapping = new TreeMap<>();
	// are the semantic mapping used or just the code
	private boolean isDefinedMapping = true;

	public PiecewiseMapping(float fromMin, float fromMax) {
		super(fromMin, fromMax);
		reset();
	}

	public PiecewiseMapping(PiecewiseMapping copy) {
		super(copy);
		this.mapping.putAll(copy.mapping);
	}

	/**
	 * @return the isDefinedMapping, see {@link #isDefinedMapping}
	 */
	public boolean isDefinedMapping() {
		return isDefinedMapping;
	}

	@Override
	public void fromJavaScript(String code) {
		this.isDefinedMapping = updateFromCode(code);
		super.fromJavaScript(code);
	}

	@Override
	public PiecewiseMapping clone() {
		return new PiecewiseMapping(this);
	}

	public boolean updateFromCode(String code) {
		// no change
		if (isDefinedMapping && this.toJavaScript().equals(code))
			return true;
		mapping.clear();
		// TODO an intelligent approach to re interpret the source
		return false;
	}

	@Override
	public void reset() {
		super.reset();
		mapping.clear();
		if (!Float.isNaN(getFromMin())) {
			put(getFromMin(), 0);
		}
		if (!Float.isNaN(getFromMax())) {
			put(getFromMax(), 1);
		}
		this.isDefinedMapping = true;
	}

	@Override
	public String toJavaScript() {
		if (!isDefinedMapping)
			return super.toJavaScript();
		StringBuilder b = new StringBuilder();
		if (mapping.size() < 2) {
			String min_from, min_to, max_from, max_to;
			if (mapping.isEmpty() || isDefaultMin(mapping.firstKey())) {
				min_from = "value_min";
				min_to = "0";
			} else {
				min_from = String.format(Locale.ENGLISH, "%.2g",mapping.firstKey());
				min_to = String.format(Locale.ENGLISH, "%.2g",mapping.get(mapping.firstKey()));
			}
			if (mapping.isEmpty() || !isDefaultMin(mapping.firstKey())) {
				max_from = "value_max";
				max_to = "1";
			} else {
				max_from = String.format(Locale.ENGLISH, "%.2g",mapping.firstKey());
				max_to = String.format(Locale.ENGLISH, "%.2g",mapping.get(mapping.firstKey()));
			}
			b.append(String.format("if (value < %s) return NaN\n", min_from));
			b.append(String.format("else if (value <= %s) return linear(%s, %s, value, %s, %s)\n",max_from,min_from,max_from,min_to,max_to));
		} else {
			Map.Entry<Float, Float> last = null;
			for (Map.Entry<Float, Float> entry : this) {
				if (last == null) {
					b.append(String.format(Locale.ENGLISH, "if (value < %.2g) return NaN\n", entry.getKey()));
				} else {
					b.append(String
							.format(Locale.ENGLISH,
							"else if (value <= %2$.2g) return linear(%1$.2g, %2$.2g, value, %3$.2g, %4$.2g)\n",
									last.getKey(), entry.getKey(), last.getValue(), entry.getValue()));
				}
				last = entry;
			}
		}
		b.append("else return NaN");
		return b.toString();
	}

	public void put(float from, float to) {
		mapping.put(from, to);
	}

	public void update(float oldFrom, float oldTo, float from, float to) {
		if (oldFrom == from && oldTo == to)
			return;
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
		this.isDefinedMapping = true;
		mapping.clear();
	}

	@Override
	public float apply(float in) {
		if (Float.isNaN(in))
			return in;

		if (!isDefinedMapping)
			return super.apply(in);

		if (mapping.size() < 2) {// default
			float[] m0 = getMappedMin();
			float[] m1 = getMappedMax();
			return MappingFunctions.linear(m0[0], m1[0], in, m0[1], m1[1]);
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

		return MappingFunctions.linear(start, end, in, startTo, endTo);
	}


	@Override
	public float[] getMappedMin() {
		if (!isDefinedMapping)
			return super.getMappedMin();
		if (mapping.isEmpty())
			return new float[] { getActMin(), 0 };
		float k = mapping.firstKey();
		if (mapping.size() == 1 && isDefaultMin(k)) {
			return new float[] { getActMin(), 0 };
		}
		return new float[] { k, mapping.get(k) };
	}

	@Override
	public float getMinTo() {
		if (!isDefinedMapping)
			return super.getMinTo();
		if (mapping.isEmpty() || mapping.size() == 1 && isDefaultMin(mapping.firstKey()))
			return 0;
		float min = Float.POSITIVE_INFINITY;
		for (Float v : this.mapping.values())
			min = Math.min(min, v);
		return min;
	}

	@Override
	public float getMaxTo() {
		if (!isDefinedMapping)
			return super.getMaxTo();
		if (mapping.isEmpty() || mapping.size() == 1 && !isDefaultMin(mapping.firstKey()))
			return 1;
		float max = Float.NEGATIVE_INFINITY;
		for (Float v : this.mapping.values())
			max = Math.max(max, v);
		return max;
	}


	@Override
	public float[] getMappedMax() {
		if (!isDefinedMapping)
			return super.getMappedMax();
		if (mapping.isEmpty())
			return new float[] { getActMax(), 1 };
		float k = mapping.lastKey();
		if (mapping.size() == 1 && !isDefaultMin(k)) {
			return new float[] { getActMax(), 1 };
		}
		return new float[] { k, mapping.get(k) };
	}

	@Override
	public boolean isMappingDefault() {
		return mapping.size() < 2;
	}

	public static void main(String[] args) {
		PiecewiseMapping t = new PiecewiseMapping(0, 1);
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

		PiecewiseMapping p = new PiecewiseMapping(Float.NaN, Float.NaN);
		p.setActStatistics(new FloatStatistics(-1, 1, 0, 0, 0, 0));
		System.out.println(p.toJavaScript());
		p = new PiecewiseMapping(0, Float.NaN);
		p.setActStatistics(new FloatStatistics(0, 100, 0, 0, 0, 0));
		System.out.println(p.toJavaScript());
		p = new PiecewiseMapping(Float.NaN, 1);
		p.setActStatistics(new FloatStatistics(-10, 10, 0, 0, 0, 0));
		System.out.println(p.toJavaScript());
	}

	private static void test(float expected, float actual) {
		if (expected != actual && !(Float.isNaN(expected) && Float.isNaN(actual)))
			System.err.println("wrong: " + expected + " actual: " + actual);
	}

}
