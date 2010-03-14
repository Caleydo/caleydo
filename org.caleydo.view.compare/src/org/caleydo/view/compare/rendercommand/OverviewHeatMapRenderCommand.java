package org.caleydo.view.compare.rendercommand;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.heatmap.HeatMapUtil;

import com.sun.opengl.util.texture.Texture;

public class OverviewHeatMapRenderCommand implements IHeatMapRenderCommand {

	@Override
	public void render(GL gl, HeatMapWrapper heatMapWrapper) {
		
		AHeatMapLayout layout = heatMapWrapper.getLayout();
		Vec3f overviewHeatMapPosition = layout.getOverviewHeatMapPosition();
		float overviewHeight = layout.getOverviewHeight();
		ArrayList<Texture> overviewTextures = heatMapWrapper.getOverview()
				.getHeatMapTextures();
		ContentSelectionManager contentSelectionManager = heatMapWrapper
				.getContentSelectionManager();
		ContentVirtualArray contentVA = heatMapWrapper.getOverview()
				.getContentVA();

		gl.glPushMatrix();
		gl.glTranslatef(overviewHeatMapPosition.x(), overviewHeatMapPosition
				.y(), overviewHeatMapPosition.z());
		HeatMapUtil.renderHeatmapTextures(gl, overviewTextures, overviewHeight,
				layout.getOverviewHeatmapWidth());
		drawSelections(gl, layout, contentSelectionManager, contentVA);

		gl.glPopMatrix();

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

		float overviewHeight = layout.getOverviewHeight();
		float sampleHeight = overviewHeight / contentVA.size();

		for (Integer selectedElement : selectedElements) {
			int elementIndex = contentVA.indexOf(selectedElement);

			if (elementIndex != -1) {
				gl.glColor4fv(selectionType.getColor(), 0);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex3f(0,
						overviewHeight - (elementIndex * sampleHeight), 0);
				gl.glVertex3f(layout.getOverviewHeatmapWidth(), overviewHeight
						- (elementIndex * sampleHeight), 0);
				gl.glVertex3f(layout.getOverviewHeatmapWidth(), overviewHeight
						- ((elementIndex + 1) * sampleHeight), 0);
				gl.glVertex3f(0, overviewHeight
						- ((elementIndex + 1) * sampleHeight), 0);
				gl.glEnd();
			}

		}
	}

	@Override
	public ERenderCommandType getRenderCommandType() {
		return ERenderCommandType.OVERVIEW_HEATMAP;
	}

}
