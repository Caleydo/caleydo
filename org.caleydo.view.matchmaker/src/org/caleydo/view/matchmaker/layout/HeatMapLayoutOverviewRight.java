package org.caleydo.view.matchmaker.layout;

import gleem.linalg.Vec3f;

import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

public class HeatMapLayoutOverviewRight extends AHeatMapLayoutOverview {

	public HeatMapLayoutOverviewRight(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {
		return new Vec3f(positionX + totalWidth - textWidth
				- getCaptionLabelHorizontalSpacing(), positionY
				+ getOverviewHeight() + getCaptionLabelVerticalSpacing(), 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + (totalWidth - getOverviewHeatMapWidth()),
				positionY, 0.0f);
	}

}
