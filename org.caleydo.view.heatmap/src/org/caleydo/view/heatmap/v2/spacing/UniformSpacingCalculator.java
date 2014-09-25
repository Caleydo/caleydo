/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2.spacing;

import java.util.List;

import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.view.heatmap.v2.ISpacingStrategy;

/**
 * uniform spacing strategy implementation
 *
 * @author Samuel Gratzl
 */
public class UniformSpacingCalculator implements ISpacingStrategy {

	/**
	 * @author Samuel Gratzl
	 *
	 */
	static final class UniformSpacingImpl implements ISpacingLayout {
		private final float fieldHeight;

		UniformSpacingImpl(float fieldHeight) {
			this.fieldHeight = fieldHeight;
		}

		@Override
		public float getPosition(int index) {
			return index * fieldHeight;
		}

		@Override
		public float getSize(int index) {
			return fieldHeight;
		}

		@Override
		public int getIndex(float position) {
			float pos = position / fieldHeight;
			if (pos < 0)
				return -1;
			return (int) Math.round(Math.floor(pos));
		}

		@Override
		public boolean isUniform() {
			return true;
		}
	}

	@Override
	public ISpacingLayout apply(List<Integer> ids, SelectionManager selectionManager, float size) {
		int nrRecordElements = ids.size();
		final float fieldHeight = size / nrRecordElements;

		return new UniformSpacingImpl(fieldHeight);
	}

	@Override
	public float minSize(int count, boolean isShowingLabels) {
		return count * (isShowingLabels ? 16 : 1);
	}
}
