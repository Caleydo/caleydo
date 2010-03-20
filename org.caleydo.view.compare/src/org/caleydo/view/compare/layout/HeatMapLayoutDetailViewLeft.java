package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public class HeatMapLayoutDetailViewLeft extends AHeatMapLayoutDetailView {

	public HeatMapLayoutDetailViewLeft(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);

	}

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing()
				+ getTotalOverviewWidth() + getGapWidth(), positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing(), positionY,
				0.0f);
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing()
				+ getOverviewSliderWidth(), positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing()
				+ getOverviewSliderWidth() + getOverviewGroupWidth(),
				positionY, 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return positionX + getDendrogramLineSpacing();
	}

	@Override
	public EPickingType getGroupPickingType() {
		return EPickingType.COMPARE_LEFT_GROUP_SELECTION;
	}

	@Override
	public EPickingType getHeatMapPickingType() {
		return EPickingType.COMPARE_LEFT_EMBEDDED_VIEW_SELECTION;
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {

		float leftSpacing = Math.max(getDendrogramLineWidth()
				+ getDendrogramButtonWidth(), getDendrogramLineSpacing());

		return new Vec3f(positionX + leftSpacing
				+ getCaptionLabelHorizontalSpacing(), positionY
				+ getOverviewHeight() + getCaptionLabelVerticalSpacing(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramButtonPosition() {
		return new Vec3f(positionX + getDendrogramLineWidth(), positionY
				+ getOverviewHeight(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramLinePosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getDendrogramPosition() {
		return new Vec3f(positionX + getDendrogramLineWidth(), positionY, 0.0f);
	}

}
