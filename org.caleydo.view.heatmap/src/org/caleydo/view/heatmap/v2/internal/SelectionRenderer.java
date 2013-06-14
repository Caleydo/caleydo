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
package org.caleydo.view.heatmap.v2.internal;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;


public class SelectionRenderer {
	private final SelectionManager manager;
	private final TablePerspective tablePerspective;
	private final boolean isDimension;

	public SelectionRenderer(TablePerspective tablePerspective, SelectionManager manager, boolean isDimension) {
		this.tablePerspective = tablePerspective;
		this.manager = manager;
		this.isDimension = isDimension;
	}

	public void renderDimension(GLGraphics g, SelectionType selectionType, float h, ISpacingLayout layout) {
		Set<Integer> selectedSet = manager.getElements(selectionType);

		g.color(selectionType.getColor());
		if (selectionType == SelectionType.SELECTION) {
			g.lineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			g.lineWidth(MOUSE_OVER_LINE_WIDTH);
		}

		// dimension selection
		g.gl.glEnable(GL2.GL_LINE_STIPPLE);
		g.gl.glLineStipple(2, (short) 0xAAAA);

		VirtualArray va = tablePerspective.getDimensionPerspective().getVirtualArray();
		for (Integer selectedColumn : selectedSet) {
			if (manager.checkStatus(GLHeatMap.SELECTION_HIDDEN, selectedColumn))
				continue;
			int i = va.indexOf(selectedColumn);
			if (i < 0)
				continue;
			float x = layout.getPosition(i);
			float fieldWidth = layout.getSize(i);

			g.drawRect(x, 0, fieldWidth, h);
		}

		g.gl.glDisable(GL2.GL_LINE_STIPPLE);
	}

	public void renderRecord(GLGraphics g, SelectionType selectionType, float w, ISpacingLayout layout) {
		// content selection
		Set<Integer> selectedSet = manager.getElements(selectionType);

		g.color(selectionType.getColor());
		if (selectionType == SelectionType.SELECTION) {
			g.lineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			g.lineWidth(MOUSE_OVER_LINE_WIDTH);
		}

		VirtualArray va = tablePerspective.getRecordPerspective().getVirtualArray();
		for (Integer selectedRow : selectedSet) {
			if (manager.checkStatus(GLHeatMap.SELECTION_HIDDEN, selectedRow))
				continue;
			int i = va.indexOf(selectedRow);
			if (i < 0)
				continue;
			float fieldHeight = layout.getSize(i);
			float y = layout.getPosition(i);

			g.drawRect(0, y, w, fieldHeight);
		}
	}

	public void render(GLGraphics g, float w, float h, ISpacingLayout layout) {
		if (isDimension) {
			renderDimension(g, SelectionType.SELECTION, h, layout);
			renderDimension(g, SelectionType.MOUSE_OVER, h, layout);
		} else {
			renderRecord(g, SelectionType.SELECTION, w, layout);
			renderRecord(g, SelectionType.MOUSE_OVER, w, layout);
		}
	}
}
