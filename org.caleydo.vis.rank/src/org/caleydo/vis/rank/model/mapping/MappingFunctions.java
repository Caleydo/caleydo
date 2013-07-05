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

import java.util.Arrays;



/**
 * @author Samuel Gratzl
 *
 */
public class MappingFunctions {
	public static float linear(float start, float end, float in, float startTo, float endTo) {
		if (Float.isNaN(in))
			return in;
		if (in < start)
			return Float.NaN;
		if (in > end)
			return Float.NaN;
		// linear interpolation between start and end
		float v = (in - start) / (end - start); // to ratio
		// to mapped value
		float r = startTo + v * (endTo - startTo);

		// finally clamp
		return clamp01(r);
	}


	public static float clamp01(float in) {
		if (Float.isNaN(in))
			return in;
		return (in < 0 ? 0 : (in > 1 ? 1 : in));
	}

	public static float clamp(float in, float min, float max) {
		if (Float.isNaN(in))
			return in;
		return (in < min ? min : (in > max ? max : in));
	}

	public static float normalize(float in, float min, float max) {
		if (Float.isNaN(in))
			return in;
		return clamp01((in - min) / (max - min));
	}

	public static float log(float in) {
		return (float) Math.log(in);
	}

	public static float log10(float in) {
		return (float) Math.log10(in);
	}

	public static float max(float[] in) {
		if (in.length == 0)
			return Float.NaN;
		float m = in[0];
		for (int i = 1; i < in.length; ++i)
			if (in[i] > m)
				m = in[i];
		return m;
	}

	public static float min(float[] in) {
		if (in.length == 0)
			return Float.NaN;
		float m = in[0];
		for (int i = 1; i < in.length; ++i)
			if (in[i] < m)
				m = in[i];
		return m;
	}

	public static float sum(float[] in) {
		float m = 0;
		for (int i = 0; i < in.length; ++i)
			m += in[i];
		return m;
	}

	public static float mean(float[] in) {
		if (in.length == 0)
			return 0;
		return sum(in) / in.length;
	}

	public static float geometricMean(float[] data) {
		if (data.length == 0)
			return 1;
		float c = 1;
		for (int i = 0; i < data.length; ++i)
			c *= data[i];
		return (float) Math.pow(c, 1. / data.length);
	}

	public static float median(float[] data) {
		if (data.length == 0)
			return 0;
		data = Arrays.copyOf(data, data.length);
		Arrays.sort(data);
		int center = data.length / 2;
		if (data.length % 2 == 0)
			return 0.5f * (data[center] + data[center + 1]);
		else
			return data[center + 1];
	}
}
