package org.caleydo.view.compare;

import org.caleydo.core.manager.picking.EPickingType;

import gleem.linalg.Vec3f;

public abstract class HeatMapLayout {

	protected static float OVERVIEW_TOTAL_WIDTH_PORTION = 0.25f;
	protected static float OVERVIEW_GROUP_WIDTH_PORTION = 0.04f;
	protected static float OVERVIEW_HEATMAP_WIDTH_PORTION = 0.15f;
	protected static float OVERVIEW_SLIDER_WIDTH_PORTION = 0.06f;

	protected static float DETAIL_WIDTH_PORTION = 0.5f;
	protected static float DETAIL_HEATMAP_GAP_PORTION = 0.02f;
	protected static float GAP_PORTION = 0.25f;

	protected float totalWidth;
	protected float totalHeight;
	protected float positionX;
	protected float positionY;

	public void setLayoutParameters(float positionX, float positionY,
			float totalHeight, float totalWidth) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.totalHeight = totalHeight;
		this.totalWidth = totalWidth;
	}

	public float getTotalOverviewWidth() {
		return totalWidth * OVERVIEW_TOTAL_WIDTH_PORTION;
	}

	public float getGapWidth() {
		return totalWidth * GAP_PORTION;
	}

	public float getDetailWidth() {
		return totalWidth * DETAIL_WIDTH_PORTION;
	}

	public float getOverviewHeight() {
		return totalHeight;
	}

	public float getDetailHeight() {
		return totalHeight;
	}

	public float getOverviewGroupWidth() {
		return totalWidth * OVERVIEW_GROUP_WIDTH_PORTION;
	}

	public float getOverviewHeatmapWidth() {
		return totalWidth * OVERVIEW_HEATMAP_WIDTH_PORTION;
	}

	public float getOverviewSliderWidth() {
		return totalWidth * OVERVIEW_SLIDER_WIDTH_PORTION;
	}

	public float getOverviewMaxSliderHeight() {
		return totalHeight;
	}

	public float getOverviewMaxSliderPositionY() {
		return positionY + totalHeight;
	}

	public float getOverviewMinSliderPositionY() {
		return positionY;
	}

	public float getDetailHeatMapHeight(int numSamplesInHeatMap,
			int numTotalSamples, int numHeatMaps) {
		float spaceForHeatMaps = getDetailHeight()
				- (getDetailHeight() * DETAIL_HEATMAP_GAP_PORTION * (numHeatMaps - 1));
		return (spaceForHeatMaps / (float) numTotalSamples)
				* (float) numSamplesInHeatMap;
	}
	
	public float getDetailHeatMapGapHeight() {
		return getDetailHeight() * DETAIL_HEATMAP_GAP_PORTION;
	}

	public abstract Vec3f getOverviewPosition();

	public abstract Vec3f getOverviewGroupBarPosition();

	public abstract Vec3f getOverviewHeatMapPosition();

	public abstract Vec3f getDetailPosition();

	public abstract float getOverviewSliderPositionX();
	
	public abstract EPickingType getGroupPickingType();

}
