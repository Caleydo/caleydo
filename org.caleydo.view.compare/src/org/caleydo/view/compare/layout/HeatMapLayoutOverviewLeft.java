package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public class HeatMapLayoutOverviewLeft extends AHeatMapLayoutOverview {

	public HeatMapLayoutOverviewLeft(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {
		return new Vec3f(positionX + getCaptionLabelHorizontalSpacing(),
				positionY + getOverviewHeight()
						+ getCaptionLabelVerticalSpacing(), 0.0f);
	}

}
