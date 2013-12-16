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

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.manage.GLLocation;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.EShowLabels;
import org.caleydo.view.heatmap.v2.ISpacingStrategy;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;
import org.caleydo.view.heatmap.v2.SpacingStrategies;

import com.google.common.primitives.Ints;
/**
 * utility class to render the selection of a heatmap as one ore more crosses
 *
 * @author Samuel Gratzl
 *
 */
public class DimensionRenderer {
	private final EDimension dim;
	private final IHeatMapDataProvider data;

	private EShowLabels label = EShowLabels.NONE;
	private ISpacingStrategy spacingStrategy = SpacingStrategies.UNIFORM;
	private ISpacingLayout spacing = null;
	protected IndexedId hoveredID = null;

	public DimensionRenderer(IHeatMapDataProvider data, EDimension dimension) {
		this.dim = dimension;
		this.data = data;
	}

	/**
	 * @return the spacingStrategy, see {@link #spacingStrategy}
	 */
	public ISpacingStrategy getSpacingStrategy() {
		return spacingStrategy;
	}

	/**
	 * @param spacingStrategy
	 *            setter, see {@link spacingStrategy}
	 */
	public void setSpacingStrategy(ISpacingStrategy spacingStrategy) {
		this.spacingStrategy = spacingStrategy;
	}

	/**
	 * @return the spacing, see {@link #spacing}
	 */
	public ISpacingLayout getSpacing() {
		return spacing;
	}

	/**
	 * @param labels
	 *            setter, see {@link labels}
	 */
	public void setLabel(EShowLabels labels) {
		this.label = labels;
	}

	public float minSize() {
		return spacingStrategy.minSize(size(), label.show());
	}

	public boolean isUniformSpacing() {
		return spacing.isUniform();
	}

	/**
	 * @return the labels, see {@link #label}
	 */
	public EShowLabels getLabel() {
		return label;
	}

	public int size() {
		return getData().size();
	}

	private void render(GLGraphics g, SelectionType selectionType, float w, float h) {
		List<Integer> indices = prepareRender(g, selectionType);
		final boolean isDimension = dim.isDimension();

		// dimension selection
		// if (isDimension) {
		// g.gl.glEnable(GL2.GL_LINE_STIPPLE);
		// g.gl.glLineStipple(2, (short) 0xAAAA);
		// }

		int lastIndex = -1;
		float x = 0;
		float wi = 0;
		for (int index : indices) {
			if (index != (lastIndex + 1)) // just the outsides
			{
				g.gl.glLineWidth(3);
				// flush previous
				if (isDimension)
					g.drawRect(x, 0, wi, h);
				else
					g.drawRect(0, x, w, wi);
				x = spacing.getPosition(index);
				wi = 0;
			}
			wi += spacing.getSize(index);
			lastIndex = index;
		}
		if (wi > 0)
			if (isDimension)
				g.drawRect(x, 0, wi, h);
			else
				g.drawRect(0, x, w, wi);

		// if (isDimension)
		// g.gl.glDisable(GL2.GL_LINE_STIPPLE);
	}

	private List<Integer> prepareRender(GLGraphics g, SelectionType selectionType) {
		Set<Integer> selectedSet = getManager().getElements(selectionType);

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
		SelectionManager m = getManager();
		List<Integer> indices = new ArrayList<>(selectedSet.size());
		for (Integer selectedColumn : selectedSet) {
			if (m.checkStatus(GLHeatMap.SELECTION_HIDDEN, selectedColumn))
				continue;
			int i = data.indexOf(dim, selectedColumn);
			if (i < 0)
				continue;
			indices.add(i);
		}
		Collections.sort(indices);
		return indices;
	}

	public void render(GLGraphics g, float w, float h) {
		render(g, SelectionType.SELECTION, w, h);
		render(g, SelectionType.MOUSE_OVER, w, h);
		g.lineWidth(1);
	}

	/**
	 * @return the data, see {@link #data}
	 */
	public List<Integer> getData() {
		return data.getData(dim);
	}

	/**
	 * @param x
	 */
	public void updateSpacing(float total) {
		spacing = spacingStrategy.apply(getData(), getManager(), total);
	}

	/**
	 * @return
	 */
	private SelectionManager getManager() {
		return data.getManager(dim);
	}

	/**
	 * @param index
	 */
	public GLLocation getLocation(int index, float textWidth) {
		if (spacing == null)
			return null;
		float pos = spacing.getPosition(index);
		if (label == EShowLabels.LEFT)
			pos += textWidth;
		return new GLLocation(pos, spacing.getSize(index));
	}

	/**
	 * @param position
	 * @param textWidth
	 * @return
	 */
	public int getIndex(float position, int textWidth) {
		if (spacing == null)
			return -1;
		if (label == EShowLabels.LEFT)
			position -= textWidth;
		return spacing.getIndex(position);
	}

	/**
	 * @param selection
	 * @param b
	 * @param first
	 * @return
	 */
	public void select(SelectionType selection, boolean clearExisting, IndexedId id) {
		if (id != null)
			select(SelectionType.SELECTION, clearExisting, false, id.getId());
		hoveredID = id;
	}

	private void select(SelectionType selectionType, boolean clearExisting, boolean deSelect,
			int... ids) {
		SelectionManager m = getManager();
		if (clearExisting)
			m.clearSelection(selectionType);

		if (ids.length > 0) {
			if (deSelect)
				m.removeFromType(selectionType, Ints.asList(ids));
			else
				m.addToType(selectionType, Ints.asList(ids));
		}
	}

	/**
	 * @return the hoveredID, see {@link #hoveredID}
	 */
	public IndexedId getHoveredID() {
		return hoveredID;
	}

	private Integer get(int index) {
		List<Integer> data = getData();
		if (index < 0 || index >= data.size())
			return null;
		return data.get(index);
	}

	public IndexedId getIndexedId(float v, int textWidth) {
		int index = getIndex(v, textWidth);
		Integer id = get(index);
		return new IndexedId(index, id);
	}

	private int[] toRange(IndexedId from, IndexedId to) {
		List<Integer> data = getData();
		int fIndex = from.getIndex();
		int tIndex = to.getIndex();
		final int length = Math.abs(fIndex - tIndex);
		int[] d = new int[length];
		int delta = fIndex < tIndex ? +1 : -1;
		int index = fIndex;
		for (int i = 0; i < length; ++i) {
			d[i] = data.get(index);
			index += delta;
		}
		return d;
	}

	/**
	 * @param first
	 */
	public void drag(IndexedId id) {
		int[] range = toRange(hoveredID, id);
		boolean selected = getManager().checkStatus(SelectionType.SELECTION, id.getId());
		select(SelectionType.SELECTION, false, selected, range);
		hoveredID = id;
	}
}
