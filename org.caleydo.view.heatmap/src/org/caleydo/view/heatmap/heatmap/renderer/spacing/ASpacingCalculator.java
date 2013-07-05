/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public abstract class ASpacingCalculator {

	protected final GLHeatMap heatMap;
	protected final float height;
	protected final int numElements;
	protected int minSelectedFieldHeight;

	public ASpacingCalculator(GLHeatMap heatMap, float y, int numElements) {
		this.heatMap = heatMap;
		this.height = y;
		this.numElements = numElements;
	}

	public abstract void calculateFieldHeights();

	public abstract float getFieldHeight(int recordID);

	public void setMinSelectedFieldHeight(int minSelectedFieldHeight) {
		this.minSelectedFieldHeight = minSelectedFieldHeight;
	}

}
