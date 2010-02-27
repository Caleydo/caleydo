package org.caleydo.view.compare;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.Group;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageVAType;
import org.caleydo.core.data.selection.StorageVirtualArray;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.HeatMapUtil;

import com.sun.opengl.util.texture.Texture;

public class HeatMapOverview {

	private HeatMapLayout layout;
	private VerticalSlider slider;
	private ArrayList<Texture> overviewTextures;
	private ISet set;
	private ContentVirtualArray contentVA;
	private StorageVirtualArray storageVA;
	private ArrayList<GroupInfo> selectedGroups;

	public HeatMapOverview(HeatMapLayout layout) {
		this.layout = layout;
		slider = new VerticalSlider(layout);
		selectedGroups = new ArrayList<GroupInfo>();
	}

	public void draw(GL gl, TextureManager textureManager,
			PickingManager pickingManager, int viewID, int sliderPickingID) {

		Vec3f overviewHeatMapPosition = layout.getOverviewHeatMapPosition();
		float overviewHeight = layout.getOverviewHeight();

		gl.glPushMatrix();
		gl.glTranslatef(overviewHeatMapPosition.x(), overviewHeatMapPosition
				.y(), overviewHeatMapPosition.z());
		HeatMapUtil.renderHeatmapTextures(gl, overviewTextures, overviewHeight,
				layout.getOverviewHeatmapWidth());
		gl.glPopMatrix();

		gl.glPushMatrix();
		Vec3f overviewGroupsPosition = layout.getOverviewGroupBarPosition();
		gl.glTranslatef(overviewGroupsPosition.x(), overviewGroupsPosition.y(),
				overviewGroupsPosition.z());

		HeatMapUtil.renderGroupBar(gl, contentVA, layout.getOverviewHeight(),
				layout.getOverviewGroupWidth(), pickingManager, viewID,
				EPickingType.COMPARE_GROUP_SELECTION, textureManager);

		gl.glPopMatrix();

		slider
				.draw(gl, pickingManager, textureManager, viewID,
						sliderPickingID);

	}

	public boolean handleDragging(GL gl, GLMouseListener glMouseListener) {

		if (slider.handleDragging(gl, glMouseListener)) {
			Pair<Integer, Integer> bounds = getBoundaryIndicesOfElementsInFocus();
			selectGroupsAccordingToBoundIndices(bounds.getFirst(), bounds
					.getSecond());
			return true;
		}

		return false;
	}

	private void selectGroupsAccordingToBoundIndices(int lowerBoundIndex,
			int upperBoundIndex) {

		ContentGroupList contentGroupList = contentVA.getGroupList();
		int groupSampleStartIndex = 0;
		int groupSampleEndIndex = 0;
		int groupIndex = 0;
		selectedGroups.clear();
		for (Group group : contentGroupList) {
			groupSampleEndIndex = groupSampleStartIndex + group.getNrElements()
					- 1;
			if (groupSampleStartIndex >= lowerBoundIndex
					&& groupSampleEndIndex <= upperBoundIndex) {
				group.setSelectionType(SelectionType.SELECTION);
				selectedGroups.add(new GroupInfo(group, groupIndex, groupSampleStartIndex));
			} else {
				group.setSelectionType(SelectionType.NORMAL);
			}

			groupSampleStartIndex += group.getNrElements();
			groupIndex++;
		}
	}

	private Pair<Integer, Integer> getBoundaryIndicesOfElementsInFocus() {
		float sliderHeight = slider.getSliderHeight();
		float sliderBottomPositionY = slider.getSliderPositionY();
		float sliderTopPositionY = sliderBottomPositionY + sliderHeight;
		float overviewHeight = layout.getOverviewHeight();
		float sampleHeight = overviewHeight / ((float) contentVA.size());

		int numSamplesInFocus = (int) Math.ceil(sliderHeight / sampleHeight);
		int sampleIndexTop = (int) Math
				.floor((overviewHeight - (sliderTopPositionY - layout
						.getOverviewPosition().y()))
						/ sampleHeight);
		if (sampleIndexTop < 0)
			sampleIndexTop = 0;
		int sampleIndexBottom = sampleIndexTop + numSamplesInFocus;

		if (sampleIndexBottom >= contentVA.size()) {
			sampleIndexBottom = contentVA.size() - 1;
			if (sampleIndexTop - sampleIndexBottom < numSamplesInFocus)
				sampleIndexTop = sampleIndexBottom - numSamplesInFocus;
		}
		if (sampleIndexTop < 0) {
			sampleIndexTop = 0;
			if (sampleIndexTop - sampleIndexBottom < numSamplesInFocus)
				sampleIndexBottom = sampleIndexBottom + numSamplesInFocus;
		}

		return new Pair<Integer, Integer>(sampleIndexTop, sampleIndexBottom);
	}

	public void handleSliderSelection(EPickingType pickingType,
			EPickingMode pickingMode) {
		slider.handleSliderSelection(pickingType, pickingMode);
	}

	public void setSet(ISet set) {
		this.set = set;
		contentVA = set.getContentVA(ContentVAType.CONTENT);
		storageVA = set.getStorageVA(StorageVAType.STORAGE);
		overviewTextures = HeatMapUtil.createHeatMapTextures(set, contentVA,
				storageVA, null);
	}

	public ArrayList<GroupInfo> getSelectedGroups() {
		return selectedGroups;
	}
}
