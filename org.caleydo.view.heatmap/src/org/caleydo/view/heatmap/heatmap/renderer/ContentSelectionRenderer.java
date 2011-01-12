package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class ContentSelectionRenderer extends AContentRenderer {

	public ContentSelectionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	public void renderSelection(final GL2 gl, SelectionType selectionType) {

		// content selection
		Set<Integer> selectedSet = heatMap.getContentSelectionManager().getElements(
				selectionType);
		float width = x;
		float yPosition = y;
		float xPosition = 0;

		gl.glColor4fv(selectionType.getColor(), 0);
		gl.glLineWidth(SELECTED_LINE_WIDTH);

		int lineIndex = 0;
		// FIXME this iterates over all elements but could do by only iterating
		// of the selected elements
		for (int contentIndex : heatMap.getContentVA()) {
			if (heatMap.getContentSelectionManager().checkStatus(
					GLHeatMap.SELECTION_HIDDEN, contentIndex))
				continue;
			for (Integer currentLine : selectedSet) {
				if (currentLine == contentIndex) {
					float fieldHeight = contentSpacing.getFieldHeight(contentIndex);
					// width = heatMap.getStorageVA().size() * fieldWidth;
					yPosition = contentSpacing.getYDistances().get(lineIndex);
					xPosition = 0;
					gl.glPushName(heatMap.getPickingManager().getPickingID(
							heatMap.getID(), EPickingType.HEAT_MAP_LINE_SELECTION,
							currentLine));

					float z = SELECTION_Z * selectionType.getPriority();
					
					gl.glBegin(GL2.GL_LINE_LOOP);
					gl.glVertex3f(xPosition, yPosition, z);
					gl.glVertex3f(xPosition, yPosition + fieldHeight, z);
					gl.glVertex3f(xPosition + width, yPosition + fieldHeight,z);
					gl.glVertex3f(xPosition + width, yPosition, z);
					gl.glEnd();
					gl.glPopName();
				}
			}
			lineIndex++;
		}

	}

	@Override
	public void render(GL2 gl) {	
		renderSelection(gl, SelectionType.MOUSE_OVER);
		renderSelection(gl, SelectionType.SELECTION);
	}
}
