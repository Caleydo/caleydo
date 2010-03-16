package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.ERenderCommandType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.heatmap.heatmap.template.ComparerDetailTemplate;

public class HeatMapLayoutLeft extends AHeatMapLayout {

	public HeatMapLayoutLeft(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);

		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_GROUP_BAR));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_HEATMAP));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_SLIDER));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.CAPTION_LABEL));

		remoteRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.DETAIL_HEATMAPS));
		detailHeatMapTemplate = new ComparerDetailTemplate(true);
	}

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

	@Override
	public EPickingType getHeatMapPickingType() {
		return EPickingType.COMPARE_LEFT_EMBEDDED_VIEW_SELECTION;
	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {
		return new Vec3f(positionX + getCaptionLabelHorizontalSpacing(),
				positionY + getOverviewHeight()
						+ getCaptionLabelVerticalSpacing(), 0.0f);
	}

}
