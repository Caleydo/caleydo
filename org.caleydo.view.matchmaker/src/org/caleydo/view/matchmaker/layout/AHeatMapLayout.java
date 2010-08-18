package org.caleydo.view.matchmaker.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

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
	private boolean useZoom;

	public AHeatMapLayout(RenderCommandFactory renderCommandFactory) {
		this.renderCommandFactory = renderCommandFactory;
		localRenderCommands = new ArrayList<IHeatMapRenderCommand>();
		remoteRenderCommands = new ArrayList<IHeatMapRenderCommand>();
		numTotalExperiments = 0;
		numExperiments = 0;
		totalSpaceForAllHeatMapWrappers = 0;
	}

	public void setLayoutParameters(float positionX, float positionY, float totalHeight,
			float totalWidth) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.totalHeight = totalHeight;
		this.totalWidth = totalWidth;
	}

	public abstract void calculateDrawingParameters();

	public abstract float getOverviewWidth();

	public abstract float getGapWidth();

	public abstract float getDetailWidth();

	public abstract float getOverviewHeight();

	public abstract float getDetailHeight();

	public abstract float getOverviewGroupBarWidth();

	public abstract float getOverviewHeatMapWidth();

	public abstract float getOverviewSliderWidth();

	public abstract float getOverviewMaxSliderHeight();

	public abstract float getOverviewMaxSliderPositionY();

	public abstract float getOverviewMinSliderPositionY();

	public abstract float getDetailHeatMapHeight(int heatMapID);

	public abstract float getCaptionLabelWidth();

	public abstract float getCaptionLabelHeight();

	public abstract float getCaptionLabelHorizontalSpacing();

	public abstract float getCaptionLabelVerticalSpacing();

	public abstract Vec3f getOverviewPosition();

	public abstract Vec3f getOverviewGroupBarPosition();

	public abstract Vec3f getOverviewHeatMapPosition();

	public abstract Vec3f getDetailPosition();

	public abstract float getOverviewSliderPositionX();

	public EPickingType getGroupPickingType() {
		// FIXME: There is no other way to do that yet, but this is way too
		// static
		int heatMapID = heatMapWrapper.getID();
		switch (heatMapID) {
		case 0:
			return EPickingType.COMPARE_GROUP_1_SELECTION;
		case 1:
			return EPickingType.COMPARE_GROUP_2_SELECTION;
		case 2:
			return EPickingType.COMPARE_GROUP_3_SELECTION;
		case 3:
			return EPickingType.COMPARE_GROUP_4_SELECTION;
		case 4:
			return EPickingType.COMPARE_GROUP_5_SELECTION;
		case 5:
			return EPickingType.COMPARE_GROUP_6_SELECTION;
		default:
			return null;
		}
	}

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

	public void setTotalSpaceForAllHeatMapWrappers(float totalSpaceForAllHeatMapWrappers) {
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

	/**
	 * @param useZoom
	 */
	public void setUseZoom(boolean useZoom) {
		this.useZoom = useZoom;
	}

	/**
	 * @return the useZoom
	 */
	public boolean isUseZoom() {
		return useZoom;
	}

	public float getOverviewHeatMapGroupHeight(int groupID) {
		ContentVirtualArray contentVA = heatMapWrapper.getContentVA();
		ContentGroupList contentGroupList = contentVA.getGroupList();

		Group group = contentGroupList.get(groupID);
		float sampleHeight = getOverviewHeatMapSampleHeight();

		return group.getNrElements() * sampleHeight;
	}

	public Vec3f getOverviewHeatMapGroupPosition(int groupID) {
		ContentVirtualArray contentVA = heatMapWrapper.getContentVA();
		ContentGroupList contentGroupList = contentVA.getGroupList();
		Group group = contentGroupList.get(groupID);
		float positionY = getOverviewHeatMapSamplePositionY(group.getEndIndex(),
				group.getGroupIndex());

		return new Vec3f(getOverviewHeatMapPosition().x(), positionY,
				getOverviewHeatMapPosition().z());
	}

	public float getOverviewHeatMapSampleHeight() {
		ContentVirtualArray contentVA = heatMapWrapper.getContentVA();
		ContentGroupList contentGroupList = contentVA.getGroupList();
		return (getOverviewHeight() - (contentGroupList.size() * getOverviewClusterBorderSize()))
				/ contentVA.size();
	}

	public Vec3f getOverviewGroupPosition(int groupID) {
		Vec3f groupBarPosition = getOverviewGroupBarPosition();
		return new Vec3f(groupBarPosition.x(), getOverviewHeatMapGroupPosition(groupID)
				.y(), groupBarPosition.z());
	}

	public float getOverviewClusterBorderSize() {
		AGLView view = heatMapWrapper.getView();
		IViewFrustum viewFrustum = view.getViewFrustum();
		GLCaleydoCanvas canvas = view.getParentGLCanvas();
		// One pixel in height
		return viewFrustum.getHeight() / (float) canvas.getHeight();
	}

	public float getOverviewHeatMapSamplePositionY(int contentIndex) {

		return getOverviewHeatMapSamplePositionY(contentIndex,
				getGroupIDFromContentIndex(contentIndex));
	}

	protected int getGroupIDFromContentIndex(int contentIndex) {
		ContentVirtualArray contentVA = heatMapWrapper.getContentVA();
		for (Group group : contentVA.getGroupList()) {
			if (contentIndex >= group.getStartIndex()
					&& contentIndex <= group.getEndIndex()) {
				return group.getGroupIndex();
			}
		}
		return -1;
	}

	public float getOverviewHeatMapSamplePositionY(int contentIndex, int groupIndex) {

		float sampleHeight = getOverviewHeatMapSampleHeight();
		return getOverviewHeatMapPosition().y() + getOverviewHeight()
				- (sampleHeight * (contentIndex + 1))
				- (groupIndex * getOverviewClusterBorderSize());
	}

}
