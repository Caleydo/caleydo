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
package org.caleydo.view.tourguide.api.score;

import java.util.Arrays;

import org.caleydo.core.util.base.ILabeled;

import com.google.common.base.Function;

/**
 * @author Samuel Gratzl
 *
 */
public enum ECombinedOperator implements Function<float[], Float>, ILabeled {
	MAX, MIN, MEAN, MEDIAN, GEOMETRIC_MEAN;

	@Override
	public Float apply(float[] data) {
		return combine(data);
	}

	public String getAbbreviation() {
		switch (this) {
		case GEOMETRIC_MEAN:
			return "GEO";
		case MAX:
			return "MAX";
		case MEAN:
			return "AVG";
		case MEDIAN:
			return "MED";
		case MIN:
			return "MIN";
		}
		throw new IllegalStateException("unknown operator: " + this);
	}

	@Override
	public String getLabel() {
		switch (this) {
		case GEOMETRIC_MEAN:
			return "Geometric Mean";
		case MAX:
			return "Maximum";
		case MEAN:
			return "Average";
		case MEDIAN:
			return "Median";
		case MIN:
			return "Minium";
		}
		throw new IllegalStateException("unknown operator: " + this);
	}

	public float combine(float[] data) {
		float c = 0;
		switch (this) {
		case MAX:
			if (data.length == 0)
				return Float.NaN;
			c = data[0];
			for (int i = 1; i < data.length; ++i)
				c = Math.max(c, data[i]);
			return c;
		case MIN:
			if (data.length == 0)
				return Float.NaN;
			c = data[0];
			for (int i = 1; i < data.length; ++i)
				c = Math.min(c, data[i]);
			return c;
		case MEAN:
			if (data.length == 0)
				return 0;
			c = 0;
			for (int i = 0; i < data.length; ++i)
				c += data[i];
			return c / data.length;
		case GEOMETRIC_MEAN:
			if (data.length == 0)
				return 1;
			c = 1;
			for (int i = 0; i < data.length; ++i)
				c *= data[i];
			return (float) Math.pow(c, 1. / data.length);
		case MEDIAN:
			Arrays.sort(data);
			int center = data.length / 2;
			if (data.length % 2 == 0)
				return 0.5f * (data[center] + data[center + 1]);
			else
				return data[center + 1];
		}
		throw new IllegalStateException("unknown operator: " + this);
	}
}
