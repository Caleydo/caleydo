package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.ArrayList;

import org.caleydo.view.heatmap.heatmap.GLHeatMap;

/**
 * Spacing information / calculation for heat map
 * 
 * @author Alexander Lex
 * 
 */
public class RecordSpacing {

	private float fieldWidth;

	private ASpacingCalculator spacingCalculator;

	private boolean useFishEye = false;

	/**
	 * yDistances of heat map elements are initialized during the first
	 * rendering step of the heat map
	 */
	private ArrayList<Float> yDistances;
	private GLHeatMap heatMap;

	public RecordSpacing(GLHeatMap heatMap) {
		this.heatMap = heatMap;
		yDistances = new ArrayList<Float>();
	}

	public void calculateRecordSpacing(int recordElements, int dimensionElements,
			float x, float y, float minSelectedFieldHeight) {

		fieldWidth = x / dimensionElements;

		if (y / recordElements > minSelectedFieldHeight
				|| heatMap.getZoomedElements().size() == 0) {

			spacingCalculator = new NormalSpacingCalculator(heatMap, y, recordElements);
			useFishEye = false;

		} else {
			useFishEye = true;
			// spacingCalculator = new SelectedLargerSpacingCalculator(heatMap,
			// y,
			// contentElements);
			spacingCalculator = new FishEyeSpacingCalculator(heatMap, y, recordElements,
					minSelectedFieldHeight);

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
