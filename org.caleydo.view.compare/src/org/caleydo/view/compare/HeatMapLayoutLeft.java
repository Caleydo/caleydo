package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

public class HeatMapLayoutLeft extends HeatMapLayout {

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(positionX + getTotalOverviewWidth() + getGapWidth(),
				positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + getOverviewGroupWidth(), positionY, 0.0f);
	}

}
