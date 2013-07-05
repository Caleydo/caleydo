/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
			gl.glColor4fv(SelectionType.SELECTION.getColor(), 0);
			gl.glLineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			gl.glColor4fv(SelectionType.MOUSE_OVER.getColor(), 0);
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
