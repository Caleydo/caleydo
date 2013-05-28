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
package org.caleydo.view.heatmap.v2;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.spacing.IRecordSpacingLayout;

import com.jogamp.common.util.IntIntHashMap;


public class SelectionRenderer {
	private final SelectionManager manager;
	private final TablePerspective tablePerspective;
	private final boolean isDimension;
	private final IntIntHashMap pickingIds;

	public SelectionRenderer(TablePerspective tablePerspective, SelectionManager manager, boolean isDimension, IntIntHashMap pickingIds) {
		this.tablePerspective = tablePerspective;
		this.manager = manager;
		this.isDimension = isDimension;
		this.pickingIds = pickingIds;
	}

	public void renderDimension(GLGraphics g, SelectionType selectionType, float h, IRecordSpacingLayout layout,
			boolean doPicking) {
		Set<Integer> selectedSet = manager.getElements(selectionType);

		if (!doPicking) {
			g.color(selectionType.getColor());
			if (selectionType == SelectionType.SELECTION) {
				g.lineWidth(SELECTED_LINE_WIDTH);
			} else if (selectionType == SelectionType.MOUSE_OVER) {
				g.lineWidth(MOUSE_OVER_LINE_WIDTH);
			}

			// dimension selection
			g.gl.glEnable(GL2.GL_LINE_STIPPLE);
			g.gl.glLineStipple(2, (short) 0xAAAA);
		}

		int columnIndex = 0;
		final float fieldWidth = layout.getFieldWidth();
		for (int tempColumn : tablePerspective.getDimensionPerspective().getVirtualArray()) {
			for (Integer selectedColumn : selectedSet) {
				if (tempColumn == selectedColumn) {
					// TODO we need indices of all elements

					float x = columnIndex * fieldWidth;

					if (doPicking)
						g.pushName(pickingIds.get(selectedColumn));
					g.drawRect(x, 0, fieldWidth, h);
					if (doPicking)
						g.popName();
				}
			}
			columnIndex++;
		}

		g.gl.glDisable(GL2.GL_LINE_STIPPLE);
	}

	public void renderRecord(GLGraphics g, SelectionType selectionType, float w, IRecordSpacingLayout layout,
			boolean doPicking) {
		// content selection
		Set<Integer> selectedSet = manager.getElements(selectionType);

		if (!doPicking) {
			g.color(selectionType.getColor());
			if (selectionType == SelectionType.SELECTION) {
				g.lineWidth(SELECTED_LINE_WIDTH);
			} else if (selectionType == SelectionType.MOUSE_OVER) {
				g.lineWidth(MOUSE_OVER_LINE_WIDTH);
			}
		}

		int lineIndex = 0;
		// FIXME this iterates over all elements but could do by only iterating
		// of the selected elements
		for (int recordIndex : tablePerspective.getRecordPerspective().getVirtualArray()) {
			if (manager.checkStatus(GLHeatMap.SELECTION_HIDDEN, recordIndex))
				continue;
			for (Integer currentLine : selectedSet) {
				if (currentLine == recordIndex) {
					float fieldHeight = layout.getFieldHeight(recordIndex);
					// width = heatMap.getDimensionVA().size() * fieldWidth;
					float y = layout.getYPosition(lineIndex);
					float x = 0;

					if (doPicking)
						g.pushName(pickingIds.get(currentLine));
					g.drawRect(x, y, w, fieldHeight);
					if (doPicking)
						g.popName();
				}
			}
			lineIndex++;
		}

	}

	public void render(GLGraphics g, float w, float h, IRecordSpacingLayout layout, boolean doPicking) {
		if (isDimension) {
			renderDimension(g, SelectionType.SELECTION, h, layout, doPicking);
			renderDimension(g, SelectionType.MOUSE_OVER, h, layout, doPicking);
		} else {
			renderRecord(g, SelectionType.SELECTION, w, layout, doPicking);
			renderRecord(g, SelectionType.MOUSE_OVER, w, layout, doPicking);
		}
	}
}
