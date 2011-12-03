package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public abstract class ASpacingCalculator {

	protected GLHeatMap heatMap;
	protected float y;
	protected float recordElements;
	protected int minSelectedFieldHeight;

	public ASpacingCalculator(GLHeatMap heatMap, float y, float recordElements) {
		this.heatMap = heatMap;
		this.y = y;
		this.recordElements = recordElements;
	}

	public abstract void calculateFieldHeights();

	public abstract float getFieldHeight(int recordID);

	public void setMinSelectedFieldHeight(int minSelectedFieldHeight) {
		this.minSelectedFieldHeight = minSelectedFieldHeight;
	}

}
