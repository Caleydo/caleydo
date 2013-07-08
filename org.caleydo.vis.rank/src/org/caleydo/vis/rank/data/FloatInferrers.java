/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.data;

import java.util.Arrays;

import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.util.function.IFloatIterator;

/**
 * factory class for {@link IFloatInferrer}
 *
 * @author Samuel Gratzl
 *
 */
public class FloatInferrers {
	public static IFloatInferrer MEAN = new IFloatInferrer() {
		@Override
		public float infer(IFloatIterator it, int size) {
			return FloatStatistics.of(it).getMean();
		}
	};

	public static IFloatInferrer MEDIAN = new IFloatInferrer() {
		@Override
		public float infer(IFloatIterator it, int size) {
			float[] tmp = new float[size];
			int i = 0;
			while (it.hasNext()) {
				float v = it.nextPrimitive();
				if (Float.isNaN(v))
					continue;
				tmp[i++] = v;
			}
			tmp = Arrays.copyOf(tmp, i);
			Arrays.sort(tmp);
			if (tmp.length % 2 == 0)
				return 0.5f * (tmp[tmp.length / 2] + tmp[tmp.length / 2 + 1]);
			else
				return tmp[tmp.length / 2 + 1];
		}
	};

	/**
	 * constant value
	 *
	 * @param value
	 * @return
	 */
	public static IFloatInferrer fix(final float value) {
		return new IFloatInferrer() {
			@Override
			public float infer(IFloatIterator it, int size) {
				return value;
			}
		};
	}
}
