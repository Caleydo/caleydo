package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

public abstract class AHeatMapLayoutDetailView extends AHeatMapLayout {

	// protected static final float OVERVIEW_TOTAL_WIDTH_PORTION = 0.25f;
	// protected static final float OVERVIEW_GROUP_WIDTH_PORTION = 0.04f;
	// protected static final float OVERVIEW_HEATMAP_WIDTH_PORTION = 0.15f;
	// protected static final float OVERVIEW_SLIDER_WIDTH_PORTION = 0.06f;
	//
	// protected static final float DETAIL_WIDTH_PORTION = 0.46f;
	// protected static final float DETAIL_HEATMAP_GAP_PORTION = 0.02f;
	//
	// protected static final float OVERVIEW_TO_DETAIL_GAP_PORTION = 0.25f;
	//
	// protected static final float DENDROGRAM_BUTTON_HEIGHT_PORTION = 0.05f;
	// protected static final float DENDROGRAM_BUTTON_WIDTH_PORTION = 0.1f;
	// protected static final float DENDROGRAM_LINE_WIDTH_PORTION = 0.02f;
	// protected static final float DENDROGRAM_LINE_SPACING_PORTION = 0.04f;
	//
	// protected static final float CAPTION_LABEL_HEIGHT_PORTION = 0.03f;
	// protected static final float CAPTION_LABEL_WIDTH_PORTION = 0.92f;
	// protected static final float CAPTION_LABEL_HORIZONTAL_SPACING_PORTION =
	// 0.03f;
	// protected static final float CAPTION_LABEL_VERTICAL_SPACING_PORTION =
	// 0.01f;
	// protected static final float OVERVIEW_HEIGHT_PORTION = 0.95f;
	// protected static final float DETAIL_HEIGHT_PORTION = 0.95f;

	// private HashMap<Integer, Vec3f> hashHeatMapPositions;
	// private HashMap<Integer, Float> hashHeatMapHeights;
	protected ADetailViewLayoutState state;
	protected boolean useDendrogram;

	public AHeatMapLayoutDetailView(RenderCommandFactory renderCommandFactory) {
		super(renderCommandFactory);
		state = new DetailViewLayoutStateNormal(this);

		localRenderCommands.addAll(state
				.getLocalRenderCommands(renderCommandFactory));

		remoteRenderCommands.addAll(state
				.getRemoteRenderCommands(renderCommandFactory));
		useDendrogram = false;

		// hashHeatMapPositions = new HashMap<Integer, Vec3f>();
		// hashHeatMapHeights = new HashMap<Integer, Float>();
	}

	public void setLayoutParameters(float positionX, float positionY,
			float totalHeight, float totalWidth) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.totalHeight = totalHeight;
		this.totalWidth = totalWidth;
		state
				.setLayoutParameters(positionX, positionY, totalHeight,
						totalWidth);
	}

	public float getTotalOverviewWidth() {
		return state.getTotalOverviewWidth();
	}

	public float getGapWidth() {
		return state.getGapWidth();
	}

	public float getDetailWidth() {
		return state.getDetailWidth();
	}

	public float getOverviewHeight() {
		return state.getOverviewHeight();
	}

	public float getDetailHeight() {
		return state.getDetailHeight();
	}

	public float getOverviewGroupWidth() {
		return state.getOverviewGroupWidth();
	}

	public float getOverviewHeatmapWidth() {
		return state.getOverviewHeatmapWidth();
	}

	public float getOverviewSliderWidth() {
		return state.getOverviewSliderWidth();
	}

	public float getOverviewMaxSliderHeight() {
		return state.getOverviewMaxSliderHeight();
	}

	public float getOverviewMaxSliderPositionY() {
		return state.getOverviewMaxSliderPositionY();
	}

	public float getOverviewMinSliderPositionY() {
		return state.getOverviewMinSliderPositionY();
	}

	// protected void calculateDetailHeatMapHeights() {
	//
	// hashHeatMapHeights.clear();
	//
	// HashMap<Group, GroupInfo> selectedGroups = heatMapWrapper
	// .getSelectedGroups();
	//
	// /** all genes currently rendered */
	// int totalNumberOfElements = 0;
	// /**
	// * all genes for which we would like to show text, i.e. that are in a
	// * heat map that is either active or pinned
	// */
	// int numberOfFocusElements = 0;
	// /**
	// * the space the heat maps would like to have per element
	// */
	// float requestedFocusSpacing = 0;
	//
	// float totalHeatMapOverheadSize = 0;
	//
	// for (Group group : selectedGroups.keySet()) {
	// GLHeatMap heatMap = heatMapWrapper
	// .getHeatMap(group.getGroupIndex());
	// int numElements = heatMap.getNumberOfVisibleElements();
	// totalNumberOfElements += numElements;
	// totalHeatMapOverheadSize += heatMap.getRequiredOverheadSpacing();
	// if (heatMap.isForceMinSpacing()) {
	// numberOfFocusElements += numElements;
	// requestedFocusSpacing = heatMap.getMinSpacing();
	// }
	//
	// }
	//
	// /**
	// * the space that the actual heat maps can use for rendering, i.e.
	// * height - spacing between hms - overhead in hms - spacing on top and
	// * bottom
	// */
	// float availableSpaceForHeatMaps = getDetailHeight()
	// - (getDetailHeight() * DETAIL_HEATMAP_GAP_PORTION * (selectedGroups
	// .size() - 1)) - totalHeatMapOverheadSize;
	//
	// /** the default spacing if no elements were in focus */
	// float defaultSpacing = availableSpaceForHeatMaps
	// / (float) totalNumberOfElements;
	//
	// /** the minimum spacing */
	// float minSpacing = defaultSpacing / 3.0f;
	//
	// /** resulting spacing for one element in an active / pinned heat map */
	// float resultingFocusSpacing = 0;
	//
	// /** resulting spacing for one element in an inactive heat map */
	// float resultingNormalSpacing = 0;
	//
	// // the case where we have enough space for everything
	// if (numberOfFocusElements > 0) {
	// if (defaultSpacing > requestedFocusSpacing) {
	// resultingFocusSpacing = defaultSpacing;
	// resultingNormalSpacing = defaultSpacing;
	// } else if ((availableSpaceForHeatMaps - (numberOfFocusElements *
	// requestedFocusSpacing))
	// / (totalNumberOfElements - numberOfFocusElements) > minSpacing) {
	// resultingFocusSpacing = requestedFocusSpacing;
	// resultingNormalSpacing = (availableSpaceForHeatMaps -
	// resultingFocusSpacing
	// * numberOfFocusElements)
	// / (totalNumberOfElements - numberOfFocusElements);
	// } else {
	// resultingNormalSpacing = minSpacing;
	// resultingFocusSpacing = (availableSpaceForHeatMaps -
	// (totalNumberOfElements - numberOfFocusElements)
	// * resultingNormalSpacing)
	// / numberOfFocusElements;
	// }
	// } else {
	// resultingNormalSpacing = defaultSpacing;
	// }
	//
	// for (Group group : selectedGroups.keySet()) {
	// GLHeatMap heatMap = heatMapWrapper
	// .getHeatMap(group.getGroupIndex());
	// int numElements = heatMap.getNumberOfVisibleElements();
	// float currentHeatMapOverheadSize = heatMap
	// .getRequiredOverheadSpacing();
	// if (heatMap.isForceMinSpacing()) {
	// hashHeatMapHeights.put(group.getGroupIndex(),
	// (resultingFocusSpacing * numElements)
	// + currentHeatMapOverheadSize);
	// } else {
	// hashHeatMapHeights.put(group.getGroupIndex(),
	// (resultingNormalSpacing * numElements)
	// + currentHeatMapOverheadSize);
	// }
	// }
	//
	// }

	public float getDetailHeatMapHeight(int heatMapID) {
		return state.getDetailHeatMapHeight(heatMapID);
	}

	public float getDetailHeatMapGapHeight() {
		return state.getDetailHeatMapGapHeight();
	}

	public float getCaptionLabelWidth() {
		return state.getCaptionLabelWidth();
	}

	public float getCaptionLabelHeight() {
		return state.getCaptionLabelHeight();
	}

	public float getCaptionLabelHorizontalSpacing() {
		return state.getCaptionLabelHorizontalSpacing();
	}

	public float getCaptionLabelVerticalSpacing() {
		return state.getCaptionLabelVerticalSpacing();
	}

	// protected void calculateHeatMapPositions() {
	//
	// hashHeatMapPositions.clear();
	// HashMap<Group, GroupInfo> selectedGroups = heatMapWrapper
	// .getSelectedGroups();
	//
	// Vec3f detailPosition = getDetailPosition();
	// float currentPositionY = detailPosition.y() + getDetailHeight();
	//
	// for (Group group : heatMapWrapper.getSet().getContentVA(
	// ContentVAType.CONTENT).getGroupList()) {
	//
	// if (!selectedGroups.containsKey(group))
	// continue;
	// GLHeatMap heatMap = heatMapWrapper
	// .getHeatMap(group.getGroupIndex());
	// if (heatMap == null)
	// continue;
	//
	// float heatMapHeight = getDetailHeatMapHeight(group.getGroupIndex());
	// hashHeatMapPositions.put(group.getGroupIndex(), new Vec3f(
	// detailPosition.x(), currentPositionY - heatMapHeight,
	// detailPosition.z()));
	// currentPositionY -= (heatMapHeight + getDetailHeatMapGapHeight());
	// }
	// }

	@Override
	public Vec3f getDetailHeatMapPosition(int heatMapID) {
		return state.getDetailHeatMapPosition(heatMapID);
	}

	@Override
	public void calculateDrawingParameters() {
		state.calculateDrawingParameters();
	}

	@Override
	public float getDendrogramButtonHeight() {
		return state.getDendrogramButtonHeight();
	}

	@Override
	public float getDendrogramButtonWidth() {
		return state.getDendrogramButtonWidth();
	}

	@Override
	public float getDendrogramLineHeight() {
		return state.getDendrogramLineHeight();
	}

	@Override
	public float getDendrogramLineWidth() {
		return state.getDendrogramLineWidth();
	}

	public float getDendrogramLineSpacing() {
		return state.getDendrogramLineSpacing();
	}

	@Override
	public void useDendrogram(boolean useDendrogram) {
		if(this.useDendrogram == useDendrogram)
			return;
		
		this.useDendrogram = useDendrogram;
		if(useDendrogram) {
			float dendrogramButtonWidth = getDendrogramButtonWidth();
			DetailViewLayoutStateDendrogram dendrogramState = new DetailViewLayoutStateDendrogram(this);
			dendrogramState.setDendrogramButtonWidth(dendrogramButtonWidth);
			state = dendrogramState;
		} else {
			state = new DetailViewLayoutStateNormal(this);
		}
	}

	public float getDendrogramHeight() {
		return state.getDendrogramHeight();
	}

	public float getDendrogramWidth() {
		return state.getDendrogramWidth();
	}
	
	@Override
	public boolean isDendrogramUsed() {
		return useDendrogram;
	}
}
