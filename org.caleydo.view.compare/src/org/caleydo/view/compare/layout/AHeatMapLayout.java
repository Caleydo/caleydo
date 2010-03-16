package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.heatmap.template.ComparerDetailTemplate;

public abstract class AHeatMapLayout {

//	protected static float OVERVIEW_TOTAL_WIDTH_PORTION = 0.25f;
//	protected static float OVERVIEW_GROUP_WIDTH_PORTION = 0.04f;
//	protected static float OVERVIEW_HEATMAP_WIDTH_PORTION = 0.15f;
//	protected static float OVERVIEW_SLIDER_WIDTH_PORTION = 0.06f;
//
//	protected static float DETAIL_WIDTH_PORTION = 0.5f;
//	protected static float DETAIL_HEATMAP_GAP_PORTION = 0.02f;
//	protected static float OVERVIEW_TO_DETAIL_GAP_PORTION = 0.25f;
//
//	protected static float CAPTION_LABEL_HEIGHT_PORTION = 0.03f;
//	protected static float CAPTION_LABEL_HORIZONTAL_SPACING_PORTION = 0.03f;
//	protected static float CAPTION_LABEL_VERTICAL_SPACING_PORTION = 0.01f;
//	protected static float OVERVIEW_HEIGHT_PORTION = 0.95f;
//	protected static float DETAIL_HEIGHT_PORTION = 0.95f;
//
//	protected ComparerDetailTemplate detailHeatMapTemplate;

	protected float totalWidth;
	protected float totalHeight;
	protected float positionX;
	protected float positionY;

	protected RenderCommandFactory renderCommandFactory;
	protected ArrayList<IHeatMapRenderCommand> localRenderCommands;
	protected ArrayList<IHeatMapRenderCommand> remoteRenderCommands;

	protected ComparerDetailTemplate detailHeatMapTemplate;

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

	public abstract float getTotalOverviewWidth();

	public abstract float getGapWidth();

	public abstract float getDetailWidth();

	public abstract float getOverviewHeight();

	public abstract float getDetailHeight();

	public abstract float getOverviewGroupWidth();

	public abstract float getOverviewHeatmapWidth();

	public abstract float getOverviewSliderWidth();

	public abstract float getOverviewMaxSliderHeight();

	public abstract  float getOverviewMaxSliderPositionY();

	public abstract float getOverviewMinSliderPositionY();

	public abstract float getDetailHeatMapHeight(int numSamplesInHeatMap,
			int numTotalSamples, int numHeatMaps);

	public abstract float getDetailHeatMapGapHeight();

	public abstract float getCaptionLabelWidth();

	public abstract float getCaptionLabelHeight();

	public abstract float getCaptionLabelHorizontalSpacing();

	public abstract float getCaptionLabelVerticalSpacing() ;

	public abstract Vec3f getOverviewPosition();

	public abstract Vec3f getOverviewGroupBarPosition();

	public abstract Vec3f getOverviewHeatMapPosition();

	public abstract Vec3f getDetailPosition();

	public abstract float getOverviewSliderPositionX();

	public abstract EPickingType getGroupPickingType();

	public abstract EPickingType getHeatMapPickingType();

	public abstract Vec3f getCaptionLabelPosition(float textWidth);

	public ArrayList<IHeatMapRenderCommand> getRenderCommandsOfLocalItems() {
		return localRenderCommands;
	}

	public ArrayList<IHeatMapRenderCommand> getRenderCommandsOfRemoteItems() {
		return remoteRenderCommands;
	}

	public ComparerDetailTemplate getHeatMapTemplate() {
		return detailHeatMapTemplate;
	}
}
