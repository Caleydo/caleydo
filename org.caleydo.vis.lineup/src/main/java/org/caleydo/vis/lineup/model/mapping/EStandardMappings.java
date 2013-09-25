/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;


/**
 * @author Samuel Gratzl
 *
 */
public enum EStandardMappings{
	LINEAR, INVERT, ABS, Z_SCORE, LOG, P_Q_VALUE;

	public void apply(IMappingFunction mapping) {
		switch (this) {
		case LINEAR:
			if (mapping instanceof PiecewiseMapping) {
				PiecewiseMapping p = (PiecewiseMapping) mapping;
				p.clear();
				p.put(mapping.getActMin(), 0);
				p.put(mapping.getActMax(), 1);
			} else {
				mapping.fromJavaScript(wrapFilter("linear(value_min, value_max, value, 0, 1)"));
			}
			return;
		case INVERT:
			if (mapping instanceof PiecewiseMapping) {
				PiecewiseMapping p = (PiecewiseMapping) mapping;
				p.clear();
				p.put(mapping.getActMin(), 1);
				p.put(mapping.getActMax(), 0);
			} else {
				mapping.fromJavaScript(wrapFilter("linear(0, 1, value, 1, 0)"));
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
				mapping.fromJavaScript(wrapFilter("linear(0, abs(value_min, value_max), abs(value), 0, 1)"));
			}
			return;
		case Z_SCORE:
			mapping.fromJavaScript(wrapFilter("((value - data.mean) / data.sd) + 0.5"));
			return;
		case LOG:
			mapping.fromJavaScript(wrapFilter("linear(log(Math.max(10E-10,value_min)), log(Math.max(10E-10,value_max)), log(Math.max(10E-10,value)), 0, 1)"));
			return;
		case P_Q_VALUE:
			mapping.fromJavaScript(wrapFilter("linear(0.0, -log(Math.max(10e-10,value_min)), -log(Math.max(10e-10,value)), 0.0, 1.0)"));
			return;
		}
		throw new IllegalStateException();
	}

	/**
	 * @param string
	 * @return
	 */
	private String wrapFilter(String code) {
		return "if (value < filter.raw_min || value > filter.raw_max) return NaN\n" + //
				"var n = " + code + "\n" + //
				"if (n < filter.normalized_min || n > filter.normalized_max) return NaN\n" + //
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
}
