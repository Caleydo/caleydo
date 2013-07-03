/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class NormalSpacingCalculator extends ASpacingCalculator {

	private float normalFieldHeight;

	public NormalSpacingCalculator(GLHeatMap heatMap, float height, int numElements) {
		super(heatMap, height, numElements);
	}

	@Override
	public void calculateFieldHeights() {

		normalFieldHeight = height / numElements;
	}

	@Override
	public float getFieldHeight(int recordID) {
		return normalFieldHeight;
	}
}
