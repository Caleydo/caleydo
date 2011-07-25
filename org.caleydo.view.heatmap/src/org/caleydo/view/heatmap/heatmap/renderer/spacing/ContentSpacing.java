package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.ArrayList;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Spacing information / calculation for heat map
 * 
 * @author Alexander Lex
 * 
 */
public class ContentSpacing {

	private float fieldWidth;

	private ASpacingCalculator spacingCalculator;

	private boolean useFishEye = false;

	/** yDistances of heat map elements are initialized during the first rendering step of the heat map */
	private ArrayList<Float> yDistances;
	private GLHeatMap heatMap;

	public ContentSpacing(GLHeatMap heatMap) {
		this.heatMap = heatMap;
		yDistances = new ArrayList<Float>();
	}

	public void calculateContentSpacing(int contentElements, int dimensionElements,
			float x, float y, float minSelectedFieldHeight) {
		fieldWidth = x / dimensionElements;

		if (y / contentElements > minSelectedFieldHeight
				|| heatMap.getZoomedElements().size() == 0) {

			spacingCalculator = new NormalSpacingCalculator(heatMap, y, contentElements);
			useFishEye = false;

		} else {
			useFishEye = true;
			// spacingCalculator = new SelectedLargerSpacingCalculator(heatMap,
			// y,
			// contentElements);
			spacingCalculator = new FishEyeSpacingCalculator(heatMap, y, contentElements, minSelectedFieldHeight);

		}
		spacingCalculator.calculateFieldHeights();
	}

	public float getFieldHeight(int recordID) {
		return spacingCalculator.getFieldHeight(recordID);
	}

	public float getFieldWidth() {
		return fieldWidth;
	}

	public boolean isUseFishEye() {
		return useFishEye;
	}

	public ArrayList<Float> getYDistances() {
		return yDistances;
	}
}
