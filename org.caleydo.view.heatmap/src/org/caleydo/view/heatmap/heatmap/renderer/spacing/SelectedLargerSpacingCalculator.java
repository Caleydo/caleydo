/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class SelectedLargerSpacingCalculator extends ASpacingCalculator {

	private float selectedFieldHeight;
	private float normalFieldHeight;

	public SelectedLargerSpacingCalculator(GLHeatMap heatMap, float height, int numElements) {
		super(heatMap, height, numElements);
	}

	@Override
	public void calculateFieldHeights() {
		selectedFieldHeight = heatMap.getPixelGLConverter().getGLHeightForPixelHeight(
				minSelectedFieldHeight);
		int nrZoomedElements = heatMap.getZoomedElements().size();
		normalFieldHeight = (height - (nrZoomedElements * selectedFieldHeight))
				/ (numElements - nrZoomedElements);
	}

	@Override
	public float getFieldHeight(int recordID) {

		if (heatMap.getRecordSelectionManager().checkStatus(SelectionType.SELECTION,
				recordID)
				|| heatMap.getRecordSelectionManager().checkStatus(
						SelectionType.MOUSE_OVER, recordID)) {
			return selectedFieldHeight;
		}
		return normalFieldHeight;
	}

}
