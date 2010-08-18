package org.caleydo.view.matchmaker.layout;

import gleem.linalg.Vec3f;

import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

public class HeatMapLayoutOverviewLeft extends AHeatMapLayoutOverview {

	public HeatMapLayoutOverviewLeft(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {
		return new Vec3f(positionX + getCaptionLabelHorizontalSpacing(), positionY
				+ getOverviewHeight() + getCaptionLabelVerticalSpacing(), 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

}
