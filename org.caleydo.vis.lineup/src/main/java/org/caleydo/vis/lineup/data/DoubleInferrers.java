/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.data;

import java.util.Arrays;

import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.function.IDoubleSizedIterator;

/**
 * factory class for {@link IDoubleInferrer}
 *
 * @author Samuel Gratzl
 *
 */
public class DoubleInferrers {
	public static IDoubleInferrer MEAN = new IDoubleInferrer() {
		@Override
		public double infer(IDoubleSizedIterator it) {
			return DoubleStatistics.of(it).getMean();
		}
	};

	public static IDoubleInferrer MEDIAN = new IDoubleInferrer() {
		@Override
		public double infer(IDoubleSizedIterator it) {
			double[] tmp = new double[it.size()];
			int i = 0;
			while (it.hasNext()) {
				double v = it.nextPrimitive();
				if (Double.isNaN(v))
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
	public static IDoubleInferrer fix(final double value) {
		return new IDoubleInferrer() {
			@Override
			public double infer(IDoubleSizedIterator it) {
				return value;
			}
		};
	}
}
