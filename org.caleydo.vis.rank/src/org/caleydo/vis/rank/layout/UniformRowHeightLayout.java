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

import java.util.BitSet;

import org.caleydo.vis.rank.model.ColumnRanker;
import org.caleydo.vis.rank.ui.RenderStyle;

/**
 * uniform {@link IRowHeightLayout} where every row has the same height
 *
 * @author Samuel Gratzl
 *
 */
class UniformRowHeightLayout implements IRowHeightLayout {
	private static final float ROW_HEIGHT = 20;

	UniformRowHeightLayout() {
	}

	@Override
	public IRowLayoutInstance layout(ColumnRanker ranker, float h, int size, int offset, boolean forceOffset,
			IRowLayoutInstance previous) {
		final int selectedRank = ranker.getSelectedRank();
		final int selectedIndex = selectedRank < 0 ? -1 : ranker.get(selectedRank).getIndex();

		final int visibleRows = (int) Math.round(Math.floor((h - 5) / ROW_HEIGHT));
		int[] order = ranker.getOrder();
		final int numRows = order.length;

		// clamping
		if (visibleRows >= numRows)
			offset = 0;
		if (offset >= (numRows - visibleRows))
			offset = numRows - visibleRows;

		// let the selected index be visible
		if (selectedRank >= 0 && !forceOffset) {
			if (selectedRank < offset)
				offset = selectedRank;
			if (selectedRank >= (offset + visibleRows))
				offset = selectedRank - visibleRows + 1;
		}
		if (offset < 0)
			offset = 0;

		float y = 0;
		int numVisibles = 0;
		BitSet unused = new BitSet(size);
		unused.set(0, size);
		for (int r = 0; r < order.length; ++r) {
			int rowIndex = order[r];
			unused.clear(rowIndex);
			if (r < offset)
				continue;
			float hr = ROW_HEIGHT;
			y += hr;
			numVisibles++;
			if ((y + hr + 5) >= h)
				break;
		}
		return new UniformRowLayoutInstance(order, offset, numVisibles, unused, h, selectedIndex);
	}

	@Override
	public String getIcon() {
		return RenderStyle.ICON_ALIGN_UNIFORM;
	}

	private static class UniformRowLayoutInstance extends ARowLayoutInstance {
		private final int[] order;
		private final BitSet unused;
		private final float h;

		public UniformRowLayoutInstance(int[] order, int offset, int numVisibles, BitSet unused, float h,
				int selectedIndex) {
			super(offset, numVisibles, selectedIndex);
			this.order = order;
			this.unused = unused;
			this.h = h;
		}

		@Override
		public int getSize() {
			return order.length;
		}

		@Override
		public void layout(IRowSetter setter, float x, float w) {
			float y = 0;
			// first free the elements
			for (int r = 0; r < offset; ++r)
				setter.set(order[r], x, 0, w, 0, false);
			for (int i = unused.nextSetBit(0); i >= 0; i = unused.nextSetBit(i + 1)) {
				setter.set(i, x, h, w, 0, false);
			}
			// alloc again
			for (int r = offset; r < (offset + numVisibles); ++r) {
				int rowIndex = order[r];
				float hr = ROW_HEIGHT;
				setter.set(rowIndex, x, y, w, hr, rowIndex == selectedIndex);
				y += hr;
			}
		}
	}
}
