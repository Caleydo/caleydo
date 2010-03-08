package org.caleydo.view.compare;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.ERenderCommandType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import gleem.linalg.Vec3f;

public class HeatMapLayoutRight extends HeatMapLayout {

	public HeatMapLayoutRight(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
		
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_GROUP_BAR));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_HEATMAP));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_SLIDER));

		remoteRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.DETAIL_HEATMAPS));
	}

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth(),
				positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth()
				+ getOverviewHeatmapWidth(), positionY, 0.0f);
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return new Vec3f(positionX + getDetailWidth() + getGapWidth(),
				positionY, 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return positionX + getDetailWidth() + getGapWidth()
				+ getOverviewHeatmapWidth() + getOverviewGroupWidth();
	}

	@Override
	public EPickingType getGroupPickingType() {
		return EPickingType.COMPARE_RIGHT_GROUP_SELECTION;
	}

	@Override
	public EPickingType getHeatMapPickingType() {
		return EPickingType.COMPARE_RIGHT_EMBEDDED_VIEW_SELECTION;
	}

}
