package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

public class HeatMapLayoutRight extends HeatMapLayout {

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth(),
				positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth()
				+ getOverviewHeatmapWidth(), positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth(),
				positionY, 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return positionX + getDetailWidth() + getGapWidth()
				+ getOverviewHeatmapWidth() + getOverviewGroupWidth();
	}

}
