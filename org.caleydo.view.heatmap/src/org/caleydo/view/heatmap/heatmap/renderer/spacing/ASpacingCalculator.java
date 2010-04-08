package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public abstract class ASpacingCalculator {

	protected GLHeatMap heatMap;
	protected float y;
	protected float contentElements;

	public ASpacingCalculator(GLHeatMap heatMap, float y, float contentElements) {
		this.heatMap = heatMap;
		this.y = y;
		this.contentElements = contentElements;
	}

	public abstract void calculateFieldHeights();

	public abstract float getFieldHeight(int contentID);

}
