/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import static org.caleydo.vis.lineup.model.mapping.JavaScriptFunctions.abs;
import static org.caleydo.vis.lineup.model.mapping.JavaScriptFunctions.linear;
import static org.caleydo.vis.lineup.model.mapping.JavaScriptFunctions.log;

import org.caleydo.core.util.function.DoubleStatistics;
/**
 * @author Samuel Gratzl
 *
 */
public enum EStandardMappings{
	LINEAR("linear(value_min, value_max, value, 0, 1)"), //
	INVERT("linear(0, 1, value, 1, 0)"), //
	ABS("linear(0, Math.max(value_min, value_max), abs(value), 0, 1)"), //
	Z_SCORE("((value - data.mean) / data.sd) + 0.5"), //
	LOG("linear(log(Math.max(10E-10,value_min)), log(Math.max(10E-10,value_max)), log(Math.max(10E-10,value)), 0, 1)"), //
	P_Q_VALUE("linear(0.0, -log(Math.max(10e-10,value_min)), -log(Math.max(10e-10,value)), 0.0, 1.0)");

	private final String code;

	private EStandardMappings(String code) {
		this.code = wrapFilter(code);
	}

	/**
	 * @param value
	 */
	private double apply(double value, ScriptedMappingFunction f) {
		final double value_min = f.getActMin();
		final double value_max = f.getActMax();
		final DoubleStatistics data = f.actStats;
		switch (this) {
		case LINEAR:
			return linear(f.getActMin(), f.getActMax(), value, 0, 1);
		case INVERT:
			return linear(0, 1, value, 1, 0);
		case ABS:
			return linear(0, Math.max(value_min, value_max), abs(value), 0, 1);
		case Z_SCORE:
			return ((value - data.getMean()) / data.getSd()) + 0.5;
		case LOG:
			return linear(log(Math.max(10E-10, value_min)), log(Math.max(10E-10, value_max)),
					log(Math.max(10E-10, value)), 0, 1);
		case P_Q_VALUE:
			return linear(0.0, -log(Math.max(10e-10, value_min)), -log(Math.max(10e-10, value)), 0.0, 1.0);
		}
		throw new IllegalStateException();
	}

	public void apply(IMappingFunction mapping) {
		switch (this) {
		case LINEAR:
			if (mapping instanceof PiecewiseMapping) {
				PiecewiseMapping p = (PiecewiseMapping) mapping;
				p.clear();
				p.put(mapping.getActMin(), 0);
				p.put(mapping.getActMax(), 1);
			} else {
				mapping.fromJavaScript(code);
			}
			return;
		case INVERT:
			if (mapping instanceof PiecewiseMapping) {
				PiecewiseMapping p = (PiecewiseMapping) mapping;
				p.clear();
				p.put(mapping.getActMin(), 1);
				p.put(mapping.getActMax(), 0);
			} else {
				mapping.fromJavaScript(code);
			}
			return;
		case ABS:
			if (mapping instanceof PiecewiseMapping) {
				PiecewiseMapping p = (PiecewiseMapping) mapping;
				p.clear();
				p.put(mapping.getActMin(), 1);
				p.put((mapping.getActMax() + mapping.getActMin()) * 0.5f, 0);
				p.put(mapping.getActMax(), 1);
			} else {
				mapping.fromJavaScript(code);
			}
			return;
		case Z_SCORE:
		case LOG:
		case P_Q_VALUE:
			mapping.fromJavaScript(code);
			return;
		}
		throw new IllegalStateException();
	}

	/**
	 * @param string
	 * @return
	 */
	private String wrapFilter(String code) {
		return "if (!filter.filterRaw(value)) return NaN\n" + //
				"var n = " + code + "\n" + //
				"if (!filter.filterNormalized(n)) return NaN\n" + //
				"return n";
	}

	@Override
	public String toString() {
		switch (this) {
		case LINEAR:
			return "Linear";
		case INVERT:
			return "Invert";
		case ABS:
			return "Abs";
		case Z_SCORE:
			return "Z-Score";
		case LOG:
			return "Log";
		case P_Q_VALUE:
			return "P/Q-value";
		}
		throw new IllegalStateException();
	}

	/**
	 * @param code
	 * @param in
	 * @param f
	 * @return
	 */
	public static Double apply(String code, double value, ScriptedMappingFunction f) {
		if (!f.getFilter().filterRaw(value))
			return Double.NaN;
		for (EStandardMappings m : EStandardMappings.values()) {
			if (m.code.equals(code)) {
				double r = m.apply(value, f);
				if (!f.getFilter().filterNormalized(r))
					return Double.NaN;
				return r;
			}
		}
		return null;
	}
}
