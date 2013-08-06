/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.heatmap.v2;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.mapping.UpdateColorMappingEvent;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.item.BookmarkMenuItem;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.contextmenu.container.GeneMenuItemContainer;
import org.caleydo.view.heatmap.heatmap.GLHeatMap;
import org.caleydo.view.heatmap.v2.ISpacingStrategy.ISpacingLayout;
import org.caleydo.view.heatmap.v2.internal.HeatMapTextureRenderer;
import org.caleydo.view.heatmap.v2.internal.SelectionRenderer;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
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

	private Integer hoveredRecordID = null;
	private Integer hoveredDimensionID = null;

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
		this.dimensionSelectionRenderer = new SelectionRenderer(tablePerspective, selections.getDimensionSelectionManager(),
				true);
		this.recordSelectionRenderer = new SelectionRenderer(tablePerspective, selections.getRecordSelectionManager(),
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
		Pair<Integer, Integer> ids = toDimensionRecordIds(pick);
		Integer dimensionID = ids.getFirst();
		Integer recordID = ids.getSecond();
		boolean repaint = false;
		if (dimensionID != null) {
			if (dimensionID != null)
				createSelection(selections.getDimensionSelectionManager(), SelectionType.SELECTION, dimensionID);
			hoveredDimensionID = dimensionID;
			repaint = true;
		}
		if (recordID != null) {
			if (recordID != null)
				createSelection(selections.getRecordSelectionManager(), SelectionType.SELECTION, recordID);
			hoveredRecordID = recordID;
			repaint = true;
		}
		if (repaint)
			repaint();
	}

	@Override
	protected void onMouseMoved(Pick pick) {
		Pair<Integer, Integer> ids = toDimensionRecordIds(pick);
		Integer dimensionID = ids.getFirst();
		Integer recordID = ids.getSecond();

		boolean repaint = false;
		if (dimensionID != hoveredDimensionID) {
			if (dimensionID != null)
				createSelection(selections.getDimensionSelectionManager(), SelectionType.MOUSE_OVER, dimensionID);
			hoveredDimensionID = dimensionID;
			repaint = true;
		}
		if (recordID != hoveredRecordID) {
			if (recordID != null)
				createSelection(selections.getRecordSelectionManager(), SelectionType.MOUSE_OVER, recordID);
			hoveredRecordID = recordID;
			repaint = true;
		}
		if (repaint)
			repaint();
	}

	@Override
	protected void onMouseOut(Pick pick) {
		// clear all hovered elements
		createSelection(selections.getDimensionSelectionManager(), SelectionType.MOUSE_OVER, -1);
		createSelection(selections.getRecordSelectionManager(), SelectionType.MOUSE_OVER, -1);
		repaint();
		hoveredDimensionID = null;
		hoveredRecordID = null;
	}

	@Override
	protected void onRightClicked(Pick pick) {
		Pair<Integer, Integer> ids = toDimensionRecordIds(pick);
		Integer dimensionID = ids.getFirst();
		Integer recordID = ids.getSecond();

		List<AContextMenuItem> toShow = new ArrayList<>();
		if (recordID != null) {
			createSelection(selections.getRecordSelectionManager(), SelectionType.SELECTION, recordID);

			ATableBasedDataDomain dataDomain = selections.getDataDomain();
			if (dataDomain instanceof GeneticDataDomain && dataDomain.isColumnDimension()) {
				GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
				contexMenuItemContainer.setDataDomain(dataDomain);
				contexMenuItemContainer.setData(dataDomain.getRecordIDType(), recordID);
				contexMenuItemContainer.addSeparator();
				toShow.addAll(contexMenuItemContainer.getContextMenuItems());
			} else {
				IDType recordIDType = dataDomain.getRecordIDType();
				AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
						+ recordIDType.getIDCategory().getHumanReadableIDType().getTypeName() + ": "
						+ dataDomain.getRecordLabel(recordIDType, recordID), recordIDType, recordID);
				toShow.add(menuItem);
			}
		}
		if (dimensionID != null) {
			createSelection(selections.getDimensionSelectionManager(), SelectionType.SELECTION, dimensionID);

			ATableBasedDataDomain dataDomain = selections.getDataDomain();
			if (dataDomain instanceof GeneticDataDomain && !dataDomain.isColumnDimension()) {
				GeneMenuItemContainer contexMenuItemContainer = new GeneMenuItemContainer();
				contexMenuItemContainer.setDataDomain(dataDomain);
				contexMenuItemContainer.setData(dataDomain.getDimensionIDType(), dimensionID);
				contexMenuItemContainer.addSeparator();
				toShow.addAll(contexMenuItemContainer.getContextMenuItems());
			} else {
				IDType dimensionIDType = dataDomain.getDimensionIDType();
				AContextMenuItem menuItem = new BookmarkMenuItem("Bookmark "
						+ dimensionIDType.getIDCategory().getHumanReadableIDType().getTypeName() + ": "
						+ dataDomain.getDimensionLabel(dimensionIDType, dimensionID), dimensionIDType, dimensionID);
				toShow.add(menuItem);
			}
		}
		if (!toShow.isEmpty())
			context.getSWTLayer().showContextMenu(toShow);
	}

	/**
	 * computes out of the given pick the correspondimg dimension and record picking ids
	 *
	 * @param pick
	 * @return
	 */
	private Pair<Integer, Integer> toDimensionRecordIds(Pick pick) {
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
		return Pair.make(dimensionID, recordID);
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

	private void createSelection(SelectionManager manager, SelectionType selectionType, int recordID) {
		if (manager.checkStatus(selectionType, recordID))
			return;

		// check if the mouse-overed element is already selected, and if it is,
		// whether mouse over is clear.
		// If that all is true we don't need to do anything
		if (selectionType == SelectionType.MOUSE_OVER && manager.checkStatus(SelectionType.SELECTION, recordID)
				&& manager.getElements(SelectionType.MOUSE_OVER).isEmpty())
			return;

		manager.clearSelection(selectionType);

		// TODO: Integrate multi spotting support again

		if (recordID >= 0)
			manager.addToType(selectionType, recordID);

		selections.fireSelectionDelta(manager.getIDType());
		relayout();
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
}
