/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2.spacing;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.ISpacingStrategy;

/**
 * uniform spacing strategy implementation
 * 
 * @author Samuel Gratzl
 * 
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
	}

	@Override
	public ISpacingLayout apply(Perspective perspective, SelectionManager selectionManager, boolean hideHidden,
			float size) {
		int nrRecordElements = perspective.getVirtualArray().size();
		if (hideHidden) {
			nrRecordElements -= selectionManager.getNumberOfElements(GLHeatMap.SELECTION_HIDDEN);
		}

		final float fieldHeight = size / nrRecordElements;

		return new UniformSpacingImpl(fieldHeight);
	}
}
