/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
		float glSelectedFieldHeight = heatMap.getPixelGLConverter()
				.getGLHeightForPixelHeight(minSelectedFieldHeight);

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
