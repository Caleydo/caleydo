package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public class HeatMapLayoutDetailViewRight extends AHeatMapLayoutDetailView {

	public HeatMapLayoutDetailViewRight(
			RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
	}

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth(),
				positionY + getDendrogramBottomSpacing(), 0.0f);
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth()
				+ getOverviewHeatMapWidth(), positionY
				+ getDendrogramBottomSpacing(), 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth(),
				positionY + getDendrogramBottomSpacing(), 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return positionX + getDetailWidth() + getGapWidth()
				+ getOverviewHeatMapWidth() + getOverviewGroupBarWidth();
	}

	@Override
	public EPickingType getGroupPickingType() {
		return EPickingType.COMPARE_RIGHT_GROUP_SELECTION;
	}

	@Override
	public EPickingType getHeatMapPickingType() {
		return EPickingType.COMPARE_RIGHT_EMBEDDED_VIEW_SELECTION;
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {

		float rightSpacing = Math.max(getDendrogramLineWidth()
				+ getDendrogramButtonWidth(), getDendrogramLineSpacing());

		return new Vec3f(positionX + totalWidth - textWidth
				- getCaptionLabelHorizontalSpacing() - rightSpacing, positionY
				+ getDendrogramBottomSpacing() + getOverviewHeight()
				+ getCaptionLabelVerticalSpacing(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramButtonPosition() {
		return new Vec3f(positionX + totalWidth - getDendrogramLineWidth()
				- getDendrogramButtonWidth(), positionY
				+ getDendrogramBottomSpacing() + getOverviewHeight(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramLinePosition() {
		return new Vec3f(positionX + totalWidth - getDendrogramLineWidth(),
				positionY + getDendrogramBottomSpacing(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramPosition() {
		return new Vec3f(positionX + totalWidth - getDendrogramLineWidth()
				- getDendrogramWidth(), positionY
				+ getDendrogramBottomSpacing(), 0.0f);
	}
}
