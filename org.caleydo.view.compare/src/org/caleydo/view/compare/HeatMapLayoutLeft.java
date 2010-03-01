package org.caleydo.view.compare;

import org.caleydo.core.manager.picking.EPickingType;

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
		return new Vec3f(positionX + getOverviewSliderWidth(), positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + getOverviewSliderWidth()
				+ getOverviewGroupWidth(), positionY, 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return positionX;
	}

	@Override
	public EPickingType getGroupPickingType() {
		return EPickingType.COMPARE_LEFT_GROUP_SELECTION;
	}

}
