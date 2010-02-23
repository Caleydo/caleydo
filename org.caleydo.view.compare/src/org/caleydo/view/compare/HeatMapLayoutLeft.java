package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

public class HeatMapLayoutLeft extends HeatMapLayout {

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(positionX + getOverviewWidth() + getGapWidth(),
				positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

}
