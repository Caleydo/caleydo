package org.caleydo.view.compare.rendercommand;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.heatmap.hierarchical.HeatMapUtil;

import com.sun.opengl.util.texture.Texture;

public class OverviewHeatMapRenderCommand implements IHeatMapRenderCommand {

	private PickingManager pickingManager;
	private int viewID;

	public OverviewHeatMapRenderCommand(int viewID,
			PickingManager pickingManager) {
		this.viewID = viewID;
		this.pickingManager = pickingManager;
	}

	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {

		AHeatMapLayout layout = heatMapWrapper.getLayout();

		ContentSelectionManager contentSelectionManager = heatMapWrapper
				.getContentSelectionManager();
		ContentVirtualArray contentVA = heatMapWrapper.getOverview()
				.getContentVA();
		ContentGroupList contentGroupList = contentVA.getGroupList();

		for (int i = 0; i < contentGroupList.size(); i++) {
			Vec3f overviewHeatMapGroupPosition = layout
					.getOverviewHeatMapGroupPosition(i);
			float height = layout.getOverviewHeatMapGroupHeight(i);

			gl.glPushName(pickingManager.getPickingID(viewID, layout
					.getGroupPickingType(), i));
			gl.glPushMatrix();
			gl.glTranslatef(overviewHeatMapGroupPosition.x(),
					overviewHeatMapGroupPosition.y(),
					overviewHeatMapGroupPosition.z());

			ArrayList<Texture> overviewTextures = heatMapWrapper.getOverview()
					.getClusterTextures(i);
			HeatMapUtil.renderHeatmapTextures(gl, overviewTextures, height,
					layout.getOverviewHeatMapWidth());
			gl.glPopMatrix();
			gl.glPopName();
		}

		drawSelections(gl, layout, contentSelectionManager, contentVA);

	}

	private void drawSelections(GL gl, AHeatMapLayout layout,
			ContentSelectionManager contentSelectionManager,
			ContentVirtualArray contentVA) {

		Set<Integer> mouseOverElements = contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		Set<Integer> selectedElements = contentSelectionManager
				.getElements(SelectionType.SELECTION);

		drawSelectionsOfType(gl, layout, contentVA, mouseOverElements,
				SelectionType.MOUSE_OVER);
		drawSelectionsOfType(gl, layout, contentVA, selectedElements,
				SelectionType.SELECTION);

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
	}

	private void drawSelectionsOfType(GL gl, AHeatMapLayout layout,
			ContentVirtualArray contentVA, Set<Integer> selectedElements,
			SelectionType selectionType) {

		float sampleHeight = layout.getOverviewHeatMapSampleHeight();
		float overviewHeatMapWidth = layout.getOverviewHeatMapWidth();

		for (Integer selectedElement : selectedElements) {
			int contentIndex = contentVA.indexOf(selectedElement);

			if (contentIndex != -1) {
				float positionY = layout
						.getOverviewHeatMapSamplePositionY(contentIndex);
				Vec3f overviewHeatMapPosition = layout
						.getOverviewHeatMapPosition();
				gl.glColor4fv(selectionType.getColor(), 0);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(overviewHeatMapPosition.x(), positionY,
						overviewHeatMapPosition.z());
				gl.glVertex3f(overviewHeatMapPosition.x()
						+ overviewHeatMapWidth, positionY,
						overviewHeatMapPosition.z());
				gl.glVertex3f(overviewHeatMapPosition.x()
						+ overviewHeatMapWidth, positionY + sampleHeight,
						overviewHeatMapPosition.z());
				gl.glVertex3f(overviewHeatMapPosition.x(), positionY
						+ sampleHeight, overviewHeatMapPosition.z());
				gl.glEnd();
			}

		}
	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.OVERVIEW_HEATMAP;
	}

}
