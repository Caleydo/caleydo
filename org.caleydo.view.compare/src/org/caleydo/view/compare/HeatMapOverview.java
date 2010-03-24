package org.caleydo.view.compare;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
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
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.heatmap.hierarchical.HeatMapUtil;

import com.sun.opengl.util.texture.Texture;

public class HeatMapOverview {

	private AHeatMapLayout layout;
	private VerticalSlider slider;
	private ArrayList<ArrayList<Texture>> overviewTextures;
	private ISet set;
	private ContentVirtualArray contentVA;
	private StorageVirtualArray storageVA;
	private HashMap<Group, Boolean> selectedGroups;

	public HeatMapOverview(AHeatMapLayout layout) {
		this.layout = layout;
		slider = new VerticalSlider(layout);
		selectedGroups = new HashMap<Group, Boolean>();
		overviewTextures = new ArrayList<ArrayList<Texture>>();
	}

	public void draw(GL gl, TextureManager textureManager,
			PickingManager pickingManager,
			ContentSelectionManager contentSelectionManager, int viewID,
			int sliderPickingID) {

		// Vec3f overviewHeatMapPosition = layout.getOverviewHeatMapPosition();
		// float overviewHeight = layout.getOverviewHeight();
		//
		// gl.glPushMatrix();
		// gl.glTranslatef(overviewHeatMapPosition.x(), overviewHeatMapPosition
		// .y(), overviewHeatMapPosition.z());
		// HeatMapUtil.renderHeatmapTextures(gl, overviewTextures,
		// overviewHeight,
		// layout.getOverviewHeatmapWidth());
		// drawSelections(gl, contentSelectionManager);
		//		
		// gl.glPopMatrix();

		// gl.glPushMatrix();
		// Vec3f overviewGroupsPosition = layout.getOverviewGroupBarPosition();
		// gl.glTranslatef(overviewGroupsPosition.x(),
		// overviewGroupsPosition.y(),
		// overviewGroupsPosition.z());
		//
		// HeatMapUtil.renderGroupBar(gl, contentVA, layout.getOverviewHeight(),
		// layout.getOverviewGroupWidth(), pickingManager, viewID, layout
		// .getGroupPickingType(), textureManager);
		//
		// gl.glPopMatrix();

		// HeatMapUtil.renderGroupBar(gl, contentVA, layout.getOverviewHeight(),
		// layout.getOverviewGroupWidth(), pickingManager, viewID, layout
		// .getGroupPickingType(), textureManager);
		//
		// //FIXME: remove this. the group borders need to be actually put in
		// the heatmap texture
		// // Render group borders over heatmap texture
		// ContentGroupList contentGroupList = contentVA.getGroupList();
		// boolean isLeft = true;
		// if (layout.getOverviewHeatMapPosition().x() <
		// layout.getDetailPosition().x())
		// isLeft = false;
		//		
		// if (contentGroupList != null) {
		// float sampleHeight = layout.getOverviewHeight() / ((float)
		// contentVA.size());
		// float groupPositionY = layout.getOverviewHeight();
		//
		// gl.glColor4f(1, 1, 1, 1);
		// gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
		// int groupIndex = 0;
		// for (Group group : contentGroupList) {
		// int numSamplesGroup = group.getNrElements();
		// float groupHeight = numSamplesGroup * sampleHeight;
		// // gl.glLineWidth(1);
		// gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glVertex3f(0, groupPositionY, 0.0f);
		// if (isLeft)
		// gl.glVertex3f(0-layout.getOverviewHeatmapWidth(), groupPositionY,
		// 0.0f);
		// else
		// gl.glVertex3f(layout.getOverviewSliderWidth()+layout.getOverviewHeatmapWidth(),
		// groupPositionY, 0.0f);
		// gl.glEnd();
		// groupIndex++;
		// groupPositionY -= groupHeight;
		// }
		// }
		//		
		// gl.glPopMatrix();

		// slider
		// .draw(gl, pickingManager, textureManager, viewID,
		// sliderPickingID);

	}

	// private void drawSelections(GL gl,
	// ContentSelectionManager contentSelectionManager) {
	//		
	// float overviewHeight = layout.getOverviewHeight();
	// float sampleHeight = overviewHeight / contentVA.size();
	//
	// Set<Integer> mouseOverElements = contentSelectionManager
	// .getElements(SelectionType.MOUSE_OVER);
	// Set<Integer> selectedElements = contentSelectionManager
	// .getElements(SelectionType.SELECTION);
	//
	// for (Integer mouseOverElement : mouseOverElements) {
	// int elementIndex = contentVA.indexOf(mouseOverElement);
	//
	// if (elementIndex != -1) {
	// gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
	// gl.glBegin(GL.GL_LINE_LOOP);
	// gl.glVertex3f(0,
	// overviewHeight - (elementIndex * sampleHeight), 0);
	// gl.glVertex3f(layout.getOverviewHeatmapWidth(), overviewHeight
	// - (elementIndex * sampleHeight), 0);
	// gl.glVertex3f(layout.getOverviewHeatmapWidth(), overviewHeight
	// - ((elementIndex + 1) * sampleHeight), 0);
	// gl.glVertex3f(0, overviewHeight
	// - ((elementIndex + 1) * sampleHeight), 0);
	// gl.glEnd();
	// }
	//
	// // selectedElements.remove(mouseOverElement);
	// }
	//
	// for (Integer selectedElement : selectedElements) {
	// int elementIndex = contentVA.indexOf(selectedElement);
	//
	// if (elementIndex != -1) {
	// gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
	// gl.glBegin(GL.GL_LINE_LOOP);
	// gl.glVertex3f(0,
	// overviewHeight - (elementIndex * sampleHeight), 0);
	// gl.glVertex3f(layout.getOverviewHeatmapWidth(), overviewHeight
	// - (elementIndex * sampleHeight), 0);
	// gl.glVertex3f(layout.getOverviewHeatmapWidth(), overviewHeight
	// - ((elementIndex + 1) * sampleHeight), 0);
	// gl.glVertex3f(0, overviewHeight
	// - ((elementIndex + 1) * sampleHeight), 0);
	// gl.glEnd();
	// }
	// }
	// }

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
				selectedGroups.put(group, null);
			} else {
				group.setSelectionType(SelectionType.NORMAL);
			}

			groupSampleStartIndex += group.getNrElements();
			groupIndex++;
		}
	}

	private Pair<Integer, Integer> getBoundaryIndicesOfElementsInFocus() {

		float sliderBottomPositionY = slider.getSliderBottomPositionY();
		float sliderHeight = slider.getSliderHeight();
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
		
		updateHeatMapTextures(null);
	}

	public HashMap<Group, Boolean> getSelectedGroups() {
		return selectedGroups;
	}

	public ArrayList<Texture> getClusterTextures(int index) {
		return overviewTextures.get(index);
	}

	public ContentVirtualArray getContentVA() {
		return contentVA;
	}

	public VerticalSlider getOverviewSlider() {
		return slider;
	}

	public void updateHeatMapTextures(
			ContentSelectionManager contentSelectionManager) {
		overviewTextures.clear();
		ContentGroupList groupList = contentVA.getGroupList();
		for(Group group : groupList) {
			ContentVirtualArray clusterVA = new ContentVirtualArray();

			for (int i = group.getStartIndex(); i <= group.getEndIndex(); i++) {
				if (i >= contentVA.size())
					break;
				clusterVA.append(contentVA.get(i));
			}
			overviewTextures.add(HeatMapUtil.createHeatMapTextures(set, clusterVA,
					storageVA, contentSelectionManager));
		}
	}

	public void setLayout(AHeatMapLayout layout) {
		this.layout = layout;
	}
}
