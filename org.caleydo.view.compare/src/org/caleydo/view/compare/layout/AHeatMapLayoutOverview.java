package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.ERenderCommandType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public abstract class AHeatMapLayoutOverview extends AHeatMapLayout {

	public AHeatMapLayoutOverview(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_HEATMAP));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.CAPTION_LABEL));
	}

	@Override
	public Vec3f getDetailPosition() {
		return null;
	}

	@Override
	public EPickingType getGroupPickingType() {
		return null;
	}

	@Override
	public EPickingType getHeatMapPickingType() {
		return null;
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return null;
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return 0;
	}

}
