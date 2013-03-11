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
package org.caleydo.vis.rank.data;

import java.util.Arrays;

import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.util.function.IFloatIterator;

/**
 * @author Samuel Gratzl
 *
 */
public class FloatInferrers {
	public static IFloatInferrer MEAN = new IFloatInferrer() {
		@Override
		public float infer(IFloatIterator it, int size) {
			return FloatStatistics.compute(it).getMean();
		}
	};

	public static IFloatInferrer MEDIAN = new IFloatInferrer() {
		@Override
		public float infer(IFloatIterator it, int size) {
			float[] tmp = new float[size];
			for (int i = 0; it.hasNext(); ++i) {
				tmp[i] = it.nextPrimitive();
			}
			Arrays.sort(tmp);
			if (size % 2 == 0)
				return 0.5f*(tmp[size/2]+tmp[size/2+1]);
			else
				return tmp[size / 2 + 1];
		}
	};

	public static IFloatInferrer fix(final float value) {
		return new IFloatInferrer() {
			@Override
			public float infer(IFloatIterator it, int size) {
				return value;
			}
		};
	}
}
