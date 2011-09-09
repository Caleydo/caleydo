package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class NormalSpacingCalculator extends ASpacingCalculator {

	private float normalFieldHeight;

	public NormalSpacingCalculator(GLHeatMap heatMap, float y, float contentElements) {
		super(heatMap, y, contentElements);
	}

	@Override
	public void calculateFieldHeights() {

		normalFieldHeight = y / recordElements;
	}

	@Override
	public float getFieldHeight(int recordID) {
		return normalFieldHeight;
	}
}
