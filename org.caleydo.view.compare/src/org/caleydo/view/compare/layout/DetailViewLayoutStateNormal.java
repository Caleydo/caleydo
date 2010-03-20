package org.caleydo.view.compare.layout;

import java.util.ArrayList;

import org.caleydo.view.compare.rendercommand.ERenderCommandType;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public class DetailViewLayoutStateNormal extends ADetailViewLayoutState {

	protected static final float OVERVIEW_TOTAL_WIDTH_PORTION = 0.25f;
	protected static final float OVERVIEW_GROUP_WIDTH_PORTION = 0.04f;
	protected static final float OVERVIEW_HEATMAP_WIDTH_PORTION = 0.15f;
	protected static final float OVERVIEW_SLIDER_WIDTH_PORTION = 0.06f;
	protected static final float OVERVIEW_HEIGHT_PORTION = 0.95f;

	protected static final float DETAIL_WIDTH_PORTION = 0.46f;

	protected static final float OVERVIEW_TO_DETAIL_GAP_PORTION = 0.25f;

	protected static final float DENDROGRAM_BUTTON_WIDTH_PORTION = 0.1f;
	protected static final float DENDROGRAM_LINE_WIDTH_PORTION = 0.02f;
	protected static final float DENDROGRAM_LINE_SPACING_PORTION = 0.04f;

	protected static final float CAPTION_LABEL_WIDTH_PORTION = 0.92f;
	protected static final float CAPTION_LABEL_HORIZONTAL_SPACING_PORTION = 0.03f;

	public DetailViewLayoutStateNormal(AHeatMapLayout layout) {
		super(layout);
	}

	@Override
	public float getCaptionLabelHorizontalSpacing() {
		return totalWidth * CAPTION_LABEL_HORIZONTAL_SPACING_PORTION;
	}

	@Override
	public float getCaptionLabelWidth() {
		return totalWidth * CAPTION_LABEL_WIDTH_PORTION;
	}

	@Override
	public float getDendrogramButtonWidth() {
		return totalWidth * DENDROGRAM_BUTTON_WIDTH_PORTION;
	}

	@Override
	public float getDendrogramHeight() {
		return 0;
	}

	@Override
	public float getDendrogramLineSpacing() {
		return totalWidth * DENDROGRAM_LINE_SPACING_PORTION;
	}

	@Override
	public float getDendrogramLineWidth() {
		return totalWidth * DENDROGRAM_LINE_WIDTH_PORTION;
	}

	@Override
	public float getDendrogramWidth() {
		return 0;
	}

	@Override
	public float getDetailWidth() {
		return totalWidth * DETAIL_WIDTH_PORTION;
	}

	@Override
	public float getGapWidth() {
		return totalWidth * OVERVIEW_TO_DETAIL_GAP_PORTION;
	}

	@Override
	public float getOverviewGroupWidth() {
		return totalWidth * OVERVIEW_GROUP_WIDTH_PORTION;
	}

	@Override
	public float getOverviewHeatmapWidth() {
		return totalWidth * OVERVIEW_HEATMAP_WIDTH_PORTION;
	}

	@Override
	public float getOverviewSliderWidth() {
		return totalWidth * OVERVIEW_SLIDER_WIDTH_PORTION;
	}
	
	@Override
	public float getTotalOverviewWidth() {
		return totalWidth * OVERVIEW_TOTAL_WIDTH_PORTION;
	}

	@Override
	public ArrayList<IHeatMapRenderCommand> getLocalRenderCommands(
			RenderCommandFactory renderCommandFactory) {
		ArrayList<IHeatMapRenderCommand> localRenderCommands = new ArrayList<IHeatMapRenderCommand>();

		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_GROUP_BAR));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_HEATMAP));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.OVERVIEW_SLIDER));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.CAPTION_LABEL));
		localRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.DENDROGRAM_BUTTON));
		return localRenderCommands;
	}

	@Override
	public ArrayList<IHeatMapRenderCommand> getRemoteRenderCommands(
			RenderCommandFactory renderCommandFactory) {
		ArrayList<IHeatMapRenderCommand> remoteRenderCommands = new ArrayList<IHeatMapRenderCommand>();

		remoteRenderCommands.add(renderCommandFactory
				.getRenderCommand(ERenderCommandType.DETAIL_HEATMAPS));

		return remoteRenderCommands;
	}
	
	@Override
	public float getOverviewHeight() {
		return totalHeight * OVERVIEW_HEIGHT_PORTION;
	}

	@Override
	public float getDendrogramBottomSpacing() {
		return 0;
	}

}
