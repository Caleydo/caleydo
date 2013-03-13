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
 * @author Samuel Gratzl
 *
 */
class FishEyeRowHeightLayout implements IRowHeightLayout {
	private static final float MAX_ROW_HEIGHT = 40;
	private static final float MIN_ROW_HEIGHT = 3;

	private static final float[] fishEyeHeights;

	static {
		fishEyeHeights = new float[26];
		fishEyeHeights[0] = MAX_ROW_HEIGHT;
		for (int i = 1; i < fishEyeHeights.length; ++i) {
			float ratio = i / (float) fishEyeHeights.length;
			float hi = 30 * (float) (1 - Math.sqrt(ratio));
			fishEyeHeights[i] = fishEyeHeights[i - 1] + MIN_ROW_HEIGHT + hi;
		}
	}

	FishEyeRowHeightLayout() {
	}

	@Override
	public String getIcon() {
		return RenderStyle.ICON_ALIGN_FISH;
	}

	private static float rowHeight(int distance) {
		distance = Math.abs(distance);
		float hr;
		if (distance == 0) {
			hr = fishEyeHeights[0];
		} else if (distance < fishEyeHeights.length) {
			hr = fishEyeHeights[distance] - fishEyeHeights[distance - 1];
		} else {
			hr = MIN_ROW_HEIGHT;
		}
		return hr;
	}

	@Override
	public IRowLayoutInstance layout(ColumnRanker ranker, float h, int size, int offset, boolean forceOffset) {
		int selectedRank = ranker.getSelectedRank();
		if (selectedRank < 0)
			selectedRank = 0;

		int[] order = ranker.getOrder();
		final int numRows = order.length;
		if (offset > numRows)
			offset = numRows - 1;

		// let the selected index be visible
		if (selectedRank >= 0 && !forceOffset) {
			if (selectedRank < offset) // before
				offset = selectedRank;
			else {
				float topSum = 5;
				if (selectedRank >= fishEyeHeights.length)
					topSum += MIN_ROW_HEIGHT * (fishEyeHeights.length - selectedRank);
				if (selectedRank > 0)
					topSum += fishEyeHeights[Math.min(selectedRank, fishEyeHeights.length - 1)];
				while (topSum > h) {// after
					offset++;
					topSum -= rowHeight(selectedRank - offset + 1); // TODO check
				}
			}
		}

		float y = 0;
		int numVisibles = 0;
		BitSet unused = new BitSet(size);
		unused.set(0, size);
		for (int r = 0; r < offset; ++r)
			unused.clear(order[r]);

		for (int r = offset; r < order.length; ++r) {
			unused.clear(order[r]);
			float hr = rowHeight(selectedRank - r);
			y += hr;
			numVisibles++;
			if ((y + rowHeight(selectedRank - r - 1) + 5) >= h)
				break;
		}

		return new FishEyeImpl(order, offset, numVisibles, selectedRank, unused, h);
	}

	class FishEyeImpl extends ARowLayoutInstance {
		private final int[] order;
		private final BitSet unused;
		private final float h;
		private final int base;

		public FishEyeImpl(int[] order, int offset, int numVisibles, int base, BitSet unused, float h) {
			super(offset, numVisibles);
			this.order = order;
			this.unused = unused;
			this.h = h;
			this.base = base;
		}

		@Override
		public int getSize() {
			return order.length;
		}

		@Override
		public void layout(IRowSetter setter, float x, float w, int selectedIndex) {
			float y = 0;
			for (int r = 0; r < offset; ++r)
				setter.set(order[r], x, 0, w, 0, order[r] == selectedIndex);
			for (int i = unused.nextSetBit(0); i >= 0; i = unused.nextSetBit(i + 1)) {
				setter.set(i, x, h, w, 0, i == selectedIndex);
			}
			for (int r = offset; r < (offset + numVisibles); ++r) {
				int rowIndex = order[r];
				float hr = rowHeight(base - r);
				setter.set(rowIndex, x, y, w, hr, rowIndex == selectedIndex);
				y += hr;
			}
		}

	}
}

