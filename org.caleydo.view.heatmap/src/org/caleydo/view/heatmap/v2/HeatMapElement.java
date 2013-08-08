/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;
import org.caleydo.view.heatmap.v2.internal.HeatMapTextureRenderer;
import org.caleydo.view.heatmap.v2.internal.SelectionRenderer;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

/**
 * a generic heat map implemenation
 *
 * @author Samuel Gratzl
 *
 */
public class HeatMapElement extends ASingleTablePerspectiveElement {

	private static final int TEXT_OFFSET = 5;
	/**
	 * maximum pixel size of a text
	 */
	private static final int MAX_TEXT_HEIGHT = 12;

	private final static int TEXT_WIDTH = 80; // [px]

	/**
	 * position of a the heat map text
	 *
	 * @author Samuel Gratzl
	 *
	 */
	public enum EShowLabels {
		NONE, LEFT, RIGHT;

		public boolean show() {
			return this != NONE;
		}
	}

	/** hide elements with the state {@link #SELECTION_HIDDEN} if this is true */
	private boolean hideElements = true;

	private final SelectionRenderer recordSelectionRenderer;
	private final SelectionRenderer dimensionSelectionRenderer;

	private ISpacingStrategy recordSpacingStrategy = SpacingStrategies.UNIFORM;
	private ISpacingStrategy dimensionSpacingStrategy = SpacingStrategies.UNIFORM;

	private ISpacingLayout recordSpacing = null;
	private ISpacingLayout dimensionSpacing = null;

	/**
	 * strategy to render a single field in the heat map
	 */
	private final IBlockColorer blockColorer;

	private final HeatMapTextureRenderer textureRenderer;

	private EShowLabels dimensionLabels = EShowLabels.NONE;
	private EShowLabels recordLabels = EShowLabels.NONE;

	private IndexedId hoveredRecordID = null;
	private IndexedId hoveredDimensionID = null;

	public HeatMapElement(TablePerspective tablePerspective) {
		this(tablePerspective, BasicBlockColorer.INSTANCE, EDetailLevel.HIGH, false);
	}

	public HeatMapElement(TablePerspective tablePerspective, IBlockColorer blockColorer, EDetailLevel detailLevel,
			boolean forceTextures) {
		super(tablePerspective);
		blockColorer = Objects.firstNonNull(blockColorer, BasicBlockColorer.INSTANCE);
		detailLevel = Objects.firstNonNull(detailLevel, EDetailLevel.LOW);

		Preconditions.checkNotNull(blockColorer, "need a valid renderer");

		this.blockColorer = blockColorer;
		this.dimensionSelectionRenderer = new SelectionRenderer(tablePerspective.getDimensionPerspective(),
				selections.getDimensionSelectionManager(),
				true);
		this.recordSelectionRenderer = new SelectionRenderer(tablePerspective.getRecordPerspective(),
				selections.getRecordSelectionManager(),
 false);

		switch (detailLevel) {
		case HIGH:
		case MEDIUM:
			setVisibility(EVisibility.PICKABLE); //pickable + no textures
			break;
		default:
			setVisibility(EVisibility.VISIBLE); // not pickable + textures
			break;
		}
		// force texture or low details
		if (forceTextures || EDetailLevel.MEDIUM.compareTo(detailLevel) > 0) {
			this.textureRenderer = new HeatMapTextureRenderer(tablePerspective, blockColorer);
		} else {
			this.textureRenderer = null;
		}
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		onVAUpdate(selections.getTablePerspective());
	}

	@Override
	protected void takeDown() {
		if (textureRenderer != null)
			textureRenderer.takeDown();
		super.takeDown();
	}

	/**
	 * @return the recommended min size of this heatmap
	 */
	@Override
	public Vec2f getMinSize() {
		TablePerspective tablePerspective = selections.getTablePerspective();
		float w = tablePerspective.getNrDimensions() * (dimensionLabels.show() ? 16 : 1);
		float h = tablePerspective.getNrRecords() * (recordLabels.show() ? 16 : 1);
		if (recordLabels.show())
			w += TEXT_WIDTH;
		if (dimensionLabels.show())
			h += TEXT_WIDTH;
		return new Vec2f(w, h);
	}

	/**
	 * @param showDimensionLabels
	 *            setter, see {@link showDimensionLabels}
	 */
	public void setDimensionLabels(EShowLabels value) {
		if (this.dimensionLabels == value)
			return;
		this.dimensionLabels = value;
		relayout();
		relayoutParent();
	}

	/**
	 * @param showRecordLabels
	 *            setter, see {@link showRecordLabels}
	 */
	public void setRecordLabels(EShowLabels value) {
		if (this.recordLabels == value)
			return;
		this.recordLabels = value;
		relayout();
		relayoutParent();
	}

	/**
	 * @return the recordLabels, see {@link #recordLabels}
	 */
	public EShowLabels getRecordLabels() {
		return recordLabels;
	}

	/**
	 * @return the dimensionLabels, see {@link #dimensionLabels}
	 */
	public EShowLabels getDimensionLabels() {
		return dimensionLabels;
	}

	/**
	 * @param recordSpacingStrategy
	 *            setter, see {@link recordSpacingStrategy}
	 */
	public void setRecordSpacingStrategy(ISpacingStrategy recordSpacingStrategy) {
		if (this.recordSpacingStrategy == recordSpacingStrategy)
			return;
		this.recordSpacingStrategy = recordSpacingStrategy;
		relayout();
	}

	/**
	 * @param dimensionSpacingStrategy
	 *            setter, see {@link dimensionSpacingStrategy}
	 */
	public void setDimensionSpacingStrategy(ISpacingStrategy dimensionSpacingStrategy) {
		if (this.dimensionSpacingStrategy == dimensionSpacingStrategy)
			return;
		this.dimensionSpacingStrategy = dimensionSpacingStrategy;
		relayout();
	}

	@Override
	public void onVAUpdate(TablePerspective tablePerspective) {
		if (textureRenderer != null) {
			textureRenderer.create(context);
		}
		repaintAll();
		relayoutParent();
	}

	@Override
	protected void layoutImpl() {
		Vec2f size = getSize().copy();
		if (recordLabels.show()) {
			size.setX(size.x() - TEXT_WIDTH);
		}
		if (dimensionLabels.show()) {
			size.setY(size.y() - TEXT_WIDTH);
		}
		// compute the layout
		this.recordSpacing = recordSpacingStrategy.apply(selections.getTablePerspective().getRecordPerspective(),
				selections.getRecordSelectionManager(), isHideElements(), size.y());
		this.dimensionSpacing = dimensionSpacingStrategy.apply(selections.getTablePerspective().getDimensionPerspective(),
				selections.getDimensionSelectionManager(), isHideElements(), size.x());
	}

	public CellSpace getDimensionCellSpace(int index) {
		if (dimensionSpacing == null)
			return null;
		float pos = dimensionSpacing.getPosition(index);
		if (recordLabels == EShowLabels.LEFT)
			pos += TEXT_WIDTH;
		return new CellSpace(pos, dimensionSpacing.getSize(index));
	}

	public CellSpace getRecordCellSpace(int index) {
		if (recordSpacing == null)
			return null;
		float pos = recordSpacing.getPosition(index);
		if (dimensionLabels == EShowLabels.LEFT)
			pos += TEXT_WIDTH;
		return new CellSpace(pos, recordSpacing.getSize(index));
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.save();
		switch (recordLabels) {
		case LEFT:
			w -= TEXT_WIDTH;
			g.move(TEXT_WIDTH, 0);
			break;
		case RIGHT:
			w -= TEXT_WIDTH;
			break;
		default:
			break;
		}
		switch (dimensionLabels) {
		case LEFT:
			h -= TEXT_WIDTH;
			g.move(0, TEXT_WIDTH);
			break;
		case RIGHT:
			h -= TEXT_WIDTH;
			break;
		default:
			break;
		}

		if (recordLabels.show()) {
			final TablePerspective tablePerspective = selections.getTablePerspective();
			final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
			final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

			for (int i = 0; i < recordVA.size(); ++i) {
				Integer recordID = recordVA.get(i);
				if (isHidden(recordID)) {
					continue;
				}
				float y = recordSpacing.getPosition(i);
				float fieldHeight = recordSpacing.getSize(i);
				float textHeight = Math.min(fieldHeight, MAX_TEXT_HEIGHT);
				String text = dataDomain.getRecordLabel(recordID);
				if (recordLabels == EShowLabels.LEFT)
					g.drawText(text, -TEXT_WIDTH, y + (fieldHeight - textHeight) * 0.5f, TEXT_WIDTH - TEXT_OFFSET,
							textHeight, VAlign.RIGHT);
				else
					g.drawText(text, w + TEXT_OFFSET, y + (fieldHeight - textHeight) * 0.5f, TEXT_WIDTH - TEXT_OFFSET,
							textHeight, VAlign.LEFT);
			}
		}

		if (dimensionLabels.show()) {
			final TablePerspective tablePerspective = selections.getTablePerspective();
			final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
			final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

			g.save();
			g.gl.glRotatef(-90, 0, 0, 1);
			for (int i = 0; i < dimensionVA.size(); ++i) {
				Integer dimensionID = dimensionVA.get(i);
				String label = dataDomain.getDimensionLabel(dimensionID);
				float x = dimensionSpacing.getPosition(i);
				float fieldWidth = dimensionSpacing.getSize(i);
				float textWidth = Math.min(fieldWidth, MAX_TEXT_HEIGHT);

				if (dimensionLabels == EShowLabels.LEFT)
					g.drawText(label, TEXT_OFFSET, x + (fieldWidth - textWidth) * 0.5f, TEXT_WIDTH - TEXT_OFFSET,
							textWidth, VAlign.LEFT);
				else
					g.drawText(label, -h - TEXT_WIDTH, x + (fieldWidth - textWidth) * 0.5f, TEXT_WIDTH - TEXT_OFFSET,
							textWidth, VAlign.RIGHT);
			}
			g.restore();
		}

		if (textureRenderer != null)
			textureRenderer.render(g, w, h);
		else
			render(g, w, h);

		g.incZ();
		recordSelectionRenderer.render(g, w, h, recordSpacing);
		dimensionSelectionRenderer.render(g, w, h, dimensionSpacing);
		g.decZ();

		g.restore();
	}

	/**
	 * render the heatmap as blocks
	 *
	 * @param g
	 * @param w
	 * @param h
	 */
	private void render(GLGraphics g, float w, float h) {
		final TablePerspective tablePerspective = selections.getTablePerspective();
		final VirtualArray recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
		final VirtualArray dimensionVA = tablePerspective.getDimensionPerspective().getVirtualArray();
		final ATableBasedDataDomain dataDomain = tablePerspective.getDataDomain();

		for (int i = 0; i < recordVA.size(); ++i) {
			Integer recordID = recordVA.get(i);
			if (isHidden(recordID)) {
				continue;
			}
			float y = recordSpacing.getPosition(i);
			float fieldHeight = recordSpacing.getSize(i);

			if (fieldHeight <= 0)
				continue;

			for (int j = 0; j < dimensionVA.size(); ++j) {
				Integer dimensionID = dimensionVA.get(j);
				float x = dimensionSpacing.getPosition(j);
				float fieldWidth = dimensionSpacing.getSize(j);
				if (fieldWidth <= 0)
					continue;
				boolean deSelected = isDeselected(recordID);
				Color color = blockColorer.apply(recordID, dimensionID, dataDomain, deSelected);
				g.color(color).fillRect(x, y, fieldWidth, fieldHeight);
			}
		}
	}

	@Override
	protected void onClicked(Pick pick) {
		Pair<IndexedId, IndexedId> ids = toDimensionRecordIds(pick);
		Integer dimensionID = ids.getFirst().id;
		Integer recordID = ids.getSecond().id;
		boolean repaint = false;
		boolean isCTRLDown = ((IMouseEvent) pick).isCtrlDown();
		if (dimensionID != null) {
			if (dimensionID != null)
				select(selections.getDimensionSelectionManager(), SelectionType.SELECTION, !isCTRLDown, false,
						dimensionID);
			hoveredDimensionID = ids.getFirst();
			repaint = true;
		}
		if (recordID != null) {
			if (recordID != null)
				select(selections.getRecordSelectionManager(), SelectionType.SELECTION, !isCTRLDown, false, recordID);
			hoveredRecordID = ids.getSecond();
			repaint = true;
		}
		if (repaint)
			repaint();
		if (!pick.isAnyDragging())
			pick.setDoDragging(true);
	}

	@Override
	protected void onMouseMoved(Pick pick) {
		Pair<IndexedId, IndexedId> ids = toDimensionRecordIds(pick);
		Integer dimensionID = ids.getFirst().id;
		Integer recordID = ids.getSecond().id;
		boolean repaint = false;
		if (!Objects.equal(ids.getFirst(), hoveredDimensionID)) {
			if (dimensionID != null)
				select(selections.getDimensionSelectionManager(), SelectionType.MOUSE_OVER, true, false, dimensionID);
			hoveredDimensionID = ids.getFirst();
			repaint = true;
		}
		if (!Objects.equal(ids.getSecond(), hoveredRecordID)) {
			if (recordID != null)
				select(selections.getRecordSelectionManager(), SelectionType.MOUSE_OVER, true, false, recordID);
			hoveredRecordID = ids.getSecond();
			repaint = true;
		}
		if (repaint)
			repaint();
	}

	@Override
	protected void onMouseReleased(Pick pick) {
		if (!pick.isDoDragging())
			return;
	}

	@Override
	protected void onDragged(Pick pick) {
		if (!pick.isDoDragging())
			return;
		Pair<IndexedId, IndexedId> ids = toDimensionRecordIds(pick);
		Integer dimensionID = ids.getFirst().id;
		Integer recordID = ids.getSecond().id;

		boolean repaint = false;
		if (dimensionID != null && !Objects.equal(ids.getFirst(), hoveredDimensionID)) {
			int[] range = toRange(hoveredDimensionID, ids.getFirst(), getTablePerspective().getDimensionPerspective());
			boolean selected = selections.getDimensionSelectionManager().checkStatus(SelectionType.SELECTION, dimensionID);
			select(selections.getDimensionSelectionManager(), SelectionType.SELECTION, false, selected, range);
			hoveredDimensionID = ids.getFirst();
			repaint = true;
		}
		if (recordID != null && !Objects.equal(ids.getSecond(), hoveredRecordID)) {
			int[] range = toRange(hoveredRecordID, ids.getSecond(), getTablePerspective().getRecordPerspective());
			boolean selected = selections.getRecordSelectionManager().checkStatus(SelectionType.SELECTION, recordID);
			select(selections.getRecordSelectionManager(), SelectionType.SELECTION, false, selected, range);
			hoveredRecordID = ids.getSecond();
			repaint = true;
		}
		if (repaint)
			repaint();
	}

	private static int[] toRange(IndexedId from, IndexedId to, Perspective per) {
		final VirtualArray va = per.getVirtualArray();
		final int length = Math.abs(from.index - to.index);
		int[] d = new int[length];
		int delta = from.index < to.index ? +1 : -1;
		int index = from.index;
		for (int i = 0; i < length; ++i) {
			d[i] = va.get(index);
			index += delta;
		}
		return d;
	}

	@Override
	protected void onMouseOut(Pick pick) {
		// clear all hovered elements
		select(selections.getDimensionSelectionManager(), SelectionType.MOUSE_OVER, true, false);
		select(selections.getRecordSelectionManager(), SelectionType.MOUSE_OVER, true, false);
		repaint();
		hoveredDimensionID = null;
		hoveredRecordID = null;
	}

	/**
	 * computes out of the given pick the correspondimg dimension and record picking ids
	 *
	 * @param pick
	 * @return
	 */
	private Pair<IndexedId, IndexedId> toDimensionRecordIds(Pick pick) {
		Vec2f point = toRelative(pick.getPickedPoint());
		float x = point.x();
		float y = point.y();
		switch (recordLabels) {
		case LEFT:
			x -= TEXT_WIDTH;
			break;
		default:
			break;
		}
		switch (dimensionLabels) {
		case LEFT:
			y -= TEXT_WIDTH;
			break;
		default:
			break;
		}

		int dindex = dimensionSpacing.getIndex(x);
		int rindex = recordSpacing.getIndex(y);

		Integer recordID = get(rindex, getTablePerspective().getRecordPerspective().getVirtualArray());
		Integer dimensionID = get(dindex, getTablePerspective().getDimensionPerspective().getVirtualArray());
		return Pair.make(new IndexedId(dindex, dimensionID), new IndexedId(rindex, recordID));
	}

	private static Integer get(int index, VirtualArray virtualArray) {
		if (index < 0 || index >= virtualArray.size())
			return null;
		return virtualArray.get(index);
	}

	private boolean isHidden(Integer recordID) {
		return isHideElements() && selections.getRecordSelectionManager().checkStatus(GLHeatMap.SELECTION_HIDDEN, recordID);
	}

	private boolean isDeselected(int recordID) {
		return selections.getRecordSelectionManager().checkStatus(SelectionType.DESELECTED, recordID);
	}

	private void select(SelectionManager manager, SelectionType selectionType, boolean clearExisting, boolean deSelect,
			int... ids) {
		if (clearExisting)
			manager.clearSelection(selectionType);

		if (ids.length > 0) {
			if (deSelect)
				manager.removeFromType(selectionType, Ints.asList(ids));
			else
				manager.addToType(selectionType, Ints.asList(ids));
		}

		selections.fireSelectionDelta(manager.getIDType());
	}

	/**
	 * Check whether we should hide elements
	 *
	 * @return
	 */
	public boolean isHideElements() {
		return hideElements;
	}

	/**
	 * @param hideElements
	 *            setter, see {@link hideElements}
	 */
	public void setHideElements(boolean hideElements) {
		if (this.hideElements == hideElements)
			return;
		this.hideElements = hideElements;
		relayout();
	}

	@ListenTo
	private void onColorMappingUpdate(UpdateColorMappingEvent event) {
		repaint();
		if (textureRenderer != null && context != null)
			textureRenderer.create(context);
	}

	@Override
	public String toString() {
		return "Heat map for " + selections;
	}

	private static final class IndexedId {
		private final int index;
		private final Integer id;

		public IndexedId(int index, Integer id) {
			this.index = index;
			this.id = id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + index;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IndexedId other = (IndexedId) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (index != other.index)
				return false;
			return true;
		}
	}
}
