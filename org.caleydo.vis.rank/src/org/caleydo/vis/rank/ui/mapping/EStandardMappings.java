/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.ui.mapping;

import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;

/**
 * @author Samuel Gratzl
 *
 */
public enum EStandardMappings{
	LINEAR, INVERT, ABS, Z_SCORE, LINEAR_LOG;

	public void apply(IMappingFunction mapping) {
		switch (this) {
		case LINEAR:
			if (mapping instanceof PiecewiseMapping) {
				PiecewiseMapping p = (PiecewiseMapping) mapping;
				p.clear();
				p.put(mapping.getActMin(), 0);
				p.put(mapping.getActMax(), 1);
			} else {
				mapping.fromJavaScript("linear(value_min, value_max, value, 0, 1)");
			}
			return;
		case INVERT:
			if (mapping instanceof PiecewiseMapping) {
				PiecewiseMapping p = (PiecewiseMapping) mapping;
				p.clear();
				p.put(mapping.getActMin(), 1);
				p.put(mapping.getActMax(), 0);
			} else {
				mapping.fromJavaScript("linear(0, 1, value, 1, 0)");
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
				mapping.fromJavaScript("linear(0, abs(value_min, value_max), abs(value), 0, 1)");
			}
			return;
		case Z_SCORE:
			mapping.fromJavaScript("((value - data.mean) / data.sd) + 0.5");
			return;
		case LINEAR_LOG:
			mapping.fromJavaScript("linear(0, log(value_max), log(value), 0, 1)");
			return;
		}
		throw new IllegalStateException();
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
		case LINEAR_LOG:
			return "Log";
		}
		throw new IllegalStateException();
	}
}
