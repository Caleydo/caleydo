package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.ERenderCommandType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.ComparerDetailTemplate;

public class HeatMapLayoutRight extends AHeatMapLayout {

	public HeatMapLayoutRight(RenderCommandFactory renderCommandFactory) {
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

		detailHeatMapTemplate = new ComparerDetailTemplate(false);
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

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {
		return new Vec3f(positionX + totalWidth - textWidth
				- getCaptionLabelHorizontalSpacing(), positionY
				+ getOverviewHeight() + getCaptionLabelVerticalSpacing(), 0.0f);
	}

}
