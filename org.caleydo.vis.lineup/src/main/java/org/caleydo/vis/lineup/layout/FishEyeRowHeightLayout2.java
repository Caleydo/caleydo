/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.layout;

import gleem.linalg.Vec2f;

import java.net.URL;
import java.util.BitSet;

import org.caleydo.vis.lineup.model.ColumnRanker;
import org.caleydo.vis.lineup.ui.RenderStyle;

/**
 * @author Samuel Gratzl
 *
 */
class FishEyeRowHeightLayout2 implements IRowHeightLayout {
	private static final float MAX_ROW_HEIGHT = 42;
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

	FishEyeRowHeightLayout2() {
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
		int selectedRank = ranker.getSelectedRank();
		final int selectedIndex = selectedRank < 0 ? -1 : ranker.get(selectedRank).getIndex();
		int[] order = ranker.getOrder();
		if (selectedRank < 0)
			selectedRank = 0;

		IntFloat tmp = fishEyeAdaption(h, offset, forceOffset, selectedRank, order,
				previous instanceof FishEyeImpl ? (FishEyeImpl) previous : null);
		offset = tmp.i;
		float ybase = tmp.f;
		float y = ybase;

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

		return new FishEyeImpl(order, offset, numVisibles, selectedRank, unused, h, selectedIndex, ybase);
	}

	private static class IntFloat {
		int i;
		float f;

		public IntFloat(int i, float f) {
			this.i = i;
			this.f = f;
		}
	}

	protected IntFloat fishEyeAdaption(float h, int offset, boolean forceOffset, int selectedRank, int[] order,
			FishEyeImpl previous) {
		final int numRows = order.length;
		offset = clamp(offset, 0, numRows - 1);
		float y = 0;
		if (forceOffset)
			return new IntFloat(offset, y);


		// let the selected index be visible
		if (selectedRank < offset) // out of view
			offset = selectedRank;
		else if (previous != null && (previous.getOffset() + previous.getNumVisibles()) <= selectedRank) { // out of
																											// view
			IntFloat rh = sumRows(h);
			offset = selectedRank - rh.i;
			y = h - rh.f;
		} else if (previous != null) {
			// now the tricky part: the selected row was visible
			// try to keep the selected row and the same position
			Vec2f hBounds = previous.getVerticalBounds(selectedRank);
			if ((hBounds.x() + MAX_ROW_HEIGHT + 5) > h) { // not enough space
				y = h - 5 - MAX_ROW_HEIGHT;
				offset = selectedRank;
				float hi = MAX_ROW_HEIGHT;
				for (int i = 0; offset > 0 && (y - hi) > 0; i++) {
					offset--;
					y -= hi;
					hi = rowHeight(i + 1);
				}
				// y = h - rh.f - MAX_ROW_HEIGHT - 5;
			} else {
				y = hBounds.x();
				int oi = -1;
				float yi = rowHeight(oi);
				while ((y - yi) > 0 && -oi <= selectedRank) {
					oi--;
					y -= yi;
					yi = rowHeight(oi);
				}
				oi++;
				offset = selectedRank + oi;
				if (offset <= 0)
					y = 0;
			}
		} else {
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

		return new IntFloat(clamp(offset, 0, numRows - 1), y);
	}

	/**
	 * @param h
	 */
	private IntFloat sumRows(float h) {
		h -= 5;
		float hi = 0;
		int rows = fishEyeHeights.length-1;
		while (rows > 0 && h < fishEyeHeights[rows])
			rows--;
		rows--;
		hi += fishEyeHeights[rows];
		if (rows == (fishEyeHeights.length-1)) {
			int mins = (int) Math.round(Math.floor((h - fishEyeHeights[rows]) / MIN_ROW_HEIGHT));
			hi += mins * MIN_ROW_HEIGHT;
			rows += mins;
		}
		return new IntFloat(rows, hi);
	}

	private static int clamp(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	class FishEyeImpl extends ARowLayoutInstance {
		private final int[] order;
		private final BitSet unused;
		private final float h;
		private final int base;
		private float ybase;

		public FishEyeImpl(int[] order, int offset, int numVisibles, int base, BitSet unused, float h,
				int selectedIndex, float ybase) {
			super(offset, numVisibles, selectedIndex);
			this.order = order;
			this.unused = unused;
			this.h = h;
			this.base = base;
			this.ybase = ybase;
		}

		@Override
		public int getSize() {
			return order.length;
		}

		public Vec2f getVerticalBounds(int rank) {
			float y = ybase;
			float h = 0;
			if (rank >= offset && (rank - offset) < numVisibles) {
				rank -= offset;
				for (int i = 0; i < rank; ++i) {// acc all before
					y += rowHeight(rank - i - 1 - base);
				}
				h = rowHeight(rank - base);
			}
			return new Vec2f(y, h);
		}

		@Override
		public void layout(IRowSetter setter, float x, float w) {
			float y = ybase;
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
