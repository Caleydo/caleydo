package org.caleydo.view.matchmaker.layout;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.view.heatmap.HeatMapRenderStyle;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.rendercommand.IHeatMapRenderCommand;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

public abstract class ADetailViewLayoutState {

	protected static final float DETAIL_HEATMAP_SPACING_PORTION_DEFAULT = 0.01f;

	protected static final float DENDROGRAM_BUTTON_HEIGHT_PORTION = 0.03f;

	protected static final float CAPTION_LABEL_HEIGHT_PORTION = 0.025f;
	protected static final float CAPTION_LABEL_VERTICAL_SPACING_PORTION = 0.01f;
	protected static final float DETAIL_HEIGHT_PORTION = 0.955f;

	protected float totalWidth;
	protected float totalHeight;
	protected float positionX;
	protected float positionY;
	protected float detailHeatMapSpacing;
	protected AHeatMapLayout layout;

	private HashMap<Integer, Vec3f> hashHeatMapPositions;
	private HashMap<Integer, Float> hashHeatMapHeights;

	public ADetailViewLayoutState(AHeatMapLayout layout) {
		this.layout = layout;

		hashHeatMapPositions = new HashMap<Integer, Vec3f>();
		hashHeatMapHeights = new HashMap<Integer, Float>();
	}

	public void setLayoutParameters(float positionX, float positionY, float totalHeight,
			float totalWidth) {
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
		HashMap<Group, Boolean> selectedGroups = heatMapWrapper.getSelectedGroups();

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
		float requestedFocusSpacing = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT;

		/**
		 * The overhead of all heat maps
		 */
		float totalHeatMapOverheadSize = 0;

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = heatMapWrapper.getHeatMap(group.getGroupID());
			int numElements = heatMap.getNumberOfVisibleElements();
			totalNumberOfElements += numElements;
			totalHeatMapOverheadSize += heatMap.getRequiredOverheadSpacing();
			if (heatMap.isForceMinSpacing()) {
				numberOfFocusElements += numElements;
				requestedFocusSpacing = HeatMapRenderStyle.MIN_FIELD_HEIGHT_FOR_CAPTION;
			}

		}

		float gapSpace = getDetailHeight() * DETAIL_HEATMAP_SPACING_PORTION_DEFAULT
				* (selectedGroups.size() - 1);

		detailHeatMapSpacing = getDetailHeight() * DETAIL_HEATMAP_SPACING_PORTION_DEFAULT;

		/**
		 * the space that the actual heat maps can use for rendering, i.e.
		 * height - spacing between hms - overhead in hms - spacing on top and
		 * bottom
		 */
		float availableSpaceForHeatMaps = getDetailHeight() - gapSpace
				- totalHeatMapOverheadSize;

		/** the default spacing if no elements were in focus */
		float defaultSpacing = availableSpaceForHeatMaps / totalNumberOfElements;

		/** the minimum spacing for a whole heat map */
		float hmMinSpacing = HeatMapRenderStyle.MIN_SELECTED_FIELD_HEIGHT * 1.5f;

		/** resulting spacing for one element in an active / pinned heat map */
		float resultingFocusSpacing = 0;

		/** resulting spacing for one element in an inactive heat map */
		float resultingNormalSpacing = 0;

		// calculate remaining size after removing the elements which need extra
		// space due to small heat map size
		int minSizeGranted = 0;
		int elementsInOverhead = 0;
		int selectedElementsInOverhead = 0;

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = heatMapWrapper.getHeatMap(group.getGroupID());
			int numElements = heatMap.getNumberOfVisibleElements();

			if (numElements * defaultSpacing < hmMinSpacing) {
				minSizeGranted++;
				elementsInOverhead += numElements;
				if (heatMap.isForceMinSpacing())
					selectedElementsInOverhead += numElements;
			}
		}

		// if we had a heat map which requires the min size we need to
		// recalculate this stuff
		if (minSizeGranted > 0) {
			availableSpaceForHeatMaps -= hmMinSpacing * minSizeGranted;

			totalNumberOfElements -= elementsInOverhead;
			numberOfFocusElements -= selectedElementsInOverhead;

			defaultSpacing = availableSpaceForHeatMaps / (float) (totalNumberOfElements);
		}

		// if we use a zoom layout and we have focus elements
		if (layout.isUseZoom() && numberOfFocusElements > 0 && selectedGroups.size() > 1) {
			// the case where we have enough space for everything
			if (defaultSpacing > requestedFocusSpacing) {
				resultingFocusSpacing = defaultSpacing;
				resultingNormalSpacing = defaultSpacing;
			} else if ((availableSpaceForHeatMaps - (numberOfFocusElements * requestedFocusSpacing)) > availableSpaceForHeatMaps / 2) {
				resultingFocusSpacing = requestedFocusSpacing;
				resultingNormalSpacing = (availableSpaceForHeatMaps - resultingFocusSpacing
						* numberOfFocusElements)
						/ (totalNumberOfElements - numberOfFocusElements);
			} else {
				// resultingFocusSpacing = (availableSpaceForHeatMaps / 3)
				// / numberOfFocusElements;
				// resultingNormalSpacing = (availableSpaceForHeatMaps / 3 * 2)
				// / (totalNumberOfElements - numberOfFocusElements);
				// resultingFocusSpacing = 2 * defaultSpacing;
				// resultingNormalSpacing = (availableSpaceForHeatMaps -
				// resultingFocusSpacing
				// * numberOfFocusElements)
				// / totalNumberOfElements;

				int nrDefaultElements = totalNumberOfElements - numberOfFocusElements;
				resultingNormalSpacing = defaultSpacing / 2;
				resultingFocusSpacing = (availableSpaceForHeatMaps - resultingNormalSpacing
						* nrDefaultElements)
						/ numberOfFocusElements;
			}
		} else {
			resultingNormalSpacing = defaultSpacing;
			resultingFocusSpacing = defaultSpacing;
		}

		for (Group group : selectedGroups.keySet()) {
			GLHeatMap heatMap = heatMapWrapper.getHeatMap(group.getGroupID());
			int numElements = heatMap.getNumberOfVisibleElements();
			float currentHeatMapOverheadSize = heatMap.getRequiredOverheadSpacing();
			float size = 0;
			if (layout.isUseZoom() && heatMap.isForceMinSpacing()) {
				size = (resultingFocusSpacing * numElements) + currentHeatMapOverheadSize;
			} else {
				size = (resultingNormalSpacing * numElements)
						+ currentHeatMapOverheadSize;

			}

			if (size - currentHeatMapOverheadSize < hmMinSpacing)
				size = hmMinSpacing + currentHeatMapOverheadSize;

			hashHeatMapHeights.put(group.getGroupID(), size);
		}

	}

	public float getDetailHeatMapHeight(int heatMapID) {
		return hashHeatMapHeights.get(heatMapID);
	}

	protected float getDetailHeatMapGapHeight() {
		return detailHeatMapSpacing;
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
		HashMap<Group, Boolean> selectedGroups = heatMapWrapper.getSelectedGroups();

		Vec3f detailPosition = layout.getDetailPosition();
		float currentPositionY = detailPosition.y() + getDetailHeight();

		for (Group group : heatMapWrapper.getSet().getContentData(DataTable.RECORD)
				.getContentVA().getGroupList()) {

			if (!selectedGroups.containsKey(group))
				continue;
			GLHeatMap heatMap = heatMapWrapper.getHeatMap(group.getGroupID());
			if (heatMap == null)
				continue;

			float heatMapHeight = getDetailHeatMapHeight(group.getGroupID());
			hashHeatMapPositions.put(group.getGroupID(), new Vec3f(detailPosition.x(),
					currentPositionY - heatMapHeight, detailPosition.z()));
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
