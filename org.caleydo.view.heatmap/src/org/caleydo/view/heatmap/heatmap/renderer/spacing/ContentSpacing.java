package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.ArrayList;

import org.caleydo.view.heatmap.HeatMapRenderStyle;
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

	private ArrayList<Float> yDistances;
	private GLHeatMap heatMap;

	public ContentSpacing(GLHeatMap heatMap) {
		this.heatMap = heatMap;
		yDistances = new ArrayList<Float>();
	}

	public void calculateContentSpacing(int contentElements, int storageElements,
			float x, float y) {
		fieldWidth = x / storageElements;

		if (y / contentElements > HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT
				|| heatMap.getZoomedElements().size() == 0) {
			spacingCalculator = new NormalSpacingCalculator(heatMap, y, contentElements);
			useFishEye = false;

		} else {
			useFishEye = true;
			// spacingCalculator = new SelectedLargerSpacingCalculator(heatMap,
			// y,
			// contentElements);
			spacingCalculator = new FishEyeSpacingCalculator(heatMap, y, contentElements);

		}
		spacingCalculator.calculateFieldHeights();
	}

	public float getFieldHeight(int contentID) {
		return spacingCalculator.getFieldHeight(contentID);
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
