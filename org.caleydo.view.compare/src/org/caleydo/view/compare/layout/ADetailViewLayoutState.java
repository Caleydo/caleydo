package org.caleydo.view.compare.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.Group;
import org.caleydo.view.compare.GroupInfo;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public abstract class ADetailViewLayoutState {

	protected static final float DETAIL_HEATMAP_GAP_PORTION = 0.02f;

	protected static final float DENDROGRAM_BUTTON_HEIGHT_PORTION = 0.05f;

	protected static final float CAPTION_LABEL_HEIGHT_PORTION = 0.03f;
	protected static final float CAPTION_LABEL_VERTICAL_SPACING_PORTION = 0.01f;
	protected static final float DETAIL_HEIGHT_PORTION = 0.95f;

	protected float totalWidth;
	protected float totalHeight;
	protected float positionX;
	protected float positionY;
	protected AHeatMapLayout layout;

	private HashMap<Integer, Vec3f> hashHeatMapPositions;
	private HashMap<Integer, Float> hashHeatMapHeights;

	public ADetailViewLayoutState(AHeatMapLayout layout) {
		this.layout = layout;

		hashHeatMapPositions = new HashMap<Integer, Vec3f>();
		hashHeatMapHeights = new HashMap<Integer, Float>();
	}

	public void setLayoutParameters(float positionX, float positionY,
			float totalHeight, float totalWidth) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.totalHeight = totalHeight;
		this.totalWidth = totalWidth;
	}

	public float getDetailHeight() {
		return totalHeight * DETAIL_HEIGHT_PORTION;
	}

	public float getOverviewMaxSliderHeight() {
		return totalHeight - getDendrogramBottomSpacing();
	}

	public float getOverviewMaxSliderPositionY() {
		return positionY + getDendrogramBottomSpacing() + getOverviewHeight();
	}

	public float getOverviewMinSliderPositionY() {
		return positionY + getDendrogramBottomSpacing();
	}

	protected void calculateDetailHeatMapHeights() {

		hashHeatMapHeights.clear();

		HeatMapWrapper heatMapWrapper = layout.getHeatMapWrapper();
		HashMap<Group, GroupInfo> selectedGroups = heatMapWrapper
				.getSelectedGroups();

		/** all genes currently rendered */
		int totalNumberOfElements = 0;
		/**
		 * all genes for which we would like to show text, i.e. that are in a
		 * heat map that is either active or pinned
		 */
		int numberOfFocusElements = 0;
		/**
		 * the space the heat maps would like to have per element
		 */
		float requestedFocusSpacing = 0;

		float totalHeatMapOverheadSize = 0;

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = heatMapWrapper
					.getHeatMap(group.getGroupIndex());
			int numElements = heatMap.getNumberOfVisibleElements();
			totalNumberOfElements += numElements;
			totalHeatMapOverheadSize += heatMap.getRequiredOverheadSpacing();
			if (heatMap.isForceMinSpacing()) {
				numberOfFocusElements += numElements;
				requestedFocusSpacing = heatMap.getMinSpacing();
			}

		}

		/**
		 * the space that the actual heat maps can use for rendering, i.e.
		 * height - spacing between hms - overhead in hms - spacing on top and
		 * bottom
		 */
		float availableSpaceForHeatMaps = getDetailHeight()
				- (getDetailHeight() * DETAIL_HEATMAP_GAP_PORTION * (selectedGroups
						.size() - 1)) - totalHeatMapOverheadSize;

		/** the default spacing if no elements were in focus */
		float defaultSpacing = availableSpaceForHeatMaps
				/ (float) totalNumberOfElements;

		/** the minimum spacing */
		float minSpacing = defaultSpacing / 10.0f;

		/** the minimum spacing for a whole heat map */
		float hmMinSpacing = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT * 1.5f;

		/** resulting spacing for one element in an active / pinned heat map */
		float resultingFocusSpacing = 0;

		/** resulting spacing for one element in an inactive heat map */
		float resultingNormalSpacing = 0;

		// calculate remaining size after removing the elements which need extra
		// space due to small heat map size
		int overheadsGranted = 0;
		int elementsInOverhead = 0;

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = heatMapWrapper
					.getHeatMap(group.getGroupIndex());
			int numElements = heatMap.getNumberOfVisibleElements();

			if (numElements * defaultSpacing < hmMinSpacing) {
				overheadsGranted++;
				elementsInOverhead += numElements;
			}
		}

		availableSpaceForHeatMaps -= hmMinSpacing * overheadsGranted
				- (elementsInOverhead * defaultSpacing);

		defaultSpacing = availableSpaceForHeatMaps
				/ (float) (totalNumberOfElements);
		minSpacing = defaultSpacing / 4;

		if (layout.isUseZoom() && numberOfFocusElements > 0) {
			// the case where we have enough space for everything
			if (defaultSpacing > requestedFocusSpacing) {
				resultingFocusSpacing = defaultSpacing;
				resultingNormalSpacing = defaultSpacing;
			} else if ((availableSpaceForHeatMaps - (numberOfFocusElements * requestedFocusSpacing))
					/ (totalNumberOfElements - numberOfFocusElements) > minSpacing) {
				resultingFocusSpacing = requestedFocusSpacing;
				resultingNormalSpacing = (availableSpaceForHeatMaps - resultingFocusSpacing
						* numberOfFocusElements)
						/ (totalNumberOfElements - numberOfFocusElements);
			} else {
				resultingNormalSpacing = minSpacing;
				resultingFocusSpacing = (availableSpaceForHeatMaps - (totalNumberOfElements - numberOfFocusElements)
						* resultingNormalSpacing)
						/ numberOfFocusElements;
			}
		}
		// if(!layout.isUseZoom() && numberOfFocusElements > 0)
		// {
		// if(availableSpaceForHeatMaps > )
		// }
		else {
			resultingNormalSpacing = defaultSpacing;
		}

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = heatMapWrapper
					.getHeatMap(group.getGroupIndex());
			int numElements = heatMap.getNumberOfVisibleElements();
			float currentHeatMapOverheadSize = heatMap
					.getRequiredOverheadSpacing();
			float size = 0;
			if (layout.isUseZoom() && heatMap.isForceMinSpacing()) {
				size = (resultingFocusSpacing * numElements)
						+ currentHeatMapOverheadSize;
			} else {
				size = (resultingNormalSpacing * numElements)
						+ currentHeatMapOverheadSize;

			}

			if (size - currentHeatMapOverheadSize < hmMinSpacing)
				size = hmMinSpacing + currentHeatMapOverheadSize;

			hashHeatMapHeights.put(group.getGroupIndex(), size);
		}

	}

	public float getDetailHeatMapHeight(int heatMapID) {
		return hashHeatMapHeights.get(heatMapID);
	}

	public float getDetailHeatMapGapHeight() {
		return getDetailHeight() * DETAIL_HEATMAP_GAP_PORTION;
	}

	public float getCaptionLabelHeight() {
		return totalHeight * CAPTION_LABEL_HEIGHT_PORTION;
	}

	public float getCaptionLabelVerticalSpacing() {
		return totalHeight * CAPTION_LABEL_VERTICAL_SPACING_PORTION;
	}

	protected void calculateHeatMapPositions() {

		HeatMapWrapper heatMapWrapper = layout.getHeatMapWrapper();
		hashHeatMapPositions.clear();
		HashMap<Group, GroupInfo> selectedGroups = heatMapWrapper
				.getSelectedGroups();

		Vec3f detailPosition = layout.getDetailPosition();
		float currentPositionY = detailPosition.y() + getDetailHeight();

		for (Group group : heatMapWrapper.getSet().getContentVA(
				ContentVAType.CONTENT).getGroupList()) {

			if (!selectedGroups.containsKey(group))
				continue;
			GLHeatMap heatMap = heatMapWrapper
					.getHeatMap(group.getGroupIndex());
			if (heatMap == null)
				continue;

			float heatMapHeight = getDetailHeatMapHeight(group.getGroupIndex());
			hashHeatMapPositions.put(group.getGroupIndex(), new Vec3f(
					detailPosition.x(), currentPositionY - heatMapHeight,
					detailPosition.z()));
			currentPositionY -= (heatMapHeight + getDetailHeatMapGapHeight());
		}
	}

	public Vec3f getDetailHeatMapPosition(int heatMapID) {
		return hashHeatMapPositions.get(heatMapID);
	}

	public void calculateDrawingParameters() {
		calculateDetailHeatMapHeights();
		calculateHeatMapPositions();
	}

	public float getDendrogramButtonHeight() {
		return totalHeight * DENDROGRAM_BUTTON_HEIGHT_PORTION;
	}

	public float getDendrogramLineHeight() {
		return totalHeight;
	}

	public abstract float getOverviewHeight();

	public abstract float getTotalOverviewWidth();

	public abstract float getGapWidth();

	public abstract float getDetailWidth();

	public abstract float getOverviewGroupWidth();

	public abstract float getOverviewHeatMapWidth();

	public abstract float getOverviewSliderWidth();

	public abstract float getCaptionLabelWidth();

	public abstract float getCaptionLabelHorizontalSpacing();

	public abstract float getDendrogramButtonWidth();

	public abstract float getDendrogramLineWidth();

	public abstract float getDendrogramLineSpacing();

	public abstract float getDendrogramHeight();

	public abstract float getDendrogramWidth();

	public abstract ArrayList<IHeatMapRenderCommand> getLocalRenderCommands(
			RenderCommandFactory renderCommandFactory);

	public abstract ArrayList<IHeatMapRenderCommand> getRemoteRenderCommands(
			RenderCommandFactory renderCommandFactory);

	public abstract float getDendrogramBottomSpacing();
}
