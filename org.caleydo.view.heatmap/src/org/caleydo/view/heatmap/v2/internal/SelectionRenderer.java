/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2.internal;

import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;


/**
 * utility class to render the selection of a heatmap as one ore more crosses
 *
 * @author Samuel Gratzl
 *
 */
public class SelectionRenderer {
	private final SelectionManager manager;
	private final Perspective perspective;
	private final boolean isDimension;

	public SelectionRenderer(Perspective perspective, SelectionManager manager, boolean isDimension) {
		this.perspective = perspective;
		this.manager = manager;
		this.isDimension = isDimension;
	}

	private void render(GLGraphics g, SelectionType selectionType, float w, float h, ISpacingLayout layout) {
		List<Integer> indices = prepareRender(g, selectionType);

		// dimension selection
		if (isDimension) {
			g.gl.glEnable(GL2.GL_LINE_STIPPLE);
			g.gl.glLineStipple(2, (short) 0xAAAA);
		}

		int lastIndex = -1;
		float x = 0;
		float wi = 0;
		for (int index : indices) {
			// if (index != (lastIndex + 1)) //just the outsides
			{
				// flush previous
				if (isDimension)
					g.drawRect(x, 0, wi, h);
				else
					g.drawRect(0, x, w, wi);
				x = layout.getPosition(index);
				wi = 0;
			}
			wi += layout.getSize(index);
			lastIndex = index;
		}
		if (wi > 0)
			if (isDimension)
				g.drawRect(x, 0, wi, h);
			else
				g.drawRect(0, x, w, wi);

		if (isDimension)
			g.gl.glDisable(GL2.GL_LINE_STIPPLE);
	}

	private List<Integer> prepareRender(GLGraphics g, SelectionType selectionType) {
		Set<Integer> selectedSet = manager.getElements(selectionType);

		g.color(selectionType.getColor());
		if (selectionType == SelectionType.SELECTION) {
			g.lineWidth(SELECTED_LINE_WIDTH);
		} else if (selectionType == SelectionType.MOUSE_OVER) {
			g.lineWidth(MOUSE_OVER_LINE_WIDTH);
		}
		List<Integer> indices = toIndices(selectedSet);
		return indices;
	}

	private List<Integer> toIndices(Set<Integer> selectedSet) {
		VirtualArray va = perspective.getVirtualArray();
		List<Integer> indices = new ArrayList<>(selectedSet.size());
		for (Integer selectedColumn : selectedSet) {
			if (manager.checkStatus(GLHeatMap.SELECTION_HIDDEN, selectedColumn))
				continue;
			int i = va.indexOf(selectedColumn);
			if (i < 0)
				continue;
			indices.add(i);
		}
		Collections.sort(indices);
		return indices;
	}

	public void render(GLGraphics g, float w, float h, ISpacingLayout layout) {
		render(g, SelectionType.SELECTION, w, h, layout);
		render(g, SelectionType.MOUSE_OVER, w, h, layout);
		g.lineWidth(1);
	}
}
