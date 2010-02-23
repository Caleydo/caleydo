package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

public abstract class HeatMapLayout {

	protected static float OVERVIEW_WIDTH_PORTION = 0.2f;
	protected static float DETAIL_WIDTH_PORTION = 0.5f;
	protected static float GAP_PORTION = 0.3f;

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

	public float getOverviewWidth() {
		return totalWidth * OVERVIEW_WIDTH_PORTION;
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

	public abstract Vec3f getOverviewPosition();

	public abstract Vec3f getDetailPosition();

}
