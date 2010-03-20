package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public abstract class AHeatMapLayout {

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
	
	public abstract Vec3f getDendrogramLinePosition();
	
	public abstract float getDendrogramLineHeight();
	
	public abstract float getDendrogramLineWidth();
	
	public abstract void useDendrogram(boolean useDendrogram);
	
	public abstract Vec3f getDendrogramPosition();
	
	public abstract float getDendrogramHeight();
	
	public abstract float getDendrogramWidth();
	
	public abstract boolean isDendrogramUsed();

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

	public HeatMapWrapper getHeatMapWrapper() {
		return heatMapWrapper;
	}

}
