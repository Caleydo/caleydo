package org.caleydo.view.compare.layout;

import org.caleydo.view.compare.rendercommand.ERenderCommandType;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public abstract class AHeatMapLayoutDetailView extends AHeatMapLayout {
	
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

	public AHeatMapLayoutDetailView(RenderCommandFactory renderCommandFactory) {
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

}
