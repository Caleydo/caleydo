/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.layout;

import java.net.URL;
import java.util.BitSet;

import org.caleydo.vis.lineup.model.ColumnRanker;
import org.caleydo.vis.lineup.ui.RenderStyle;

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
	public URL getIcon() {
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
	public IRowLayoutInstance layout(ColumnRanker ranker, float h, int size, int offset, boolean forceOffset,
			IRowLayoutInstance previous) {
		final int selectedIndex = ranker.get(ranker.getSelectedRank()) == null ? -1 : ranker.get(
				ranker.getSelectedRank()).getIndex();
		int selectedRank = selectedIndex < 0 ? -1 : ranker.getSelectedRank();
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

		if (offset < 0)
			offset = 0;
		if (offset > numRows)
			offset = numRows - 1;

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

		return new FishEyeImpl(order, offset, numVisibles, selectedRank, unused, h, selectedIndex);
	}

	class FishEyeImpl extends ARowLayoutInstance {
		private final int[] order;
		private final BitSet unused;
		private final float h;
		private final int base;

		public FishEyeImpl(int[] order, int offset, int numVisibles, int base, BitSet unused, float h, int selectedIndex) {
			super(offset, numVisibles, selectedIndex);
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
		public void layout(IRowSetter setter, float x, float w) {
			float y = 0;
			for (int r = 0; r < offset; ++r)
				setter.set(order[r], x, 0, w, 0, false);
			for (int i = unused.nextSetBit(0); i >= 0; i = unused.nextSetBit(i + 1)) {
				setter.set(i, x, h, w, 0, false);
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

