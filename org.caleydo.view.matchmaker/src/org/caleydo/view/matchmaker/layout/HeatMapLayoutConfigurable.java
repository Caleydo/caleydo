package org.caleydo.view.matchmaker.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.matchmaker.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

public class HeatMapLayoutConfigurable extends AHeatMapLayout {

	private float captionLabelHeight;
	private float captionLabelWidth;
	private float captionLabelHorizontalSpacing;
	private float captionLabelVerticalSpacing;
	private Vec3f captionLabelPosition;

	private float dendrogramButtonHeight;
	private float dendrogramButtonWidth;
	private Vec3f dendrogramButtonPosition;

	private float dendrogramHeight;
	private float dendrogramWidth;
	private Vec3f dendrogramPosition;

	private float dendrogramLineHeight;
	private float dendrogramLineWidth;
	private Vec3f dendrogramLinePosition;

	private float detailHeight;
	private float detailWidth;
	private Vec3f detailPosition;

	private float overviewHeight;
	private float overviewWidth;
	private Vec3f overviewPosition;

	private float overviewGroupBarWidth;
	private Vec3f overviewGroupBarPosition;

	private float overviewHeatMapWidth;
	private Vec3f overviewHeatMapPosition;

	private float overviewSliderWidth;
	private float overviewMaxSliderHeight;
	private float overviewMaxSliderPositionY;
	private float overviewMinSliderPositionY;
	private float overviewSliderPositionX;

	private float gapWidth;

	private boolean useDendrogram;

	private EPickingType groupPickingType;
	private EPickingType heatMapPickingType;

	private HashMap<Integer, Vec3f> hashHeatMapPositions;
	private HashMap<Integer, Float> hashHeatMapHeights;

	public HeatMapLayoutConfigurable(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
	}

	@Override
	public float getCaptionLabelHeight() {
		return captionLabelHeight;
	}

	public void setCaptionLabelHeight(float captionLabelHeight) {
		this.captionLabelHeight = captionLabelHeight;
	}

	@Override
	public float getCaptionLabelWidth() {
		return captionLabelWidth;
	}

	public void setCaptionLabelWidth(float captionLabelWidth) {
		this.captionLabelWidth = captionLabelWidth;
	}

	@Override
	public float getCaptionLabelHorizontalSpacing() {
		return captionLabelHorizontalSpacing;
	}

	public void setCaptionLabelHorizontalSpacing(float captionLabelHorizontalSpacing) {
		this.captionLabelHorizontalSpacing = captionLabelHorizontalSpacing;
	}

	@Override
	public float getCaptionLabelVerticalSpacing() {
		return captionLabelVerticalSpacing;
	}

	public void setCaptionLabelVerticalSpacing(float captionLabelVerticalSpacing) {
		this.captionLabelVerticalSpacing = captionLabelVerticalSpacing;
	}

	public Vec3f getCaptionLabelPosition() {
		return captionLabelPosition;
	}

	public void setCaptionLabelPosition(Vec3f captionLabelPosition) {
		this.captionLabelPosition = captionLabelPosition;
	}

	@Override
	public float getDendrogramButtonHeight() {
		return dendrogramButtonHeight;
	}

	public void setDendrogramButtonHeight(float dendrogramButtonHeight) {
		this.dendrogramButtonHeight = dendrogramButtonHeight;
	}

	@Override
	public float getDendrogramButtonWidth() {
		return dendrogramButtonWidth;
	}

	public void setDendrogramButtonWidth(float dendrogramButtonWidth) {
		this.dendrogramButtonWidth = dendrogramButtonWidth;
	}

	@Override
	public Vec3f getDendrogramButtonPosition() {
		return dendrogramButtonPosition;
	}

	public void setDendrogramButtonPosition(Vec3f dendrogramButtonPosition) {
		this.dendrogramButtonPosition = dendrogramButtonPosition;
	}

	@Override
	public float getDendrogramHeight() {
		return dendrogramHeight;
	}

	public void setDendrogramHeight(float dendrogramHeight) {
		this.dendrogramHeight = dendrogramHeight;
	}

	@Override
	public float getDendrogramWidth() {
		return dendrogramWidth;
	}

	public void setDendrogramWidth(float dendrogramWidth) {
		this.dendrogramWidth = dendrogramWidth;
	}

	@Override
	public Vec3f getDendrogramPosition() {
		return dendrogramPosition;
	}

	public void setDendrogramPosition(Vec3f dendrogramPosition) {
		this.dendrogramPosition = dendrogramPosition;
	}

	@Override
	public float getDendrogramLineHeight() {
		return dendrogramLineHeight;
	}

	public void setDendrogramLineHeight(float dendrogramLineHeight) {
		this.dendrogramLineHeight = dendrogramLineHeight;
	}

	@Override
	public float getDendrogramLineWidth() {
		return dendrogramLineWidth;
	}

	public void setDendrogramLineWidth(float dendrogramLineWidth) {
		this.dendrogramLineWidth = dendrogramLineWidth;
	}

	@Override
	public Vec3f getDendrogramLinePosition() {
		return dendrogramLinePosition;
	}

	public void setDendrogramLinePosition(Vec3f dendrogramLinePosition) {
		this.dendrogramLinePosition = dendrogramLinePosition;
	}

	@Override
	public float getDetailHeight() {
		return detailHeight;
	}

	public void setDetailHeight(float detailHeight) {
		this.detailHeight = detailHeight;
	}

	@Override
	public float getDetailWidth() {
		return detailWidth;
	}

	public void setDetailWidth(float detailWidth) {
		this.detailWidth = detailWidth;
	}

	@Override
	public Vec3f getDetailPosition() {
		return detailPosition;
	}

	public void setDetailPosition(Vec3f detailPosition) {
		this.detailPosition = detailPosition;
	}

	@Override
	public float getOverviewHeight() {
		return overviewHeight;
	}

	public void setOverviewHeight(float overviewHeight) {
		this.overviewHeight = overviewHeight;
	}

	@Override
	public float getOverviewWidth() {
		return overviewWidth;
	}

	public void setOverviewWidth(float overviewWidth) {
		this.overviewWidth = overviewWidth;
	}

	@Override
	public Vec3f getOverviewPosition() {
		return overviewPosition;
	}

	public void setOverviewPosition(Vec3f overviewPosition) {
		this.overviewPosition = overviewPosition;
	}

	@Override
	public float getOverviewGroupBarWidth() {
		return overviewGroupBarWidth;
	}

	public void setOverviewGroupBarWidth(float overviewGroupBarWidth) {
		this.overviewGroupBarWidth = overviewGroupBarWidth;
	}

	@Override
	public Vec3f getOverviewGroupBarPosition() {
		return overviewGroupBarPosition;
	}

	public void setOverviewGroupBarPosition(Vec3f overviewGroupBarPosition) {
		this.overviewGroupBarPosition = overviewGroupBarPosition;
	}

	@Override
	public float getOverviewHeatMapWidth() {
		return overviewHeatMapWidth;
	}

	public void setOverviewHeatMapWidth(float overviewHeatMapWidth) {
		this.overviewHeatMapWidth = overviewHeatMapWidth;
	}

	@Override
	public Vec3f getOverviewHeatMapPosition() {
		return overviewHeatMapPosition;
	}

	public void setOverviewHeatMapPosition(Vec3f overviewHeatMapPosition) {
		this.overviewHeatMapPosition = overviewHeatMapPosition;
	}

	@Override
	public float getOverviewSliderWidth() {
		return overviewSliderWidth;
	}

	public void setOverviewSliderWidth(float overviewSliderWidth) {
		this.overviewSliderWidth = overviewSliderWidth;
	}

	@Override
	public float getOverviewMaxSliderHeight() {
		return overviewMaxSliderHeight;
	}

	public void setOverviewMaxSliderHeight(float overviewMaxSliderHeight) {
		this.overviewMaxSliderHeight = overviewMaxSliderHeight;
	}

	@Override
	public float getOverviewMaxSliderPositionY() {
		return overviewMaxSliderPositionY;
	}

	public void setOverviewMaxSliderPositionY(float overviewMaxSliderPositionY) {
		this.overviewMaxSliderPositionY = overviewMaxSliderPositionY;
	}

	@Override
	public float getOverviewMinSliderPositionY() {
		return overviewMinSliderPositionY;
	}

	public void setOverviewMinSliderPositionY(float overviewMinSliderPositionY) {
		this.overviewMinSliderPositionY = overviewMinSliderPositionY;
	}

	@Override
	public float getOverviewSliderPositionX() {
		return overviewSliderPositionX;
	}

	public void setOverviewSliderPositionX(float overviewSliderPositionX) {
		this.overviewSliderPositionX = overviewSliderPositionX;
	}

	@Override
	public float getGapWidth() {
		return gapWidth;
	}

	public void setGapWidth(float gapWidth) {
		this.gapWidth = gapWidth;
	}

	@Override
	public EPickingType getGroupPickingType() {
		return groupPickingType;
	}

	public void setGroupPickingType(EPickingType groupPickingType) {
		this.groupPickingType = groupPickingType;
	}

	@Override
	public EPickingType getHeatMapPickingType() {
		return heatMapPickingType;
	}

	public void setHeatMapPickingType(EPickingType heatMapPickingType) {
		this.heatMapPickingType = heatMapPickingType;
	}

	public HashMap<Integer, Vec3f> getHeatMapPositions() {
		return hashHeatMapPositions;
	}

	public void setHeatMapPositions(HashMap<Integer, Vec3f> hashHeatMapPositions) {
		this.hashHeatMapPositions = hashHeatMapPositions;
	}

	public HashMap<Integer, Float> getHeatMapHeights() {
		return hashHeatMapHeights;
	}

	public void setHeatMapHeights(HashMap<Integer, Float> hashHeatMapHeights) {
		this.hashHeatMapHeights = hashHeatMapHeights;
	}

	@Override
	public void calculateDrawingParameters() {

	}

	@Override
	public Vec3f getCaptionLabelPosition(float textWidth) {
		return captionLabelPosition;
	}

	@Override
	public float getDetailHeatMapHeight(int heatMapID) {
		if (hashHeatMapHeights == null)
			return 0;
		return hashHeatMapHeights.get(heatMapID);
	}

	@Override
	public Vec3f getDetailHeatMapPosition(int heatMapID) {
		if (hashHeatMapPositions == null)
			return new Vec3f(0, 0, 0);
		return hashHeatMapPositions.get(heatMapID);
	}

	@Override
	public boolean isDendrogramUsed() {
		return useDendrogram;
	}

	@Override
	public void useDendrogram(boolean useDendrogram) {
		this.useDendrogram = useDendrogram;
	}

	public void setLocalRenderCommands(
			ArrayList<IHeatMapRenderCommand> localRenderCommands) {
		this.localRenderCommands = localRenderCommands;
	}

	public void setRemoteRenderCommands(
			ArrayList<IHeatMapRenderCommand> remoteRenderCommands) {
		this.remoteRenderCommands = remoteRenderCommands;
	}

}
