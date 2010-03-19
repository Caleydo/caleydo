package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public abstract class AHeatMapLayout {

	// protected static float OVERVIEW_TOTAL_WIDTH_PORTION = 0.25f;
	// protected static float OVERVIEW_GROUP_WIDTH_PORTION = 0.04f;
	// protected static float OVERVIEW_HEATMAP_WIDTH_PORTION = 0.15f;
	// protected static float OVERVIEW_SLIDER_WIDTH_PORTION = 0.06f;
	//
	// protected static float DETAIL_WIDTH_PORTION = 0.5f;
	// protected static float DETAIL_HEATMAP_GAP_PORTION = 0.02f;
	// protected static float OVERVIEW_TO_DETAIL_GAP_PORTION = 0.25f;
	//
	// protected static float CAPTION_LABEL_HEIGHT_PORTION = 0.03f;
	// protected static float CAPTION_LABEL_HORIZONTAL_SPACING_PORTION = 0.03f;
	// protected static float CAPTION_LABEL_VERTICAL_SPACING_PORTION = 0.01f;
	// protected static float OVERVIEW_HEIGHT_PORTION = 0.95f;
	// protected static float DETAIL_HEIGHT_PORTION = 0.95f;
	//
	// protected ComparerDetailTemplate detailHeatMapTemplate;

	protected float totalWidth;
	protected float totalHeight;
	protected float positionX;
	protected float positionY;
	protected float totalSpaceForAllHeatMapWrappers;
	protected int numExperiments;
	protected int numTotalExperiments;
	protected HeatMapWrapper heatMapWrapper;

	protected RenderCommandFactory renderCommandFactory;
	protected ArrayList<IHeatMapRenderCommand> localRenderCommands;
	protected ArrayList<IHeatMapRenderCommand> remoteRenderCommands;

	public AHeatMapLayout(RenderCommandFactory renderCommandFactory) {
		this.renderCommandFactory = renderCommandFactory;
		localRenderCommands = new ArrayList<IHeatMapRenderCommand>();
		remoteRenderCommands = new ArrayList<IHeatMapRenderCommand>();
		numTotalExperiments = 0;
		numExperiments = 0;
		totalSpaceForAllHeatMapWrappers = 0;
	}

	public void setLayoutParameters(float positionX, float positionY,
			float totalHeight, float totalWidth) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.totalHeight = totalHeight;
		this.totalWidth = totalWidth;
	}
	
	public abstract void calculateDrawingParameters();

	public abstract float getTotalOverviewWidth();

	public abstract float getGapWidth();

	public abstract float getDetailWidth();

	public abstract float getOverviewHeight();

	public abstract float getDetailHeight();

	public abstract float getOverviewGroupWidth();

	public abstract float getOverviewHeatmapWidth();

	public abstract float getOverviewSliderWidth();

	public abstract float getOverviewMaxSliderHeight();

	public abstract float getOverviewMaxSliderPositionY();

	public abstract float getOverviewMinSliderPositionY();

	public abstract float getDetailHeatMapHeight(int heatMapID);

	public abstract float getDetailHeatMapGapHeight();

	public abstract float getCaptionLabelWidth();

	public abstract float getCaptionLabelHeight();

	public abstract float getCaptionLabelHorizontalSpacing();

	public abstract float getCaptionLabelVerticalSpacing();

	public abstract Vec3f getOverviewPosition();

	public abstract Vec3f getOverviewGroupBarPosition();

	public abstract Vec3f getOverviewHeatMapPosition();

	public abstract Vec3f getDetailPosition();

	public abstract float getOverviewSliderPositionX();

	public abstract EPickingType getGroupPickingType();

	public abstract EPickingType getHeatMapPickingType();

	public abstract Vec3f getCaptionLabelPosition(float textWidth);
	
	public abstract Vec3f getDetailHeatMapPosition(int heatMapID);
	
	public abstract Vec3f getDendrogramButtonPosition();
	
	public abstract float getDendrogramButtonHeight();
	
	public abstract float getDendrogramButtonWidth();

	public ArrayList<IHeatMapRenderCommand> getRenderCommandsOfLocalItems() {
		return localRenderCommands;
	}

	public ArrayList<IHeatMapRenderCommand> getRenderCommandsOfRemoteItems() {
		return remoteRenderCommands;
	}

	public void setTotalSpaceForAllHeatMapWrappers(
			float totalSpaceForAllHeatMapWrappers) {
		this.totalSpaceForAllHeatMapWrappers = totalSpaceForAllHeatMapWrappers;
	}

	public void setNumExperiments(int numExperiments) {
		this.numExperiments = numExperiments;
	}

	public void setNumTotalExperiments(int numTotalExperiments) {
		this.numTotalExperiments = numTotalExperiments;
	}

	public Vec3f getPosition() {
		return new Vec3f(positionX, positionY, 0);
	}

	public float getWidth() {
		return totalWidth;
	}

	public float getHeight() {
		return totalHeight;
	}
	
	public void setHeatMapWrapper(HeatMapWrapper heatMapWrapper) {
		this.heatMapWrapper = heatMapWrapper;
	}

}
