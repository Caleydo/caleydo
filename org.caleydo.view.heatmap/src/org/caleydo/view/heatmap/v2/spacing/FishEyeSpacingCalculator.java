/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2.spacing;

import java.util.BitSet;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.ISpacingStrategy;
import org.caleydo.view.heatmap.v2.spacing.UniformSpacingCalculator.UniformSpacingImpl;

/**
 * fish eye {@link ISpacingStrategy}
 *
 * @author Samuel Gratzl
 *
 */
public class FishEyeSpacingCalculator implements ISpacingStrategy {

	private final float minSelectionSize;

	/**
	 * @param minSelectionSize
	 */
	public FishEyeSpacingCalculator(float minSelectionSize) {
		this.minSelectionSize = minSelectionSize;
	}

	@Override
	public ISpacingLayout apply(Perspective perspective, SelectionManager selectionManager, boolean hideHidden,
			float size) {
		final VirtualArray va = perspective.getVirtualArray();
		int nrRecordElements = perspective.getVirtualArray().size();
		if (hideHidden) { // remove hidden
			nrRecordElements -= selectionManager.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		float fieldSize = size / nrRecordElements;

		if (fieldSize >= minSelectionSize) { // we have at least the min selection size can use uniform
			return new UniformSpacingImpl(fieldSize);
		}
		final BitSet selectedIndices = getSelectedIndices(selectionManager, va);
		final int selected = selectedIndices.cardinality();

		if (selectedIndices.isEmpty()) //nothing selected
			return new UniformSpacingImpl(fieldSize);

		float[] positions = new float[nrRecordElements];
		float lastSize = 0;

		final int spread = computeSpread(size);
		if (selected * minSelectionSize > size) { // too many selected just scale them and show them
			lastSize = justSelections(nrRecordElements, selectedIndices, positions, size);
		} else if (spread <= 0) {
			lastSize = selectionAndLinearRest(size, nrRecordElements, selectedIndices, selected, positions);
		} else {
			lastSize = fishEyed(size, nrRecordElements, selectedIndices, selected, positions, spread);
		}

		return new DeterminedSpacingImpl(positions, lastSize);
	}

	@Override
	public float minSize(int count, boolean isShowingLabels) {
		return count * (isShowingLabels ? 16 : 1) * 0.5f; // scale down as we have fish eye
	}

	/**
	 * real fish eye
	 *
	 * @return
	 */
	private float fishEyed(final float size, final int nrRecordElements, final BitSet selectedIndices,
			final int selected,
			float[] positions, final int spread) {
		// the elements that are spreaded
		BitSet spreadSelected = toSpreadedBitSet(nrRecordElements, selectedIndices, spread);
		final int spreadSize = spreadSelected.cardinality();

		// TODO correctly determine
		float fieldSize = (size - selected * minSelectionSize) / (nrRecordElements - selected); // first guess
		float level1Size = (minSelectionSize + fieldSize) * 0.5f;
		fieldSize = (size - selected * minSelectionSize - spreadSize * level1Size)
				/ (nrRecordElements - selected - spreadSize);
		if (fieldSize < 0) { // corner case
			fieldSize = 0;
			level1Size = (size - selected * minSelectionSize) / (spreadSize);
		}

		float acc = 0;
		float lastSize = 0;
		for (int i = 0; i < nrRecordElements; ++i) {
			positions[i] = acc;
			lastSize = selectedIndices.get(i) ? minSelectionSize : spreadSelected.get(i) ? level1Size : fieldSize;
			acc += lastSize;
		}

		return lastSize;
	}

	private static BitSet toSpreadedBitSet(int nrRecordElements, final BitSet selectedIndices, final int spread) {
		BitSet spreadSelected = new BitSet(selectedIndices.size());
		for (int i = selectedIndices.nextSetBit(0); i >= 0; i = selectedIndices.nextSetBit(i+1)) {
			for (int j = Math.max(0, i - spread); j <= Math.min(i + spread, nrRecordElements - 1); ++j)
				spreadSelected.set(j);
		}
		spreadSelected.andNot(selectedIndices); // clear the primary ones
		return spreadSelected;
	}

	/**
	 * render the selection with its minsize and the rest the remaining space linearly
	 *
	 * @return
	 */
	private float selectionAndLinearRest(float size, int nrRecordElements, final BitSet selectedIndices,
			final int selected, float[] positions) {
		//just the selections with the min size else the rest
		float fieldSize = (size - selected * minSelectionSize) / (nrRecordElements - selected);
		float acc = 0;
		float lastSize = 0;
		for (int i = 0; i < nrRecordElements; ++i) {
			positions[i] = acc;
			lastSize = selectedIndices.get(i) ? minSelectionSize : fieldSize;
			acc += lastSize;
		}
		return lastSize;
	}

	/**
	 * render just the selections uniformly and the rest ignore
	 *
	 * @return
	 */
	private static float justSelections(int nrRecordElements, final BitSet selectedIndices, float[] positions,
			float size) {
		float acc = 0;
		float fieldSize = size / selectedIndices.cardinality();
		float lastSize = 0;
		for (int i = 0; i < nrRecordElements; ++i) {
			positions[i] = acc;
			lastSize = selectedIndices.get(i) ? fieldSize : 0;
			acc += lastSize;
		}
		return lastSize;
	}

	private int computeSpread(float size) {
		return (int) (size / (minSelectionSize * 3));
	}

	/**
	 * returns a bitset containing the selected indices
	 * @param selectionManager
	 * @param va
	 * @return
	 */
	private BitSet getSelectedIndices(SelectionManager selectionManager, VirtualArray va) {
		BitSet selectedIndices = new BitSet();
		for (Integer selected : selectionManager.getElements(SelectionType.SELECTION)) {
			if (selectionManager.checkStatus(GLHeatMap.SELECTION_HIDDEN, selected))
				continue;
			int i = va.indexOf(selected);
			if (i < 0)
				continue;
			selectedIndices.set(i);
		}
		return selectedIndices;
	}

	static final class DeterminedSpacingImpl implements ISpacingLayout {
		private final float[] positions;
		private final float lastSize;

		DeterminedSpacingImpl(float[] positions, float lastSize) {
			this.positions = positions;
			this.lastSize = lastSize;
		}

		@Override
		public float getPosition(int index) {
			if (index < 0 || index >= positions.length)
				return 0;
			return positions[index];
		}

		@Override
		public float getSize(int index) {
			if (index < 0 || index >= positions.length)
				return 0;
			if (index == positions.length - 1)
				return lastSize;
			return positions[index + 1] - positions[index];
		}

		@Override
		public int getIndex(float position) {
			for (int i = 0; i < positions.length; ++i) {
				if (position < positions[i])
					return i - 1;
			}
			if (position < (positions[positions.length - 1] + lastSize))
				return positions.length - 1;
			return -1;
		}
	}
}
