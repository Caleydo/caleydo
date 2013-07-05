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
