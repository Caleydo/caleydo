package org.caleydo.view.matchmaker;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.heatmap.hierarchical.HeatMapUtil;
import org.caleydo.view.matchmaker.layout.AHeatMapLayout;

import com.jogamp.opengl.util.texture.Texture;

public class HeatMapOverview {

	private AHeatMapLayout layout;
	private VerticalSlider slider;
	private ArrayList<ArrayList<Texture>> overviewTextures;
	private DataTable table;
	private RecordVirtualArray recordVA;
	private DimensionVirtualArray dimensionVA;
	private HashMap<Group, Boolean> selectedGroups;

	public HeatMapOverview(AHeatMapLayout layout) {
		this.layout = layout;
		slider = new VerticalSlider(layout);
		selectedGroups = new HashMap<Group, Boolean>();
		overviewTextures = new ArrayList<ArrayList<Texture>>();
	}

	public void draw(GL2 gl, TextureManager textureManager, PickingManager pickingManager,
			RecordSelectionManager contentSelectionManager, int viewID,
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
		// HeatMapUtil.renderGroupBar(gl, recordVA, layout.getOverviewHeight(),
		// layout.getOverviewGroupWidth(), pickingManager, viewID, layout
		// .getGroupPickingType(), textureManager);
		//
		// gl.glPopMatrix();

		// HeatMapUtil.renderGroupBar(gl, recordVA, layout.getOverviewHeight(),
		// layout.getOverviewGroupWidth(), pickingManager, viewID, layout
		// .getGroupPickingType(), textureManager);
		//
		// //FIXME: remove this. the group borders need to be actually put in
		// the heatmap texture
		// // Render group borders over heatmap texture
		// ContentGroupList contentGroupList = recordVA.getGroupList();
		// boolean isLeft = true;
		// if (layout.getOverviewHeatMapPosition().x() <
		// layout.getDetailPosition().x())
		// isLeft = false;
		//
		// if (contentGroupList != null) {
		// float sampleHeight = layout.getOverviewHeight() / ((float)
		// recordVA.size());
		// float groupPositionY = layout.getOverviewHeight();
		//
		// gl.glColor4f(1, 1, 1, 1);
		// gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		// int groupIndex = 0;
		// for (Group group : contentGroupList) {
		// int numSamplesGroup = group.getNrElements();
		// float groupHeight = numSamplesGroup * sampleHeight;
		// // gl.glLineWidth(1);
		// gl.glBegin(GL2.GL_LINE_LOOP);
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

	// private void drawSelections(GL2 gl,
	// ContentSelectionManager contentSelectionManager) {
	//
	// float overviewHeight = layout.getOverviewHeight();
	// float sampleHeight = overviewHeight / recordVA.size();
	//
	// Set<Integer> mouseOverElements = contentSelectionManager
	// .getElements(SelectionType.MOUSE_OVER);
	// Set<Integer> selectedElements = contentSelectionManager
	// .getElements(SelectionType.SELECTION);
	//
	// for (Integer mouseOverElement : mouseOverElements) {
	// int elementIndex = recordVA.indexOf(mouseOverElement);
	//
	// if (elementIndex != -1) {
	// gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
	// gl.glBegin(GL2.GL_LINE_LOOP);
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
	// int elementIndex = recordVA.indexOf(selectedElement);
	//
	// if (elementIndex != -1) {
	// gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
	// gl.glBegin(GL2.GL_LINE_LOOP);
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

	public boolean handleDragging(GL2 gl, GLMouseListener glMouseListener) {

		if (slider.handleDragging(gl, glMouseListener)) {
			Pair<Integer, Integer> bounds = getBoundaryIndicesOfElementsInFocus();
			selectGroupsAccordingToBoundIndices(bounds.getFirst(), bounds.getSecond());
			return true;
		}

		return false;
	}

	private void selectGroupsAccordingToBoundIndices(int lowerBoundIndex,
			int upperBoundIndex) {

		RecordGroupList contentGroupList = recordVA.getGroupList();
		int groupSampleStartIndex = 0;
		int groupSampleEndIndex = 0;
		int groupIndex = 0;
		selectedGroups.clear();
		for (Group group : contentGroupList) {
			groupSampleEndIndex = groupSampleStartIndex + group.getSize() - 1;
			if (groupSampleStartIndex >= lowerBoundIndex
					&& groupSampleEndIndex <= upperBoundIndex) {
				group.setSelectionType(SelectionType.SELECTION);
				selectedGroups.put(group, null);
			} else {
				group.setSelectionType(SelectionType.NORMAL);
			}

			groupSampleStartIndex += group.getSize();
			groupIndex++;
		}
	}

	private Pair<Integer, Integer> getBoundaryIndicesOfElementsInFocus() {

		float sliderBottomPositionY = slider.getSliderBottomPositionY();
		float sliderHeight = slider.getSliderHeight();
		float sliderTopPositionY = sliderBottomPositionY + sliderHeight;
		float overviewHeight = layout.getOverviewHeight();
		float sampleHeight = overviewHeight / ((float) recordVA.size());

		int numSamplesInFocus = (int) Math.ceil(sliderHeight / sampleHeight);
		int sampleIndexTop = (int) Math
				.floor((overviewHeight - (sliderTopPositionY - layout
						.getOverviewPosition().y())) / sampleHeight);
		if (sampleIndexTop < 0)
			sampleIndexTop = 0;
		int sampleIndexBottom = sampleIndexTop + numSamplesInFocus;

		if (sampleIndexBottom >= recordVA.size()) {
			sampleIndexBottom = recordVA.size() - 1;
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

	public void handleSliderSelection(PickingType pickingType, PickingMode pickingMode) {
		slider.handleSliderSelection(pickingType, pickingMode);
	}

	public void setTable(DataTable table) {
		this.table = table;
		recordVA = table.getRecordData(DataTable.RECORD).getRecordVA();
		dimensionVA = table.getDimensionData(DataTable.DIMENSION).getDimensionVA();

		updateHeatMapTextures(null);
	}

	public HashMap<Group, Boolean> getSelectedGroups() {
		return selectedGroups;
	}

	public ArrayList<Texture> getClusterTextures(int index) {
		return overviewTextures.get(index);
	}

	public RecordVirtualArray getRecordVA() {
		return recordVA;
	}

	public VerticalSlider getOverviewSlider() {
		return slider;
	}

	public void updateHeatMapTextures(RecordSelectionManager contentSelectionManager) {
		overviewTextures.clear();
		RecordGroupList groupList = recordVA.getGroupList();
		for (Group group : groupList) {
			RecordVirtualArray clusterVA = new RecordVirtualArray();

			for (int i = group.getStartIndex(); i <= group.getEndIndex(); i++) {
				if (i >= recordVA.size())
					break;
				clusterVA.append(recordVA.get(i));
			}
			overviewTextures.add(HeatMapUtil.createHeatMapTextures(table, clusterVA,
					dimensionVA, contentSelectionManager));
		}
	}

	public void setLayout(AHeatMapLayout layout) {
		this.layout = layout;
	}
}
