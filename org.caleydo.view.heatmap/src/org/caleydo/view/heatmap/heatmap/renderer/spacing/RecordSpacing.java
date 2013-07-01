/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer.spacing;

import java.util.ArrayList;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
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

	/**
	 * Calculates the spacing for the records in the heat map. Determines
	 * automatically whether a fish-eye should be used so that selected elements
	 * can be increased in size.
	 *
	 * @param nrRecordElements
	 *            the number of records
	 * @param nrDimensionElements
	 *            the number of dimensions
	 * @param width
	 *            the available width
	 * @param height
	 *            the available height
	 * @param minSelectedFieldHeight
	 *            how many pixels a selected value should have.
	 */
	public void calculateRecordSpacing(int nrRecordElements, int nrDimensionElements,
			float width, float height, int minSelectedFieldHeight) {

		fieldWidth = width / nrDimensionElements;
		final PixelGLConverter pixelGLConverter = heatMap.getPixelGLConverter();
		float glSelectedFieldHeight = pixelGLConverter.getGLHeightForPixelHeight(minSelectedFieldHeight);

		if (height / nrRecordElements > glSelectedFieldHeight
				|| heatMap.getZoomedElements().size() == 0) {

			spacingCalculator = new NormalSpacingCalculator(heatMap, height,
					nrRecordElements);
			useFishEye = false;

		} else {
			useFishEye = true;
			// spacingCalculator = new SelectedLargerSpacingCalculator(heatMap,
			// y,
			// contentElements);
			spacingCalculator = new FishEyeSpacingCalculator(heatMap, height,
					nrRecordElements, minSelectedFieldHeight);

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
