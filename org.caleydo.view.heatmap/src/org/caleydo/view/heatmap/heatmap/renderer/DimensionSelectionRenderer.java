/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.heatmap.renderer;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import static org.caleydo.view.heatmap.HeatMapRenderStyle.SELECTION_Z;

import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;

public class DimensionSelectionRenderer extends AHeatMapRenderer {

	public DimensionSelectionRenderer(GLHeatMap heatMap) {
		super(heatMap);
	}

	public void renderSelection(final GL2 gl, SelectionType selectionType) {

		// content selection
		Set<Integer> selectedSet = heatMap.getRecordSelectionManager().getElements(
				selectionType);
		// float width = x;
		// float yPosition = y;
		float xPosition = 0;

		if (selectionType == SelectionType.SELECTION) {
			gl.glColor4fv(SelectionType.SELECTION.getColor().getRGBA(), 0);
			gl.glLineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			gl.glColor4fv(SelectionType.MOUSE_OVER.getColor().getRGBA(), 0);
			gl.glLineWidth(MOUSE_OVER_LINE_WIDTH);
		}

		// dimension selection
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glLineStipple(2, (short) 0xAAAA);

		selectedSet = heatMap.getDimensionSelectionManager().getElements(selectionType);
		int columnIndex = 0;
		for (int tempColumn : heatMap.getTablePerspective().getDimensionPerspective()
				.getVirtualArray()) {
			for (Integer selectedColumn : selectedSet) {
				if (tempColumn == selectedColumn) {
					// TODO we need indices of all elements

					xPosition = columnIndex * recordSpacing.getFieldWidth();

					float z = SELECTION_Z * selectionType.getPriority();

					gl.glPushName(heatMap.getPickingManager().getPickingID(
							heatMap.getID(), PickingType.HEAT_MAP_DIMENSION_SELECTION,
							selectedColumn));
					gl.glBegin(GL.GL_LINE_LOOP);
					gl.glVertex3f(xPosition, y, z);
					gl.glVertex3f(xPosition, 0, z);
					gl.glVertex3f(xPosition + recordSpacing.getFieldWidth(), 0, z);
					gl.glVertex3f(xPosition + recordSpacing.getFieldWidth(), y, z);
					gl.glEnd();
					gl.glPopName();
				}
			}
			columnIndex++;
		}

		gl.glDisable(GL2.GL_LINE_STIPPLE);
	}

	@Override
	public void renderContent(GL2 gl) {
		renderSelection(gl, SelectionType.SELECTION);
		renderSelection(gl, SelectionType.MOUSE_OVER);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
