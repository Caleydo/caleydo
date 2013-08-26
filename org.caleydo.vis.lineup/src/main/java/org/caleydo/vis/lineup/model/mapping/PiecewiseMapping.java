/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Iterators;

/**
 * a special {@link ScriptedMappingFunction}, which has a defined semantic
 *
 * @author Samuel Gratzl
 *
 */
public final class PiecewiseMapping extends ScriptedMappingFunction implements Iterable<Entry<Double, Double>>,
		IMappingFunction, Cloneable {
	private final SortedMap<Double, Double> mapping = new TreeMap<>();
	// are the semantic mapping used or just the code
	private boolean isDefinedMapping = true;

	public PiecewiseMapping(double fromMin, double fromMax) {
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
	public boolean isComplexMapping() {
		if (isDefinedMapping) {
			switch (size()) {
			case 0:
				return false;
			case 1: // TODO
				return false;
			case 2:
				double k1 = mapping.firstKey();
				double k2 = mapping.lastKey();
				// linear combination or inverse
				return mapping.get(k1) > mapping.get(k2);
			default:
				return true;
			}
		} else
			return super.isComplexMapping();
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
		if (!Double.isNaN(getFromMin())) {
			put(getFromMin(), 0);
		}
		if (!Double.isNaN(getFromMax())) {
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
			b.append(String.format("if (value < %s) return NaN%n", min_from));
			b.append(String.format("else if (value <= %s) return linear(%s, %s, value, %s, %s)%n", max_from, min_from,
					max_from, min_to, max_to));
		} else {
			Map.Entry<Double, Double> last = null;
			for (Map.Entry<Double, Double> entry : this) {
				if (last == null) {
					b.append(String.format(Locale.ENGLISH, "if (value < %.2g) return NaN%n", entry.getKey()));
				} else {
					b.append(String
							.format(Locale.ENGLISH,
							"else if (value <= %2$.2g) return linear(%1$.2g, %2$.2g, value, %3$.2g, %4$.2g)%n",
									last.getKey(), entry.getKey(), last.getValue(), entry.getValue()));
				}
				last = entry;
			}
		}
		b.append("else return NaN");
		return b.toString();
	}

	public void put(double from, double to) {
		mapping.put(from, to);
	}

	public void update(double oldFrom, double oldTo, double from, double to) {
		if (oldFrom == from && oldTo == to)
			return;
		mapping.remove(oldFrom);
		mapping.put(from, to);
	}

	@Override
	public Iterator<Entry<Double, Double>> iterator() {
		return Iterators.unmodifiableIterator(mapping.entrySet().iterator());
	}

	public int size() {
		return mapping.size();
	}

	public void remove(double from) {
		mapping.remove(from);
	}

	public void clear() {
		this.isDefinedMapping = true;
		mapping.clear();
	}

	@Override
	public double apply(double in) {
		if (Double.isNaN(in))
			return in;

		if (!isDefinedMapping)
			return super.apply(in);

		if (mapping.size() < 2) {// default
			double[] m0 = getMappedMin();
			double[] m1 = getMappedMax();
			return JavaScriptFunctions.linear(m0[0], m1[0], in, m0[1], m1[1]);
		}
		if (mapping.containsKey(in))
			return mapping.get(in);

		Double first = mapping.firstKey();
		Double last = mapping.lastKey();

		if (in < first || in > last)
			return Double.NaN;
		if (mapping.size() == 2) {
			return JavaScriptFunctions.linear(first, last, in, mapping.get(first), mapping.get(last));
		} else {
			SortedMap<Double, Double> before = mapping.headMap(in);
			if (before.isEmpty()) // outside of the range
				return Double.NaN;
			Double start = before.lastKey();
			Double startTo = before.get(start);
			SortedMap<Double, Double> after = mapping.tailMap(in);
			if (after.isEmpty())
				return Double.NaN;
			Double end = after.firstKey();
			Double endTo = after.get(end);
			return JavaScriptFunctions.linear(start, end, in, startTo, endTo);
		}
	}


	@Override
	public double[] getMappedMin() {
		if (!isDefinedMapping)
			return super.getMappedMin();
		if (mapping.isEmpty())
			return new double[] { getActMin(), 0 };
		double k = mapping.firstKey();
		if (mapping.size() == 1 && isDefaultMin(k)) {
			return new double[] { getActMin(), 0 };
		}
		return new double[] { k, mapping.get(k) };
	}

	@Override
	public double getMinTo() {
		if (!isDefinedMapping)
			return super.getMinTo();
		if (mapping.isEmpty() || mapping.size() == 1 && isDefaultMin(mapping.firstKey()))
			return 0;
		double min = Double.POSITIVE_INFINITY;
		for (Double v : this.mapping.values())
			min = Math.min(min, v);
		return min;
	}

	@Override
	public double getMaxTo() {
		if (!isDefinedMapping)
			return super.getMaxTo();
		if (mapping.isEmpty() || mapping.size() == 1 && !isDefaultMin(mapping.firstKey()))
			return 1;
		double max = Double.NEGATIVE_INFINITY;
		for (Double v : this.mapping.values())
			max = Math.max(max, v);
		return max;
	}


	@Override
	public double[] getMappedMax() {
		if (!isDefinedMapping)
			return super.getMappedMax();
		if (mapping.isEmpty())
			return new double[] { getActMax(), 1 };
		double k = mapping.lastKey();
		if (mapping.size() == 1 && !isDefaultMin(k)) {
			return new double[] { getActMax(), 1 };
		}
		return new double[] { k, mapping.get(k) };
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
		test(Double.NaN, t.apply(-0.1f));
		test(Double.NaN, t.apply(1.1f));

		t.clear();
		System.out.println("test 0->1");
		t.put(0, 1);
		t.put(1, 0);
		System.out.println(t.toJavaScript());
		test(1, t.apply(0));
		test(0, t.apply(1));
		test(0.25f, t.apply(0.75f));
		test(Double.NaN, t.apply(-0.1f));
		test(Double.NaN, t.apply(1.1f));

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
		test(Double.NaN, t.apply(1.1f));

		// PiecewiseMapping p = new PiecewiseMapping(Double.NaN, Double.NaN);
		// p.setActStatistics(new DoubleStatistics(-1, 1, 0, 0, 0, 0));
		// System.out.println(p.toJavaScript());
		// p = new PiecewiseMapping(0, Double.NaN);
		// p.setActStatistics(new DoubleStatistics(0, 100, 0, 0, 0, 0));
		// System.out.println(p.toJavaScript());
		// p = new PiecewiseMapping(Double.NaN, 1);
		// p.setActStatistics(new DoubleStatistics(-10, 10, 0, 0, 0, 0));
		// System.out.println(p.toJavaScript());
	}

	private static void test(double expected, double actual) {
		if (expected != actual && !(Double.isNaN(expected) && Double.isNaN(actual)))
			System.err.println("wrong: " + expected + " actual: " + actual);
	}

}
