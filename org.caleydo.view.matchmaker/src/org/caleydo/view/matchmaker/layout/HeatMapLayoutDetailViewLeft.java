package org.caleydo.view.matchmaker.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

public class HeatMapLayoutDetailViewLeft extends AHeatMapLayoutDetailView {

	public HeatMapLayoutDetailViewLeft(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);

	}

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing() + getOverviewWidth()
				+ getGapWidth(), positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing(), positionY
				+ getDendrogramBottomSpacing(), 0.0f);
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing()
				+ getOverviewSliderWidth(), positionY + getDendrogramBottomSpacing(),
				0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + getDendrogramLineSpacing()
				+ getOverviewSliderWidth() + getOverviewGroupBarWidth(), positionY
				+ getDendrogramBottomSpacing(), 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return positionX + getDendrogramLineSpacing();
	}

	@Override
	public PickingType getHeatMapPickingType() {
		return PickingType.COMPARE_LEFT_EMBEDDED_VIEW_SELECTION;
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {

		float leftSpacing = Math.max(getDendrogramLineWidth()
				+ getDendrogramButtonWidth(), getDendrogramLineSpacing());

		return new Vec3f(positionX + leftSpacing + getCaptionLabelHorizontalSpacing(),
				positionY + getDendrogramBottomSpacing() + getOverviewHeight()
						+ getCaptionLabelVerticalSpacing(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramButtonPosition() {
		return new Vec3f(positionX + getDendrogramLineWidth(), positionY
				+ getDendrogramBottomSpacing() + getOverviewHeight()
				+ getCaptionLabelVerticalSpacing() / 2.0f, 0.0f);
	}

	@Override
	public Vec3f getDendrogramLinePosition() {
		return new Vec3f(positionX, positionY + getDendrogramBottomSpacing(), 0.0f);
	}

	@Override
	public Vec3f getDendrogramPosition() {
		return new Vec3f(positionX + getDendrogramLineWidth(), positionY
				+ getDendrogramBottomSpacing(), -1.0f);
	}

}
