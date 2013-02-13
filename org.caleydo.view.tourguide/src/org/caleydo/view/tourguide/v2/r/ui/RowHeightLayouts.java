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
package org.caleydo.view.tourguide.v2.r.ui;

import java.util.Arrays;

/**
 * @author Samuel Gratzl
 *
 */
public class RowHeightLayouts {
	public interface IRowHeightLayout {
		float[] compute(int numRows, int selectedRowIndex, float h);
	}

	public static final IRowHeightLayout UNIFORM = new IRowHeightLayout() {
		@Override
		public float[] compute(int numRows, int selectedRowIndex, float h) {
			int visibleRows = (int) Math.round(Math.floor(h / 22));
			float[] r = new float[Math.min(numRows, visibleRows)];
			Arrays.fill(r, 20);
			return r;
		}
	};

	public static final IRowHeightLayout LINEAR = new IRowHeightLayout() {
		@Override
		public float[] compute(int numRows, int selectedRowIndex, float h) {
			float[] r = new float[numRows];
			float act = 22;
			float delta = -0.75f;
			float acc = 0;
			for (int i = 0; i < numRows; ++i) {
				r[i] = act;
				act += delta;
				if (act <= 3)
					delta = 0;
				if (i == selectedRowIndex) {
					r[i] = 30;
				}
				acc += r[i];
				if (acc >= h) {
					r = Arrays.copyOf(r, i + 1);
					break;
				}
			}
			if (selectedRowIndex >= 0 && selectedRowIndex < r.length) {
				r[selectedRowIndex] = 30;
			}
			return r;
		}
	};
}
