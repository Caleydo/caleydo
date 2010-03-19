package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.ERenderCommandType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.ComparerDetailTemplate;

public abstract class AHeatMapLayoutOverview extends AHeatMapLayout {

	protected static float CAPTION_LABEL_HEIGHT_PORTION = 0.03f;
	protected static float CAPTION_LABEL_HORIZONTAL_SPACING_PORTION = 0.03f;
	protected static float CAPTION_LABEL_VERTICAL_SPACING_PORTION = 0.01f;
	protected static float OVERVIEW_HEIGHT_PORTION = 0.95f;

	public AHeatMapLayoutOverview(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_HEATMAP));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.CAPTION_LABEL));
	}

	@Override
	public Vec3f getDetailPosition() {
		return new Vec3f(0, 0, 0);
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
		return new Vec3f(0, 0, 0);
	}

	@Override
	public Vec3f getOverviewPosition() {
		return new Vec3f(positionX, positionY, 0.0f);
	}

	@Override
	public float getOverviewSliderPositionX() {
		return 0;
	}

	@Override
	public Vec3f getDetailHeatMapPosition(int heatMapID) {
		return new Vec3f(0, 0, 0);
	}

	@Override
	public void calculateDrawingParameters() {
	}

	public float getTotalOverviewWidth() {
		return getOverviewHeatmapWidth();
	}

	public float getGapWidth() {
		return 0;
	}

	public float getDetailWidth() {
		return 0;
	}

	public float getOverviewHeight() {
		return totalHeight * OVERVIEW_HEIGHT_PORTION;
	}

	public float getDetailHeight() {
		return 0;
	}

	public float getOverviewGroupWidth() {
		return 0;
	}

	public float getOverviewHeatmapWidth() {
		if (numTotalExperiments == 0)
			return totalWidth;
		float heatOverviewHeatMapWidth = (totalSpaceForAllHeatMapWrappers / (float) numTotalExperiments)
				* (float) numExperiments;
		if (heatOverviewHeatMapWidth > totalWidth
				|| heatOverviewHeatMapWidth < 0)
			return totalWidth;
		return heatOverviewHeatMapWidth;
	}

	public float getOverviewSliderWidth() {
		return 0;
	}

	public float getOverviewMaxSliderHeight() {
		return 0;
	}

	public float getOverviewMaxSliderPositionY() {
		return 0;
	}

	public float getOverviewMinSliderPositionY() {
		return 0;
	}

	public float getDetailHeatMapHeight(int heatMapID) {
		return 0;
	}

	public float getDetailHeatMapGapHeight() {
		return 0;
	}

	public float getCaptionLabelWidth() {
		return totalWidth;
	}

	public float getCaptionLabelHeight() {
		return totalHeight * CAPTION_LABEL_HEIGHT_PORTION;
	}

	public float getCaptionLabelHorizontalSpacing() {
		return totalWidth * CAPTION_LABEL_HORIZONTAL_SPACING_PORTION;
	}

	public float getCaptionLabelVerticalSpacing() {
		return totalHeight * CAPTION_LABEL_VERTICAL_SPACING_PORTION;
	}

	public void createDetailHeatMapTemplate(GLHeatMap heatMap) {
	}

	public ComparerDetailTemplate getHeatMapTemplate() {
		return null;
	}

	@Override
	public Vec3f getDendrogramButtonPosition() {
		return new Vec3f(0, 0, 0);
	}

	@Override
	public float getDendrogramButtonHeight() {
		return 0;
	}

	@Override
	public float getDendrogramButtonWidth() {
		return 0;
	}

}
