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
package org.caleydo.view.heatmap;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.hierarchical.GLHierarchicalHeatMap;

/**
 * Heat Map render styles
 * 
 * @author Alexander Lex
 */

public class HeatMapRenderStyle extends GeneralRenderStyle {

	public static final float FIELD_Z = 0.001f;

	public static final float SELECTION_Z = 0.005f;

	public static final float MIN_FIELD_HEIGHT_FOR_CAPTION = 0.05f;

		public static final int LABEL_TEXT_MIN_SIZE = 50;

	public static final float[] BACKGROUND_COLOR = { 0.8f, 0.8f, 0.8f, 1 };
	public static final float[] DRAGGING_CURSOR_COLOR = { 0.2f, 0.2f, 0.2f, 1 };
	public static final float[] DENDROGRAM_BACKROUND = { 0.5f, 0.5f, 0.5f, 1 };
	public static final float CLUSTER_BORDERS_Z = 0.009f;
	public static final float BUTTON_Z = 0.01f;
	public static final float BACKGROUND_Z = -0.1f;

//	private float selectedFieldHeight = 5.0f;
//
//	private float normalFieldHeight = 0f;
//
//	private float fieldWidth = 0f;
//
//	private int iLevels = 1;
//
//	private int iNotSelectedLevel = 1000;

	private float fWidthLevel1 = 0.2f;
	private float fWidthLevel2 = 0.0f;
	private float fWidthLevel3 = 0.0f;
	private float fWidthClusterVisualization = 0.1f;
	private float fHeightExperimentDendrogram = 1.45f;
	private float fWidthGeneDendrogram = 1.6f;
	private float fSizeHeatmapArrow = 0.17f;

	// private ArrayList<FieldWidthElement> alFieldWidths;

	// private HashMap<Integer, Float> hashLevelToWidth;

	GLHeatMap heatMap;
	GLHierarchicalHeatMap hierarchicalHeatMap;

	// private boolean useFishEye = true;

	// public void setUseFishEye(boolean useFishEye) {
	// this.useFishEye = useFishEye;
	// }

	public HeatMapRenderStyle(GLHeatMap heatMap, ViewFrustum viewFrustum) {

		super(viewFrustum);

		this.heatMap = heatMap;

		// alFieldWidths = new ArrayList<FieldWidthElement>();

		// init fish eye
		// float fDelta = (selectedFieldHeight - normalFieldHeight) / (iLevels +
		// 1);
		// hashLevelToWidth = new HashMap<Integer, Float>();
		// hashLevelToWidth.put(iNotSelectedLevel, normalFieldHeight);
		// float fCurrentWidth = normalFieldHeight;
		// for (int iCount = -iLevels; iCount <= iLevels; iCount++) {
		// if (iCount < 0) {
		// fCurrentWidth += fDelta;
		// } else if (iCount == 0) {
		// fCurrentWidth = selectedFieldHeight;
		// } else {
		// fCurrentWidth -= fDelta;
		// }

		// hashLevelToWidth.put(iCount, fCurrentWidth);
		// }

	}

	public HeatMapRenderStyle(GLHierarchicalHeatMap hierarchicalHeatMap,
			ViewFrustum viewFrustum) {

		super(viewFrustum);

		this.hierarchicalHeatMap = hierarchicalHeatMap;

	}

	// /**
	// * Initializes or updates field sizes based on selections, virtual arrays
	// * etc. Call this every time something has changed.
	// */
	// public void updateFieldSizesWithFish() {
	// int numberSelected =
	// heatMap.getRecordSelectionManager().getNumberOfElements(
	// SelectionType.MOUSE_OVER);
	// numberSelected +=
	// heatMap.getRecordSelectionManager().getNumberOfElements(
	// SelectionType.SELECTION);
	//
	// int numberTotal = heatMap.getRecordVA().size();
	//
	// float selecteFieldHeightPercentage = SELECTED_FIELD_HEIGHT_PERCENTAGE;
	// if (numberSelected > 0 && SELECTED_FIELD_HEIGHT_PERCENTAGE *
	// numberSelected > 1) {
	// selecteFieldHeightPercentage = 1.0f / numberSelected;
	// }
	//
	// selectedFieldHeight = getRenderHeight() *
	// MAXIMUM_SELECTED_AREA_PERCENTAGE
	// * selecteFieldHeightPercentage;
	// normalFieldHeight = (getRenderHeight() - numberSelected *
	// selectedFieldHeight)
	// / (numberTotal - numberSelected);
	//
	// normalFieldHeight = normalFieldHeight > selectedFieldHeight ?
	// selectedFieldHeight
	// : normalFieldHeight;
	//
	// fieldWidth = getRenderWidth() / heatMap.getDimensionVA().size();
	//
	// }

	public float getHeightExperimentDendrogram() {
		return fHeightExperimentDendrogram;
	}

	// function called by HHM to set height of experiment dendrogram
	public void setHeightExperimentDendrogram(float fHeightExperimentDendrogram) {
		this.fHeightExperimentDendrogram = fHeightExperimentDendrogram;
	}

	public float getWidthGeneDendrogram() {
		return fWidthGeneDendrogram;
	}

	// function called by HHM to set width of gene dendrogram
	public void setWidthGeneDendrogram(float fWidthGeneDendrogram) {
		this.fWidthGeneDendrogram = fWidthGeneDendrogram;
	}

	public float getWidthClusterVisualization() {
		return fWidthClusterVisualization;
	}

	public float getWidthLevel1() {
		return fWidthLevel1;
	}

	public float getWidthLevel2() {
		fWidthLevel2 = hierarchicalHeatMap.getViewFrustum().getWidth() / 5;

		return fWidthLevel2;
	}

	public float getWidthLevel3() {
		return fWidthLevel3;
	}

	// function called by HHM to set width of embedded HM
	public void setWidthLevel3(float fWidthLevel3) {
		this.fWidthLevel3 = fWidthLevel3;
	}

	// public float getYCenter() {
	//
	// // TODO: this is only correct for 4 rows
	// return viewFrustum.getHeight() / 2;
	// }

	public float getXCenter() {

		return viewFrustum.getWidth() / 2;
	}

	public float getXSpacing() {
		return 0.4f;
	}

	public float getYSpacing() {
		return 0.3f;
	}

	// private float getRenderWidth() {
	//
	// if (heatMap.getDetailLevel() == DetailLevel.HIGH)
	// return viewFrustum.getWidth() - 2.4f * getXSpacing();
	// return viewFrustum.getWidth();
	// }
	//
	// public float getRenderHeight() {
	// if (heatMap.getDetailLevel() == DetailLevel.HIGH)
	// return viewFrustum.getHeight() - 2 * getYSpacing();
	// return viewFrustum.getHeight();
	//
	// }

	public float getSizeHeatmapArrow() {
		return fSizeHeatmapArrow;
	}
}
