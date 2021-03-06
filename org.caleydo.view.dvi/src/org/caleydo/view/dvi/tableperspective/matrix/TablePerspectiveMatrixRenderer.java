/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.tableperspective.matrix;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.PickingType;
import org.caleydo.view.dvi.contextmenu.RenameLabelHolderItem;
import org.caleydo.view.dvi.node.IDVINode;
import org.caleydo.view.dvi.tableperspective.AMultiTablePerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.PerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveCreator;
import org.caleydo.view.dvi.tableperspective.TablePerspectivePickingListener;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveRenderer;

public class TablePerspectiveMatrixRenderer extends AMultiTablePerspectiveRenderer {

	protected ATableBasedDataDomain dataDomain;
	// private List<ADimensionGroupData> dimensionGroupDatas;
	protected Map<TablePerspectiveRenderer, Pair<CellContainer, CellContainer>> tablePerspectiveRenderers = new HashMap<TablePerspectiveRenderer, Pair<CellContainer, CellContainer>>();
	// protected Map<EmptyCellRenderer, Pair<CellContainer, CellContainer>> emptyCellRenderers = new
	// HashMap<EmptyCellRenderer, Pair<CellContainer, CellContainer>>();
	/**
	 * Map containing all cells of the table identified by the concatenation of the row.caption and column.caption
	 */
	protected Map<String, TablePerspectiveRenderer> cells = new HashMap<String, TablePerspectiveRenderer>();
	protected List<CellContainer> rows = new ArrayList<CellContainer>();
	protected List<CellContainer> columns = new ArrayList<CellContainer>();
	protected Map<String, CellContainer> rowMap = new HashMap<String, CellContainer>();
	protected Map<String, CellContainer> columnMap = new HashMap<String, CellContainer>();
	protected Map<String, PerspectiveRenderer> perspectiveRenderers = new HashMap<String, PerspectiveRenderer>();

	protected ATablePerspectiveMatrixRenderingStrategy renderingStrategy;

	public TablePerspectiveMatrixRenderer(ATableBasedDataDomain dataDomain, GLDataViewIntegrator view, IDVINode node,
			DragAndDropController dragAndDropController) {
		super(node, view, dragAndDropController);

		this.dataDomain = dataDomain;
		renderingStrategy = (isUpsideDown) ? new BottomUpTablePerspectiveMatrixRenderingStrategy(this)
				: new TopDownTablePerspectiveMatrixRenderingStrategy(this);
		// DataDomainManager.get().getDataDomainByType(dataDomainType);

		createRowsAndColumns(node.getTablePerspectives());
		registerPickingListeners();
	}

	@Override
	public void createPickingListeners() {

		if (arePickingListenersRegistered)
			return;

		view.addTypePickingListener(new TablePerspectivePickingListener(view, dragAndDropController, this),
				PickingType.DATA_CONTAINER.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				CellContainer container = getCellContainerWithHashID(pick.getObjectID());

				if (container == null)
					return;

				container.isCollapsed = !container.isCollapsed;

				for (CellContainer child : container.childContainers) {

					boolean isAlwaysVisible = false;
					for (TablePerspectiveRenderer tablePerspectiveRenderer : tablePerspectiveRenderers.keySet()) {
						Pair<CellContainer, CellContainer> rowAndColumn = tablePerspectiveRenderers
								.get(tablePerspectiveRenderer);

						if (tablePerspectiveRenderer.isActive()
								&& (child == rowAndColumn.getFirst() || child == rowAndColumn.getSecond())) {
							isAlwaysVisible = true;
							break;
						}
					}

					child.isVisible = isAlwaysVisible || !container.isCollapsed;
				}

				// graphLayout.updateNodePositions();
				node.recalculateNodeSize();
				view.getGraphLayout().fitNodesToDrawingArea(view.calculateGraphDrawingArea());
				view.setDisplayListDirty();
			}

		}, PickingType.COLLAPSE_BUTTON.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				PerspectiveRenderer perspectiveRenderer = getPerspectiveRenderer(pick.getObjectID());
				if (perspectiveRenderer == null)
					return;

				Vec2f point = pick.getPickedPoint();
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingProperties(new Vec2f(point.x(), point.y()), "PerspectiveDrag");

				dragAndDropController.addDraggable(perspectiveRenderer);
				view.setDisplayListDirty();
			}

			private PerspectiveRenderer getPerspectiveRenderer(int id) {
				CellContainer container = getCellContainerWithHashID(id);

				if (container == null)
					return null;

				return perspectiveRenderers.get(container.id);

			}

			@Override
			public void rightClicked(Pick pick) {
				PerspectiveRenderer perspectiveRenderer = getPerspectiveRenderer(pick.getObjectID());
				if (perspectiveRenderer == null)
					return;

				Perspective perspective = null;
				if (perspectiveRenderer.isRecordPerspective()) {
					perspective = dataDomain.getTable().getRecordPerspective(perspectiveRenderer.getPerspectiveID());
				} else {
					perspective = dataDomain.getTable().getDimensionPerspective(perspectiveRenderer.getPerspectiveID());
				}

				// view.getContextMenuCreator().addContextMenuItem(
				// new RenameVariablePerspectiveItem(perspectiveRenderer
				// .getPerspectiveID(), dataDomain, perspectiveRenderer
				// .isRecordPerspective()));
				view.getContextMenuCreator().addContextMenuItem(new RenameLabelHolderItem(perspective));

			}

		}, PickingType.PERSPECTIVE.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {
				PerspectiveRenderer perspectiveRenderer = getPerspectiveRenderer(pick.getObjectID());
				if (perspectiveRenderer == null)
					return;
				float[] color = renderingStrategy.getPerspectiveColor();

				perspectiveRenderer.setColor(new float[] { color[0] - 0.2f, color[1] - 0.2f, color[2] - 0.2f, 1f });
				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				PerspectiveRenderer perspectiveRenderer = getPerspectiveRenderer(pick.getObjectID());
				if (perspectiveRenderer == null)
					return;

				perspectiveRenderer.setColor(renderingStrategy.getPerspectiveColor());
				view.setDisplayListDirty();
			}

			private PerspectiveRenderer getPerspectiveRenderer(int id) {
				CellContainer container = getCellContainerWithHashID(id);

				if (container == null)
					return null;

				return perspectiveRenderers.get(container.id);

			}

		}, PickingType.PERSPECTIVE_PENETRATING.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {
			@Override
			public void rightClicked(Pick pick) {

				CellContainer cellContainer = null;

				for (CellContainer c : rows) {
					if (c.id.hashCode() == pick.getObjectID()) {
						cellContainer = c;
						break;
					}
				}

				if (cellContainer == null) {
					for (CellContainer c : columns) {
						if (c.id.hashCode() == pick.getObjectID()) {
							cellContainer = c;
							break;
						}
					}
				}
				if (cellContainer == null || !(cellContainer.labelProvider instanceof Group))
					return;

				// view.getContextMenuCreator().addContextMenuItem(
				// new RenameVariablePerspectiveItem(perspectiveRenderer
				// .getPerspectiveID(), dataDomain, perspectiveRenderer
				// .isRecordPerspective()));
				view.getContextMenuCreator().addContextMenuItem(
						new RenameLabelHolderItem((Group) cellContainer.labelProvider));

			}
		}, PickingType.GROUP.name() + node.getID());

	}

	private void createRowsAndColumns(List<TablePerspective> tablePerspectives) {

		Set<String> rowIDs = dataDomain.getRecordPerspectiveIDs();
		Set<String> columnIDs = dataDomain.getDimensionPerspectiveIDs();

		// FIXME: Rows and columns do not change atm, but this might happen in
		// the future.

		rows.clear();
		columns.clear();
		perspectiveRenderers.clear();
		Map<String, CellContainer> newRowMap = new HashMap<String, CellContainer>();
		Map<String, CellContainer> newColumnMap = new HashMap<String, CellContainer>();
		float[] perspectiveColor = renderingStrategy.getPerspectiveColor();

		List<CellContainer> parentContainers = new ArrayList<CellContainer>();
		Map<CellContainer, List<CellContainer>> childContainerLists = new HashMap<CellContainer, List<CellContainer>>();

		for (String id : rowIDs) {

			Perspective perspective = dataDomain.getTable().getRecordPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}
			CellContainer row = rowMap.get(id);
			if (row == null) {
				row = new CellContainer();
				row.id = id;
				row.labelProvider = perspective;
				row.numSubdivisions = 1;
				row.isVisible = true;
				row.isCollapsed = true;

			}
			row.childContainers.clear();

			PerspectiveRenderer perspectiveRenderer = new PerspectiveRenderer(perspectiveColor, perspectiveColor, 0,
					view, dataDomain, id, true);
			perspectiveRenderers.put(id, perspectiveRenderer);
			newRowMap.put(id, row);
			// rows.add(row);
			parentContainers.add(row);

			GroupList groupList = perspective.getVirtualArray().getGroupList();

			if (groupList != null && groupList.size() > 1) {
				List<CellContainer> childList = new ArrayList<CellContainer>(groupList.size());
				for (int i = 0; i < groupList.size(); i++) {

					Group group = groupList.get(i);
					String subRowID = group.getPerspectiveID() != null ? group.getPerspectiveID() : row.id + i;
					CellContainer subRow = rowMap.get(subRowID);
					if (subRow == null) {
						subRow = new CellContainer();
						subRow.labelProvider = group;
						subRow.id = subRowID;
						subRow.numSubdivisions = 1;
						subRow.isVisible = false;

						subRow.parentContainer = row;

					}
					row.childContainers.add(subRow);
					newRowMap.put(subRowID, subRow);

					childList.add(subRow);
					// columns.add(subColumn);

				}

				// Collections.sort(childList);
				childContainerLists.put(row, childList);
			}
		}

		Collections.sort(parentContainers);

		for (CellContainer row : parentContainers) {
			rows.add(row);
			List<CellContainer> childRows = childContainerLists.get(row);
			if (childRows != null) {
				rows.addAll(childRows);
			}
		}

		parentContainers.clear();
		childContainerLists.clear();

		for (String id : columnIDs) {

			Perspective perspective = dataDomain.getTable().getDimensionPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}

			CellContainer column = columnMap.get(id);

			if (column == null) {
				column = new CellContainer();
				column.id = id;
				column.labelProvider = perspective;
				column.numSubdivisions = 1;
				column.isVisible = true;
				column.isCollapsed = true;
			}

			PerspectiveRenderer perspectiveRenderer = new PerspectiveRenderer(perspectiveColor, perspectiveColor, 0,
					view, dataDomain, id, false);
			perspectiveRenderers.put(id, perspectiveRenderer);

			column.childContainers.clear();
			newColumnMap.put(id, column);
			parentContainers.add(column);
			// columns.add(column);

			GroupList groupList = perspective.getVirtualArray().getGroupList();

			if (groupList != null && groupList.size() > 1) {
				List<CellContainer> childList = new ArrayList<CellContainer>(groupList.size());
				for (int i = 0; i < groupList.size(); i++) {

					Group group = groupList.get(i);
					String subColumnID = group.getPerspectiveID() != null ? group.getPerspectiveID() : column.id + i;

					CellContainer subColumn = columnMap.get(subColumnID);

					if (subColumn == null) {
						subColumn = new CellContainer();
						subColumn.labelProvider = group;
						subColumn.id = subColumnID;
						subColumn.numSubdivisions = 1;
						subColumn.isVisible = false;
						subColumn.parentContainer = column;
					}
					column.childContainers.add(subColumn);
					newColumnMap.put(subColumnID, subColumn);

					childList.add(subColumn);
					// columns.add(subColumn);

				}

				// Collections.sort(childList);
				childContainerLists.put(column, childList);
			}
		}

		Collections.sort(parentContainers);

		for (CellContainer column : parentContainers) {
			columns.add(column);
			List<CellContainer> childColumns = childContainerLists.get(column);
			if (childColumns != null) {
				columns.addAll(childColumns);
			}
		}
		rowMap = newRowMap;
		columnMap = newColumnMap;
		cells.clear();
		// emptyCellRenderers.clear();
		tablePerspectiveRenderers.clear();

		// faster than linear search
		Map<String, TablePerspective> lookup = new HashMap<>(tablePerspectives.size());
		for (TablePerspective p : tablePerspectives)
			lookup.put(p.getTablePerspectiveKey(), p);

		// int emptyCellId = 0;
		for (CellContainer column : columns) {
			int numSubdivisions = 1;

			String dimensionPerspectiveID = column.id;
			for (CellContainer row : rows) {
				String recordPerspectiveID = row.id;
				String key = TablePerspective.createKey(recordPerspectiveID, dimensionPerspectiveID);
				TablePerspective tablePerspective = lookup.get(key);
				boolean tablePerspectiveExists = tablePerspective != null && !tablePerspective.isPrivate();
				if (tablePerspectiveExists) {
					TablePerspectiveRenderer tablePerspectiveRenderer = new TablePerspectiveRenderer(tablePerspective,
							view, node);
					tablePerspectiveRenderer.setShowText(false);
					if (view.isTablePerspectiveShownByView(tablePerspective)) {
						numSubdivisions++;
						if (numSubdivisions >= rows.size()) {
							numSubdivisions = rows.size();
						}
						tablePerspectiveRenderer.setActive(true);
						row.isVisible = true;
						column.isVisible = true;
					} else {
						if (row.parentContainer != null && row.parentContainer.isCollapsed) {
							row.isVisible = false;
						}
						if (column.parentContainer != null && column.parentContainer.isCollapsed) {
							column.isVisible = false;
						}
					}
					cells.put(row.id + column.id, tablePerspectiveRenderer);
					tablePerspectiveRenderers.put(tablePerspectiveRenderer, new Pair<CellContainer, CellContainer>(row,
							column));
				} else {
					TablePerspectiveCreator.Builder builder = new TablePerspectiveCreator.Builder(dataDomain);

					Perspective recordPerspective = dataDomain.getTable().getRecordPerspective(row.id);
					if (recordPerspective == null) {

						Perspective parentPerspective = dataDomain.getTable().getRecordPerspective(
								row.parentContainer.id);
						// This works because the child containers do net get
						// sorted in a cellcontainer
						int groupIndex = row.parentContainer.childContainers.indexOf(row);

						VirtualArray recordVA = parentPerspective.getVirtualArray();

						GroupList groupList = recordVA.getGroupList();
						Group recordGroup = groupList.get(groupIndex);
						builder.recordVA(recordVA).recordGroup(recordGroup);

					} else {
						builder.recordPerspective(recordPerspective);
					}

					Perspective dimensionPerspective = dataDomain.getTable().getDimensionPerspective(column.id);
					if (dimensionPerspective == null) {

						Perspective parentPerspective = dataDomain.getTable().getDimensionPerspective(
								column.parentContainer.id);

						// This works because the child containers do net get
						// sorted in a cellcontainer
						int groupIndex = column.parentContainer.childContainers.indexOf(column);

						VirtualArray dimensionVA = parentPerspective.getVirtualArray();
						GroupList groupList = dimensionVA.getGroupList();
						Group dimensionGroup = groupList.get(groupIndex);

						builder.dimensionVA(dimensionVA).dimensionGroup(dimensionGroup);
					} else {
						builder.dimensionPerspective(dimensionPerspective);
					}

					TablePerspectiveRenderer tablePerspectiveRenderer = new TablePerspectiveRenderer(builder.build(),
							view, node);
					tablePerspectiveRenderer.setShowText(false);
					cells.put(row.id + column.id, tablePerspectiveRenderer);
					tablePerspectiveRenderers.put(tablePerspectiveRenderer, new Pair<CellContainer, CellContainer>(row,
							column));
				}
			}
			column.numSubdivisions = numSubdivisions;
		}
	}

	@Override
	public void renderContent(GL2 gl) {

		String columnsCaption = dataDomain.getDimensionDenomination(true, true);
		String rowsCaption = dataDomain.getRecordDenomination(true, true);

		renderingStrategy.render(gl, bottomObjectPositions, topObjectPositions, x, y, node, view, pickingIDsToBePushed,
				rowsCaption, columnsCaption);

	}

	@Override
	public int getMinWidthPixels() {

		return renderingStrategy.getMinWidthPixels(rows, columns, view);

	}

	@Override
	public int getMinHeightPixels() {

		return renderingStrategy.getMinHeightPixels(rows, columns, view);

	}

	@Override
	public void setTablePerspectives(List<TablePerspective> tablePerspectives) {
		createRowsAndColumns(tablePerspectives);
	}

	@Override
	public void setUpsideDown(boolean isUpsideDown) {
		this.isUpsideDown = isUpsideDown;
		renderingStrategy = (isUpsideDown) ? new BottomUpTablePerspectiveMatrixRenderingStrategy(this)
				: new TopDownTablePerspectiveMatrixRenderingStrategy(this);
	}

	@Override
	public void removePickingListeners() {
		view.removeAllTypePickingListeners(PickingType.EMPTY_CELL.name() + node.getID());
		view.removeAllTypePickingListeners(PickingType.DATA_CONTAINER.name() + node.getID());
		view.removeAllTypePickingListeners(PickingType.COLLAPSE_BUTTON.name() + node.getID());
		view.removeAllTypePickingListeners(PickingType.PERSPECTIVE.name() + node.getID());
		view.removeAllTypePickingListeners(PickingType.PERSPECTIVE_PENETRATING.name() + node.getID());
		view.removeAllTypePickingListeners(PickingType.GROUP.name() + node.getID());

	}

	@Override
	protected Collection<TablePerspectiveRenderer> getDimensionGroupRenderers() {
		return tablePerspectiveRenderers.keySet();
	}

	protected CellContainer getCellContainerWithHashID(int id) {
		CellContainer container = null;

		for (CellContainer cellContainer : columns) {
			if (cellContainer.id.hashCode() == id) {
				container = cellContainer;
				break;
			}
		}

		if (container == null) {
			for (CellContainer cellContainer : rows) {
				if (cellContainer.id.hashCode() == id) {
					container = cellContainer;
					break;
				}
			}
		}

		return container;
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
