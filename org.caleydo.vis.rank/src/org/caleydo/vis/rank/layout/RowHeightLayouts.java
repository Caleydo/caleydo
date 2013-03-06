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
package org.caleydo.vis.rank.layout;

import java.util.Arrays;

import org.caleydo.vis.rank.model.MaxCompositeRankColumnModel;

/**
 * row height layouts determine on the one hand the row heights of items and will also be used for layouting the rows of
 * a {@link MaxCompositeRankColumnModel}
 *
 * @author Samuel Gratzl
 *
 */
public class RowHeightLayouts {
	public interface IRowHeightLayout {
		/**
		 * returns a computed list of row heights, when it is smaller than the number of rows, the remaining rows will
		 * be hidden
		 *
		 * @param numRows
		 *            the number of visible rows
		 * @param selectedRowIndex
		 *            the index of the selected one
		 * @param h
		 *            the available height
		 * @return
		 */
		float[] compute(int numRows, int selectedRowIndex, float h);

		String getIcon();
	}

	public static final IRowHeightLayout UNIFORM = new UniformRowHeightLayout();

	public static final IRowHeightLayout FISH_EYE = new FishEyeRowHeightLayout();

	public static final IRowHeightLayout JUST_SELECTED = new IRowHeightLayout() {
		@Override
		public float[] compute(int numRows, int selectedRowIndex, float h) {
			if (selectedRowIndex < 0)
				return new float[0];
			float[] r = new float[selectedRowIndex + 1];
			Arrays.fill(r, 0);
			r[selectedRowIndex] = h;
			return r;
		}

		@Override
		public String getIcon() {
			return null;
		}
	};

	public static final IRowHeightLayout HINTS = new IRowHeightLayout() {
		@Override
		public float[] compute(int numRows, int selectedRowIndex, float h) {
			if (h <= (numRows + 2) || selectedRowIndex < 0)
				return JUST_SELECTED.compute(numRows, selectedRowIndex, h);

			float[] r = new float[numRows];
			float delta = h / (numRows + 2);
			Arrays.fill(r, delta);
			r[selectedRowIndex] = delta * 3;
			return r;
		}

		@Override
		public String getIcon() {
			return null;
		}
	};
}
