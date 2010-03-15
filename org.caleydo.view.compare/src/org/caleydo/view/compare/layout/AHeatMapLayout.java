package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.ComparerDetailTemplate;

public abstract class AHeatMapLayout {

	protected static float OVERVIEW_TOTAL_WIDTH_PORTION = 0.25f;
	protected static float OVERVIEW_GROUP_WIDTH_PORTION = 0.04f;
	protected static float OVERVIEW_HEATMAP_WIDTH_PORTION = 0.15f;
	protected static float OVERVIEW_SLIDER_WIDTH_PORTION = 0.06f;

	protected static float DETAIL_WIDTH_PORTION = 0.5f;
	protected static float DETAIL_HEATMAP_GAP_PORTION = 0.02f;
	protected static float OVERVIEW_TO_DETAIL_GAP_PORTION = 0.25f;

	protected static float CAPTION_LABEL_HEIGHT_PORTION = 0.03f;
	protected static float CAPTION_LABEL_HORIZONTAL_SPACING_PORTION = 0.03f;
	protected static float CAPTION_LABEL_VERTICAL_SPACING_PORTION = 0.01f;
	protected static float OVERVIEW_HEIGHT_PORTION = 0.95f;
	protected static float DETAIL_HEIGHT_PORTION = 0.95f;

	protected ComparerDetailTemplate detailHeatMapTemplate;

	protected float totalWidth;
	protected float totalHeight;
	protected float positionX;
	protected float positionY;

	protected RenderCommandFactory renderCommandFactory;
	protected ArrayList<IHeatMapRenderCommand> localRenderCommands;
	protected ArrayList<IHeatMapRenderCommand> remoteRenderCommands;

	public AHeatMapLayout(RenderCommandFactory renderCommandFactory) {
		this.renderCommandFactory = renderCommandFactory;
		localRenderCommands = new ArrayList<IHeatMapRenderCommand>();
		remoteRenderCommands = new ArrayList<IHeatMapRenderCommand>();
	}

	public void setLayoutParameters(float positionX, float positionY,
			float totalHeight, float totalWidth) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.totalHeight = totalHeight;
		this.totalWidth = totalWidth;
	}

	public float getTotalOverviewWidth() {
		return totalWidth * OVERVIEW_TOTAL_WIDTH_PORTION;
	}

	public float getGapWidth() {
		return totalWidth * OVERVIEW_TO_DETAIL_GAP_PORTION;
	}

	public float getDetailWidth() {
		return totalWidth * DETAIL_WIDTH_PORTION;
	}

	public float getOverviewHeight() {
		return totalHeight * OVERVIEW_HEIGHT_PORTION;
	}

	public float getDetailHeight() {
		return totalHeight * DETAIL_HEIGHT_PORTION;
	}

	public float getOverviewGroupWidth() {
		return totalWidth * OVERVIEW_GROUP_WIDTH_PORTION;
	}

	public float getOverviewHeatmapWidth() {
		return totalWidth * OVERVIEW_HEATMAP_WIDTH_PORTION;
	}

	public float getOverviewSliderWidth() {
		return totalWidth * OVERVIEW_SLIDER_WIDTH_PORTION;
	}

	public float getOverviewMaxSliderHeight() {
		return totalHeight;
	}

	public float getOverviewMaxSliderPositionY() {
		return positionY + getOverviewHeight();
	}

	public float getOverviewMinSliderPositionY() {
		return positionY;
	}

	public float getDetailHeatMapHeight(int numSamplesInHeatMap,
			int numTotalSamples, int numHeatMaps) {
		float spaceForHeatMaps = getDetailHeight()
				- (getDetailHeight() * DETAIL_HEATMAP_GAP_PORTION * (numHeatMaps - 1));
		return (spaceForHeatMaps / (float) numTotalSamples)
				* (float) numSamplesInHeatMap;
	}

	public float getDetailHeatMapGapHeight() {
		return getDetailHeight() * DETAIL_HEATMAP_GAP_PORTION;
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

	public abstract Vec3f getOverviewPosition();

	public abstract Vec3f getOverviewGroupBarPosition();

	public abstract Vec3f getOverviewHeatMapPosition();

	public abstract Vec3f getDetailPosition();

	public abstract float getOverviewSliderPositionX();

	public abstract EPickingType getGroupPickingType();

	public abstract EPickingType getHeatMapPickingType();

	public abstract Vec3f getCaptionLabelPosition(float textWidth);

	public void createDetailHeatMapTemplate(GLHeatMap heatMap) {

		detailHeatMapTemplate = new ComparerDetailTemplate(heatMap, true);
	}

	public ComparerDetailTemplate getHeatMapTemplate() {
		return detailHeatMapTemplate;
	}

	public ArrayList<IHeatMapRenderCommand> getRenderCommandsOfLocalItems() {
		return localRenderCommands;
	}

	public ArrayList<IHeatMapRenderCommand> getRenderCommandsOfRemoteItems() {
		return remoteRenderCommands;
	}
}
