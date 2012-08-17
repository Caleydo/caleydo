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
package org.caleydo.view.dvi.tableperspective.matrix;

import java.awt.Point;
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
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.PickingType;
import org.caleydo.view.dvi.contextmenu.CreateTablePerspectiveItem;
import org.caleydo.view.dvi.event.CreateTablePerspectiveEvent;
import org.caleydo.view.dvi.node.IDVINode;
import org.caleydo.view.dvi.tableperspective.AMultiTablePerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.PerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.TablePerspectivePickingListener;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveRenderer;

public class TablePerspectiveMatrixRenderer extends AMultiTablePerspectiveRenderer {

	protected ATableBasedDataDomain dataDomain;
	// private List<ADimensionGroupData> dimensionGroupDatas;
	protected Map<TablePerspectiveRenderer, Pair<CellContainer, CellContainer>> dimensionGroupRenderers = new HashMap<TablePerspectiveRenderer, Pair<CellContainer, CellContainer>>();
	protected Map<EmptyCellRenderer, Pair<CellContainer, CellContainer>> emptyCellRenderers = new HashMap<EmptyCellRenderer, Pair<CellContainer, CellContainer>>();
	/**
	 * Map containing all cells of the table identified by the concatenation of
	 * the row.caption and column.caption
	 */
	protected Map<String, ColorRenderer> cells = new HashMap<String, ColorRenderer>();
	protected List<CellContainer> rows = new ArrayList<CellContainer>();
	protected List<CellContainer> columns = new ArrayList<CellContainer>();
	protected Map<String, CellContainer> rowMap = new HashMap<String, CellContainer>();
	protected Map<String, CellContainer> columnMap = new HashMap<String, CellContainer>();
	protected Map<String, PerspectiveRenderer> perspectiveRenderers = new HashMap<String, PerspectiveRenderer>();

	protected ATablePerspectiveMatrixRenderingStrategy renderingStrategy;

	public TablePerspectiveMatrixRenderer(ATableBasedDataDomain dataDomain,
			GLDataViewIntegrator view, IDVINode node,
			DragAndDropController dragAndDropController) {
		super(node, view, dragAndDropController);

		this.dataDomain = dataDomain;
		renderingStrategy = (isUpsideDown) ? new BottomUpTablePerspectiveMatrixRenderingStrategy(
				this) : new TopDownTablePerspectiveMatrixRenderingStrategy(this);
		// DataDomainManager.get().getDataDomainByType(dataDomainType);

		createRowsAndColumns(node.getTablePerspectives());
		registerPickingListeners();
	}

	@Override
	public void createPickingListeners() {

		if (arePickingListenersRegistered)
			return;

		view.addTypePickingListener(new TablePerspectivePickingListener(view,
				dragAndDropController, this),
				PickingType.DATA_CONTAINER.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {
				EmptyCellRenderer emptyCellRenderer = getEmptyCellRenderer(pick
						.getObjectID());
				if (emptyCellRenderer == null)
					return;

				emptyCellRenderer.setColor(emptyCellRenderer.getBorderColor());
				view.setDisplayListDirty();
				// System.out.println("over");
			}

			@Override
			public void mouseOut(Pick pick) {
				EmptyCellRenderer emptyCellRenderer = getEmptyCellRenderer(pick
						.getObjectID());
				if (emptyCellRenderer == null)
					return;

				emptyCellRenderer.setColor(EmptyCellRenderer.DEFAULT_COLOR);
				view.setDisplayListDirty();
				// System.out.println("out");
			}

			@Override
			public void rightClicked(Pick pick) {
				triggerTablePerspectiveCreation(pick.getObjectID(), true);
			}

			@Override
			public void clicked(Pick pick) {
				triggerTablePerspectiveCreation(pick.getObjectID(), false);
			}

			private void triggerTablePerspectiveCreation(int id, boolean useContextMenu) {
				Pair<CellContainer, CellContainer> rowAndColumn = null;

				for (EmptyCellRenderer emptyCellRenderer : emptyCellRenderers.keySet()) {
					if (emptyCellRenderer.getID() == id) {
						rowAndColumn = emptyCellRenderers.get(emptyCellRenderer);
						break;
					}
				}

				if (rowAndColumn != null) {

					CellContainer row = rowAndColumn.getFirst();
					String recordPerspectiveID = row.id;
					Group rowGroup = null;
					RecordVirtualArray recordVA = null;
					boolean createRecordPerspective = false;

					if (!dataDomain.getTable().containsRecordPerspective(
							recordPerspectiveID)) {
						// FIXME: Check additionally if the group has a
						// dimensionperspective

						RecordPerspective perspective = dataDomain.getTable()
								.getRecordPerspective(row.parentContainer.id);

						// This works because the child containers do net get
						// sorted in a cellcontainer
						int groupIndex = row.parentContainer.childContainers.indexOf(row);

						recordVA = perspective.getVirtualArray();

						RecordGroupList groupList = recordVA.getGroupList();
						rowGroup = groupList.get(groupIndex);

						createRecordPerspective = true;

					}

					CellContainer column = rowAndColumn.getSecond();
					String dimensionPerspectiveID = column.id;
					Group columnGroup = null;
					DimensionVirtualArray dimensionVA = null;
					boolean createDimensionPerspective = false;

					if (!dataDomain.getTable().containsDimensionPerspective(
							dimensionPerspectiveID)) {
						// FIXME: Check additionally if the group has a
						// dimensionperspective

						DimensionPerspective perspective = dataDomain.getTable()
								.getDimensionPerspective(column.parentContainer.id);

						// This works because the child containers do net get
						// sorted in a cellcontainer
						int groupIndex = column.parentContainer.childContainers
								.indexOf(column);

						dimensionVA = perspective.getVirtualArray();

						DimensionGroupList groupList = dimensionVA.getGroupList();
						columnGroup = groupList.get(groupIndex);

						createDimensionPerspective = true;

					}

					CreateTablePerspectiveEvent event = new CreateTablePerspectiveEvent(
							dataDomain, recordPerspectiveID, createRecordPerspective,
							recordVA, rowGroup, dimensionPerspectiveID,
							createDimensionPerspective, dimensionVA, columnGroup);
					if (useContextMenu) {
						view.getContextMenuCreator().addContextMenuItem(
								new CreateTablePerspectiveItem(event));
					} else {
						event.setSender(this);
						GeneralManager.get().getEventPublisher().triggerEvent(event);
					}
				}
			}

			private EmptyCellRenderer getEmptyCellRenderer(int id) {
				for (EmptyCellRenderer emptyCellRenderer : emptyCellRenderers.keySet()) {
					if (emptyCellRenderer.getID() == id) {
						return emptyCellRenderer;
					}
				}
				return null;
			}

		}, PickingType.EMPTY_CELL.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				CellContainer container = getCellContainerWithHashID(pick.getObjectID());

				if (container == null)
					return;

				container.isCollapsed = !container.isCollapsed;

				for (CellContainer child : container.childContainers) {

					boolean isAlwaysVisible = false;
					for (TablePerspectiveRenderer dimensionGroupRenderer : dimensionGroupRenderers
							.keySet()) {
						Pair<CellContainer, CellContainer> rowAndColumn = dimensionGroupRenderers
								.get(dimensionGroupRenderer);

						if (child == rowAndColumn.getFirst()
								|| child == rowAndColumn.getSecond()) {
							isAlwaysVisible = true;
							break;
						}
					}

					child.isVisible = isAlwaysVisible || !container.isCollapsed;
				}

				// graphLayout.updateNodePositions();
				node.recalculateNodeSize();
				view.getGraphLayout().fitNodesToDrawingArea(
						view.calculateGraphDrawingArea());
				view.setDisplayListDirty();
			}

		}, PickingType.COLLAPSE_BUTTON.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {
			@Override
			public void clicked(Pick pick) {
				PerspectiveRenderer perspectiveRenderer = getPerspectiveRenderer(pick
						.getObjectID());
				if (perspectiveRenderer == null)
					return;
				//
				// draggedComparisonGroupRenderer
				// .setSelectionType(SelectionType.SELECTION);
				Point point = pick.getPickedPoint();
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingProperties(new Point(point.x, point.y),
						"PerspectiveDrag");
				// dragAndDropController.setDraggingStartPosition(new
				// Point(point.x, point.y));
				dragAndDropController.addDraggable(perspectiveRenderer);
				// dragAndDropController.setDraggingMode("PerspectiveDrag");
				view.setDisplayListDirty();
			}

			// @Override
			// public void dragged(Pick pick) {
			//
			// String draggingMode = dragAndDropController.getDraggingMode();
			//
			// if (!dragAndDropController.isDragging()
			// && dragAndDropController.hasDraggables() && draggingMode != null
			// && draggingMode.equals("PerspectiveDrag")) {
			// dragAndDropController.startDragging();
			// }
			// }

			private PerspectiveRenderer getPerspectiveRenderer(int id) {
				CellContainer container = getCellContainerWithHashID(id);

				if (container == null)
					return null;

				return perspectiveRenderers.get(container.id);

			}

		}, PickingType.PERSPECTIVE.name() + node.getID());

		view.addTypePickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {
				PerspectiveRenderer perspectiveRenderer = getPerspectiveRenderer(pick
						.getObjectID());
				if (perspectiveRenderer == null)
					return;
				float[] color = renderingStrategy.getPerspectiveColor();

				perspectiveRenderer.setColor(new float[] { color[0] - 0.2f,
						color[1] - 0.2f, color[2] - 0.2f, 1f });
				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				PerspectiveRenderer perspectiveRenderer = getPerspectiveRenderer(pick
						.getObjectID());
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

			RecordPerspective perspective = dataDomain.getTable()
					.getRecordPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}
			CellContainer row = rowMap.get(id);
			if (row == null) {
				row = new CellContainer();
				row.id = id;
				row.caption = perspective.getLabel();
				row.numSubdivisions = 1;
				row.isVisible = true;
				row.isCollapsed = true;

			}
			row.childContainers.clear();

			PerspectiveRenderer perspectiveRenderer = new PerspectiveRenderer(
					perspectiveColor, perspectiveColor, 0, view, dataDomain, id, true);
			perspectiveRenderers.put(id, perspectiveRenderer);
			newRowMap.put(id, row);
			// rows.add(row);
			parentContainers.add(row);

			RecordGroupList groupList = perspective.getVirtualArray().getGroupList();

			if (groupList != null && groupList.size() > 1) {
				List<CellContainer> childList = new ArrayList<CellContainer>(
						groupList.size());
				for (int i = 0; i < groupList.size(); i++) {

					Group group = groupList.get(i);
					String subRowID = group.getPerspectiveID() != null ? group
							.getPerspectiveID() : row.id + i;
					CellContainer subRow = rowMap.get(subRowID);
					if (subRow == null) {
						subRow = new CellContainer();
						subRow.caption = group.getLabel();
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

			DimensionPerspective perspective = dataDomain.getTable()
					.getDimensionPerspective(id);
			if (perspective.isPrivate()) {
				continue;
			}

			CellContainer column = columnMap.get(id);

			if (column == null) {
				column = new CellContainer();
				column.id = id;
				column.caption = perspective.getLabel();
				column.numSubdivisions = 1;
				column.isVisible = true;
				column.isCollapsed = true;
			}

			PerspectiveRenderer perspectiveRenderer = new PerspectiveRenderer(
					perspectiveColor, perspectiveColor, 0, view, dataDomain, id, false);
			perspectiveRenderers.put(id, perspectiveRenderer);

			column.childContainers.clear();
			newColumnMap.put(id, column);
			parentContainers.add(column);
			// columns.add(column);

			DimensionGroupList groupList = perspective.getVirtualArray().getGroupList();

			if (groupList != null && groupList.size() > 1) {
				List<CellContainer> childList = new ArrayList<CellContainer>(
						groupList.size());
				for (int i = 0; i < groupList.size(); i++) {

					Group group = groupList.get(i);
					String subColumnID = group.getPerspectiveID() != null ? group
							.getPerspectiveID() : column.id + i;

					CellContainer subColumn = columnMap.get(subColumnID);

					if (subColumn == null) {
						subColumn = new CellContainer();
						subColumn.caption = group.getLabel();
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

		// Collections.sort(parentContainers);

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
		emptyCellRenderers.clear();
		dimensionGroupRenderers.clear();

		int emptyCellId = 0;
		for (CellContainer column : columns) {
			int numSubdivisions = 1;
			for (CellContainer row : rows) {
				boolean dimensionGroupExists = false;
				for (TablePerspective tablePerspective : tablePerspectives) {

					if (tablePerspective.isPrivate())
						continue;

					String recordPerspectiveID = row.id;
					String dimensionPerspectiveID = column.id;

					if (tablePerspective.getDimensionPerspective().getPerspectiveID()
							.equals(dimensionPerspectiveID)
							&& tablePerspective.getRecordPerspective().getPerspectiveID()
									.equals(recordPerspectiveID)) {
						numSubdivisions++;
						if (numSubdivisions >= rows.size()) {
							numSubdivisions = rows.size();
						}
						dimensionGroupExists = true;
						TablePerspectiveRenderer dimensionGroupRenderer = new TablePerspectiveRenderer(
								tablePerspective, view, node, dataDomain.getColor()
										.getRGBA());
						dimensionGroupRenderer.setShowText(false);
						cells.put(row.id + column.id, dimensionGroupRenderer);
						dimensionGroupRenderers.put(dimensionGroupRenderer,
								new Pair<CellContainer, CellContainer>(row, column));
						row.isVisible = true;
						column.isVisible = true;
						break;
					}
				}
				if (!dimensionGroupExists) {
					EmptyCellRenderer emptyCellRenderer = new EmptyCellRenderer(
							emptyCellId++);
					cells.put(row.id + column.id, emptyCellRenderer);
					emptyCellRenderers.put(emptyCellRenderer,
							new Pair<CellContainer, CellContainer>(row, column));
				}
			}
			column.numSubdivisions = numSubdivisions;
		}
	}

	@Override
	public void renderContent(GL2 gl) {

		String columnsCaption = dataDomain.getDimensionDenomination(true, true);
		String rowsCaption = dataDomain.getRecordDenomination(true, true);

		renderingStrategy.render(gl, bottomDimensionGroupPositions,
				topDimensionGroupPositions, x, y, node, view, pickingIDsToBePushed,
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
		renderingStrategy = (isUpsideDown) ? new BottomUpTablePerspectiveMatrixRenderingStrategy(
				this) : new TopDownTablePerspectiveMatrixRenderingStrategy(this);
	}

	@Override
	public void removePickingListeners() {
		view.removeAllTypePickingListeners(PickingType.EMPTY_CELL.name() + node.getID());
		view.removeAllTypePickingListeners(PickingType.DATA_CONTAINER.name()
				+ node.getID());
		view.removeAllTypePickingListeners(PickingType.COLLAPSE_BUTTON.name()
				+ node.getID());
		view.removeAllTypePickingListeners(PickingType.PERSPECTIVE.name() + node.getID());
		view.removeAllTypePickingListeners(PickingType.PERSPECTIVE_PENETRATING.name()
				+ node.getID());

	}

	@Override
	protected Collection<TablePerspectiveRenderer> getDimensionGroupRenderers() {
		return dimensionGroupRenderers.keySet();
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
	protected boolean permitsDisplayLists() {
		return false;
	}
}
