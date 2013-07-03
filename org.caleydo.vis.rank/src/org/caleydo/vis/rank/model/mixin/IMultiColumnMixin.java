/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mixin;

import org.caleydo.core.util.color.Color;
import java.util.Arrays;

import org.caleydo.vis.rank.model.ARankColumnModel;
import org.caleydo.vis.rank.model.IRow;

/**
 * contract that the columns is composite of multiple other ones
 *
 * @author Samuel Gratzl
 *
 */
public interface IMultiColumnMixin extends IFloatRankableColumnMixin, Iterable<ARankColumnModel>, IAnnotatedColumnMixin {
	/**
	 * the individual values of the children and their representation index
	 *
	 * repr &lt; 0 to indicate that there is not direct representer
	 *
	 * @param row
	 * @return
	 */
	MultiFloat getSplittedValue(IRow row);

	public static final class MultiFloat {
		public final float[] values;
		public final int repr;

		public MultiFloat(int repr, float... values) {
			this.values = values;
			this.repr = repr;
		}

		public float get() {
			return repr >= 0 ? values[repr] : Float.NaN;
		}

		public int size() {
			return values.length;
		}

		/**
		 * @return the repr, see {@link #repr}
		 */
		public int getRepr() {
			return repr;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MultiFloat(");
			builder.append(Arrays.toString(values));
			builder.append(",");
			builder.append(repr);
			builder.append(")");
			return builder.toString();
		}

	}

	boolean[] isValueInferreds(IRow row);

	/**
	 * children colors
	 *
	 * @return
	 */
	Color[] getColors();

	/**
	 * number of children
	 *
	 * @return
	 */
	int size();

	/**
	 * returns a selected child
	 *
	 * @param index
	 * @return
	 */
	ARankColumnModel get(int index);

}
