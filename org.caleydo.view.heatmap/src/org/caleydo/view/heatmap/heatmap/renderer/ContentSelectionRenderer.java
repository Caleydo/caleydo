package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;

import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class ContentSelectionRenderer extends AContentRenderer {

	public ContentSelectionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	public void renderSelection(final GL gl, SelectionType selectionType) {

		// content selection
		Set<Integer> selectedSet = heatMap.getContentSelectionManager()
				.getElements(selectionType);
		float width = x;
		float yPosition = y;
		float xPosition = 0;

		if (selectionType == SelectionType.SELECTION) {
			gl.glColor4fv(SELECTED_COLOR, 0);
			gl.glLineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			gl.glColor4fv(MOUSE_OVER_COLOR, 0);
			gl.glLineWidth(MOUSE_OVER_LINE_WIDTH);
		}

		int lineIndex = 0;
		for (int tempLine : heatMap.getContentVA()) {
			for (Integer currentLine : selectedSet) {
				if (currentLine == tempLine) {
					// width = heatMap.getStorageVA().size() * fieldWidth;
					yPosition = contentSpacing.yDistances.get(lineIndex);
					xPosition = 0;
					gl.glPushName(heatMap.getPickingManager().getPickingID(
							heatMap.getID(),
							EPickingType.HEAT_MAP_LINE_SELECTION, currentLine));

					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(xPosition, yPosition, SELECTION_Z);
					gl.glVertex3f(xPosition, yPosition + selectedFieldHeight,
							SELECTION_Z);
					gl.glVertex3f(xPosition + width, yPosition
							+ selectedFieldHeight, SELECTION_Z);
					gl.glVertex3f(xPosition + width, yPosition, SELECTION_Z);
					gl.glEnd();
					gl.glPopName();
				}
			}
			lineIndex++;
		}

	}

	@Override
	public void render(GL gl) {
		renderSelection(gl, SelectionType.SELECTION);
		renderSelection(gl, SelectionType.MOUSE_OVER);

	}

}
