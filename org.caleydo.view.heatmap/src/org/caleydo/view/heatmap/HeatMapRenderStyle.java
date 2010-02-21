package org.caleydo.view.heatmap;

import java.util.HashMap;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

/**
 * Heat Map render styles
 * 
 * @author Alexander Lex
 */

public class HeatMapRenderStyle extends GeneralRenderStyle {

	public static final float FIELD_Z = 0.001f;

	public static final float SELECTION_Z = 0.005f;

	private static final float SELECTED_FIELD_WIDTH_PERCENTAGE = 0.1f;
	private static final float MAXIMUM_SELECTED_AREA_PERCENTAGE = 0.8f;
	public static final int LABEL_TEXT_MIN_SIZE = 50;

	public static final float[] BACKGROUND_COLOR = { 0.8f, 0.8f, 0.8f, 1 };
	public static final float[] DRAGGING_CURSOR_COLOR = { 0.2f, 0.2f, 0.2f, 1 };
	public static final float[] DENDROGRAM_BACKROUND = { 0.5f, 0.5f, 0.5f, 1 };
	public static final float CLUSTER_BORDERS_Z = 0.009f;
	public static final float BUTTON_Z = 0.01f;
	public static final float BACKGROUND_Z = -0.1f;

	private float fSelectedFieldWidth = 1.0f;

	private float fNormalFieldWidth = 0f;

	private float fFieldHeight = 0f;

	private int iLevels = 1;

	private int iNotSelectedLevel = 1000;

	private float fWidthLevel1 = 0.2f;
	private float fWidthLevel2 = 0.0f;
	private float fWidthLevel3 = 0.0f;
	private float fWidthClusterVisualization = 0.1f;
	private float fHeightExperimentDendrogram = 1.45f;
	private float fWidthGeneDendrogram = 1.6f;
	private float fSizeHeatmapArrow = 0.17f;

	// private ArrayList<FieldWidthElement> alFieldWidths;

	private HashMap<Integer, Float> hashLevelToWidth;

	GLHeatMap heatMap;
	GLHierarchicalHeatMap hierarchicalHeatMap;

	private boolean useFishEye = true;

	public void setUseFishEye(boolean useFishEye) {
		this.useFishEye = useFishEye;
	}

	public HeatMapRenderStyle(GLHeatMap heatMap, IViewFrustum viewFrustum) {

		super(viewFrustum);

		this.heatMap = heatMap;

		// alFieldWidths = new ArrayList<FieldWidthElement>();

		// init fish eye
		float fDelta = (fSelectedFieldWidth - fNormalFieldWidth)
				/ (iLevels + 1);
		hashLevelToWidth = new HashMap<Integer, Float>();
		hashLevelToWidth.put(iNotSelectedLevel, fNormalFieldWidth);
		float fCurrentWidth = fNormalFieldWidth;
		for (int iCount = -iLevels; iCount <= iLevels; iCount++) {
			if (iCount < 0) {
				fCurrentWidth += fDelta;
			} else if (iCount == 0) {
				fCurrentWidth = fSelectedFieldWidth;
			} else {
				fCurrentWidth -= fDelta;
			}

			hashLevelToWidth.put(iCount, fCurrentWidth);
		}

	}

	public HeatMapRenderStyle(GLHierarchicalHeatMap hierarchicalHeatMap,
			IViewFrustum viewFrustum) {

		super(viewFrustum);

		this.hierarchicalHeatMap = hierarchicalHeatMap;

	}

	public void updateFieldSizes() {
		if (useFishEye)
			updateFieldSizesWithFish();
		else
			updateFieldSizesnofish();
	}

	public void updateFieldSizesnofish() {
		// if (heatMap.renderStorageHorizontally()) {
		// fSelectedFieldWidth = getRenderWidth()
		// / heatMap.getContentVA().size();
		// fNormalFieldWidth = fSelectedFieldWidth;
		//
		// fFieldHeight = getRenderHeight() / heatMap.getStorageVA().size();
		// } else {
		fSelectedFieldWidth = getRenderHeight() / heatMap.getContentVA().size();
		fNormalFieldWidth = fSelectedFieldWidth;

		fFieldHeight = getRenderWidth() / heatMap.getStorageVA().size();
		// }
	}

	/**
	 * Initializes or updates field sizes based on selections, virtual arrays
	 * etc. Call this every time something has changed.
	 */
	public void updateFieldSizesWithFish() {
		int iNumberSelected = heatMap.getContentSelectionManager()
				.getNumberOfElements(SelectionType.MOUSE_OVER);
		iNumberSelected += heatMap.getContentSelectionManager()
				.getNumberOfElements(SelectionType.SELECTION);

		int iNumberTotal = heatMap.getContentVA().size();

		float fSelecteFieldWidthPercentage = SELECTED_FIELD_WIDTH_PERCENTAGE;
		if (iNumberSelected > 0
				&& SELECTED_FIELD_WIDTH_PERCENTAGE * iNumberSelected > 1) {
			fSelecteFieldWidthPercentage = 1.0f / iNumberSelected;
		}

		// if (heatMap.renderStorageHorizontally()) {
		//
		// fSelectedFieldWidth = getRenderWidth()
		// * MAXIMUM_SELECTED_AREA_PERCENTAGE
		// * fSelecteFieldWidthPercentage;
		//
		// fNormalFieldWidth = (getRenderWidth() - iNumberSelected
		// * fSelectedFieldWidth)
		// / (iNumberTotal - iNumberSelected);
		//
		// fFieldHeight = getRenderHeight() / heatMap.getStorageVA().size();
		// } else {

		fSelectedFieldWidth = getRenderHeight()
				* MAXIMUM_SELECTED_AREA_PERCENTAGE
				* fSelecteFieldWidthPercentage;
		fNormalFieldWidth = (getRenderHeight() - iNumberSelected
				* fSelectedFieldWidth)
				/ (iNumberTotal - iNumberSelected);
		// }
		fFieldHeight = getRenderWidth() / heatMap.getStorageVA().size();

		fNormalFieldWidth = fNormalFieldWidth > fSelectedFieldWidth ? fSelectedFieldWidth
				: fNormalFieldWidth;
	}

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

	public float getNormalFieldWidth() {

		return fNormalFieldWidth;
	}

	public float getSelectedFieldWidth() {
		return fSelectedFieldWidth;
	}

	public float getFieldHeight() {
		return fFieldHeight;
	}

	public float getYCenter() {

		// TODO: this is only correct for 4 rows
		return viewFrustum.getHeight() / 2;
	}

	public float getXCenter() {

		return viewFrustum.getWidth() / 2;
	}

	public float getXSpacing() {
		return 0.4f;
	}

	public float getYSpacing() {
		return 0.3f;
	}

	// public void setBRenderStorageHorizontally(boolean
	// bRenderStorageHorizontally)
	// {
	// this.bRenderStorageHorizontally = bRenderStorageHorizontally;
	// }

	private float getRenderWidth() {

		if (heatMap.getDetailLevel() == EDetailLevel.HIGH)
			return viewFrustum.getWidth() - 2.4f * getXSpacing();
		return viewFrustum.getWidth();
	}

	public float getRenderHeight() {
		if (heatMap.getDetailLevel() == EDetailLevel.HIGH)
			return viewFrustum.getHeight() - 2 * getYSpacing();
		return viewFrustum.getHeight();

	}

	public float getSizeHeatmapArrow() {
		return fSizeHeatmapArrow;
	}
}
