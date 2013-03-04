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
}
