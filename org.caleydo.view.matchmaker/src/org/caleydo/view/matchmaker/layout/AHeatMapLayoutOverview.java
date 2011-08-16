package org.caleydo.view.matchmaker.layout;

import gleem.linalg.Vec3f;

import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.MatchmakerDetailTemplate;
import org.caleydo.view.matchmaker.rendercommand.ERenderCommandType;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

public abstract class AHeatMapLayoutOverview extends AHeatMapLayout {

	protected static final float CAPTION_LABEL_HEIGHT_PORTION = 0.025f;
	protected static final float CAPTION_LABEL_HORIZONTAL_SPACING_PORTION = 0.03f;
	protected static final float CAPTION_LABEL_VERTICAL_SPACING_PORTION = 0.01f;
	protected static final float OVERVIEW_HEIGHT_PORTION = 0.955f;

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
	public PickingType getHeatMapPickingType() {
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

	@Override
	public float getOverviewWidth() {
		return getOverviewHeatMapWidth();
	}

	@Override
	public float getGapWidth() {
		return 0;
	}

	@Override
	public float getDetailWidth() {
		return 0;
	}

	@Override
	public float getOverviewHeight() {
		return totalHeight * OVERVIEW_HEIGHT_PORTION;
	}

	@Override
	public float getDetailHeight() {
		return 0;
	}

	@Override
	public float getOverviewGroupBarWidth() {
		return 0;
	}

	@Override
	public float getOverviewHeatMapWidth() {
		if (numTotalExperiments == 0)
			return totalWidth;
		float heatOverviewHeatMapWidth = (totalSpaceForAllHeatMapWrappers / (float) numTotalExperiments)
				* (float) numExperiments;
		if (heatOverviewHeatMapWidth > totalWidth || heatOverviewHeatMapWidth < 0)
			return totalWidth;
		return heatOverviewHeatMapWidth;
	}

	@Override
	public float getOverviewSliderWidth() {
		return 0;
	}

	@Override
	public float getOverviewMaxSliderHeight() {
		return 0;
	}

	@Override
	public float getOverviewMaxSliderPositionY() {
		return 0;
	}

	@Override
	public float getOverviewMinSliderPositionY() {
		return 0;
	}

	@Override
	public float getDetailHeatMapHeight(int heatMapID) {
		return 0;
	}

	@Override
	public float getCaptionLabelWidth() {
		return totalWidth;
	}

	@Override
	public float getCaptionLabelHeight() {
		return totalHeight * CAPTION_LABEL_HEIGHT_PORTION;
	}

	@Override
	public float getCaptionLabelHorizontalSpacing() {
		return totalWidth * CAPTION_LABEL_HORIZONTAL_SPACING_PORTION;
	}

	@Override
	public float getCaptionLabelVerticalSpacing() {
		return totalHeight * CAPTION_LABEL_VERTICAL_SPACING_PORTION;
	}

	public void createDetailHeatMapTemplate(GLHeatMap heatMap) {
	}

	public MatchmakerDetailTemplate getHeatMapTemplate() {
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

	@Override
	public Vec3f getDendrogramLinePosition() {
		return new Vec3f(0, 0, 0);
	}

	@Override
	public float getDendrogramLineHeight() {
		return 0;
	}

	@Override
	public float getDendrogramLineWidth() {
		return 0;
	}

	@Override
	public void useDendrogram(boolean useDendrogram) {

	}

	@Override
	public Vec3f getDendrogramPosition() {
		return new Vec3f(0, 0, 0);
	}

	@Override
	public float getDendrogramHeight() {
		return 0;
	}

	@Override
	public float getDendrogramWidth() {
		return 0;
	}

	@Override
	public boolean isDendrogramUsed() {
		return false;
	}
}
