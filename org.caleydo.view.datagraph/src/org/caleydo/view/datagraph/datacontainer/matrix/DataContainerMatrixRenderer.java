package org.caleydo.view.datagraph.datacontainer.matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
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
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.contextmenu.AddDataContainerItem;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DimensionGroupRenderer;
import org.caleydo.view.datagraph.event.AddDataContainerEvent;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class DataContainerMatrixRenderer extends ADataContainerRenderer {

	protected final static String EMPTY_CELL_PICKING_TYPE = "org.caleydo.view.datagraph.emptycell";
	protected final static String COLLAPSE_BUTTON_PICKING_TYPE = "org.caleydo.view.datagraph.collapsebutton";

	private ATableBasedDataDomain dataDomain;
	// private List<ADimensionGroupData> dimensionGroupDatas;
	private Map<DimensionGroupRenderer, Pair<CellContainer, CellContainer>> dimensionGroupRenderers = new HashMap<DimensionGroupRenderer, Pair<CellContainer, CellContainer>>();
	private Map<EmptyCellRenderer, Pair<CellContainer, CellContainer>> emptyCellRenderers = new HashMap<EmptyCellRenderer, Pair<CellContainer, CellContainer>>();
	/**
	 * Map containing all cells of the table identified by the concatenation of
	 * the row.caption and column.caption
	 */
	private Map<String, ColorRenderer> cells = new HashMap<String, ColorRenderer>();
	private List<CellContainer> rows = new ArrayList<CellContainer>();
	private List<CellContainer> columns = new ArrayList<CellContainer>();
	private Map<String, CellContainer> rowMap = new HashMap<String, CellContainer>();
	private Map<String, CellContainer> columnMap = new HashMap<String, CellContainer>();

	private ADataContainerMatrixRenderingStrategy renderingStrategy;

	private boolean pickingListenersRegistered = false;

	public DataContainerMatrixRenderer(ATableBasedDataDomain dataDomain,
			GLDataGraph view, IDataGraphNode node,
			DragAndDropController dragAndDropController) {
		super(node, view, dragAndDropController);

		this.dataDomain = dataDomain;
		renderingStrategy = (isUpsideDown) ? new BottomUpDataContainerMatrixRenderingStrategy()
				: new TopDownDataContainerMatrixRenderingStrategy();
		// DataDomainManager.get().getDataDomainByType(dataDomainType);

		createRowsAndColumns(node.getDataContainers());
		registerPickingListeners();

	}

	@Override
	public void createPickingListeners() {

		if (pickingListenersRegistered)
			return;

		view.addMultiIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(pick
						.getID());
				if (dimensionGroupRenderer == null)
					return;

				dimensionGroupRenderer
						.setSelectionType(SelectionType.SELECTION);

				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick
						.getPickedPoint());
				dragAndDropController.addDraggable(dimensionGroupRenderer);
				view.setDisplayListDirty();

			}

			@Override
			public void mouseOver(Pick pick) {
				DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(pick
						.getID());
				if (dimensionGroupRenderer == null)
					return;

				dimensionGroupRenderer.setColor(dimensionGroupRenderer
						.getBorderColor());
				view.setDisplayListDirty();
			}

			@Override
			public void mouseOut(Pick pick) {
				DimensionGroupRenderer dimensionGroupRenderer = getDimensionGroupRenderer(pick
						.getID());
				if (dimensionGroupRenderer == null)
					return;

				dimensionGroupRenderer
						.setColor(dataDomain.getColor().getRGBA());
				view.setDisplayListDirty();
			}

			@Override
			public void dragged(Pick pick) {
				if (!dragAndDropController.isDragging()) {
					dragAndDropController.startDragging("DimensionGroupDrag");
				}
			}

			private DimensionGroupRenderer getDimensionGroupRenderer(int id) {
				for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers
						.keySet()) {
					if (dimensionGroupRenderer.getDataContainer().getID() == id) {
						return dimensionGroupRenderer;
					}
				}
				return null;
			}

		}, DIMENSION_GROUP_PICKING_TYPE + node.getID());

		view.addMultiIDPickingListener(new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {
				EmptyCellRenderer emptyCellRenderer = getEmptyCellRenderer(pick
						.getID());
				if (emptyCellRenderer == null)
					return;

				emptyCellRenderer.setColor(emptyCellRenderer.getBorderColor());
				view.setDisplayListDirty();
				// System.out.println("over");
			}

			@Override
			public void mouseOut(Pick pick) {
				EmptyCellRenderer emptyCellRenderer = getEmptyCellRenderer(pick
						.getID());
				if (emptyCellRenderer == null)
					return;

				emptyCellRenderer.setColor(EmptyCellRenderer.DEFAULT_COLOR);
				view.setDisplayListDirty();
				// System.out.println("out");
			}

			@Override
			public void rightClicked(Pick pick) {
				triggerDataContainerCreation(pick.getID(), true);
			}

			@Override
			public void clicked(Pick pick) {
				triggerDataContainerCreation(pick.getID(), false);
			}

			private void triggerDataContainerCreation(int id,
					boolean useContextMenu) {
				Pair<CellContainer, CellContainer> rowAndColumn = null;

				for (EmptyCellRenderer emptyCellRenderer : emptyCellRenderers
						.keySet()) {
					if (emptyCellRenderer.getID() == id) {
						rowAndColumn = emptyCellRenderers
								.get(emptyCellRenderer);
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
						int groupIndex = row.parentContainer.childContainers
								.indexOf(row);

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

						DimensionPerspective perspective = dataDomain
								.getTable().getDimensionPerspective(
										column.parentContainer.id);

						// This works because the child containers do net get
						// sorted in a cellcontainer
						int groupIndex = column.parentContainer.childContainers
								.indexOf(column);

						dimensionVA = perspective.getVirtualArray();

						DimensionGroupList groupList = dimensionVA
								.getGroupList();
						columnGroup = groupList.get(groupIndex);

						createDimensionPerspective = true;

					}

					AddDataContainerEvent event = new AddDataContainerEvent(
							dataDomain, recordPerspectiveID,
							createRecordPerspective, recordVA, rowGroup,
							dimensionPerspectiveID, createDimensionPerspective,
							dimensionVA, columnGroup);
					if (useContextMenu) {
						view.getContextMenuCreator().addContextMenuItem(
								new AddDataContainerItem(event));
					} else {
						event.setSender(this);
						GeneralManager.get().getEventPublisher()
								.triggerEvent(event);
					}
				}
			}

			private EmptyCellRenderer getEmptyCellRenderer(int id) {
				for (EmptyCellRenderer emptyCellRenderer : emptyCellRenderers
						.keySet()) {
					if (emptyCellRenderer.getID() == id) {
						return emptyCellRenderer;
					}
				}
				return null;
			}

		}, EMPTY_CELL_PICKING_TYPE + node.getID());

		view.addMultiIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				CellContainer container = null;

				for (CellContainer cellContainer : columns) {
					if (cellContainer.id.hashCode() == pick.getID()) {
						container = cellContainer;
						break;
					}
				}

				if (container == null) {
					for (CellContainer cellContainer : rows) {
						if (cellContainer.id.hashCode() == pick.getID()) {
							container = cellContainer;
							break;
						}
					}
				}

				if (container == null)
					return;

				container.isCollapsed = !container.isCollapsed;

				for (CellContainer child : container.childContainers) {

					boolean isAlwaysVisible = false;
					for (DimensionGroupRenderer dimensionGroupRenderer : dimensionGroupRenderers
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
				view.setDisplayListDirty();

			}

		}, COLLAPSE_BUTTON_PICKING_TYPE + node.getID());
	}

	private void createRowsAndColumns(List<DataContainer> dataContainers) {

		Set<String> rowIDs = dataDomain.getRecordPerspectiveIDs();
		Set<String> columnIDs = dataDomain.getDimensionPerspectiveIDs();

		// FIXME: Rows and columns do not change atm, but this might happen in
		// the future.

		rows.clear();
		columns.clear();
		Map<String, CellContainer> newRowMap = new HashMap<String, CellContainer>();
		Map<String, CellContainer> newColumnMap = new HashMap<String, CellContainer>();

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
			newRowMap.put(id, row);
			// rows.add(row);
			parentContainers.add(row);

			RecordGroupList groupList = perspective.getVirtualArray()
					.getGroupList();

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
						subRow.caption = group.getClusterNode().getLabel();
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

				Collections.sort(childList);
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
			column.childContainers.clear();
			newColumnMap.put(id, column);
			parentContainers.add(column);
			// columns.add(column);

			DimensionGroupList groupList = perspective.getVirtualArray()
					.getGroupList();

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
						subColumn.caption = group.getClusterNode().getLabel();
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

				Collections.sort(childList);
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
		emptyCellRenderers.clear();
		dimensionGroupRenderers.clear();

		int emptyCellId = 0;
		for (CellContainer column : columns) {
			int numSubdivisions = 1;
			for (CellContainer row : rows) {
				boolean dimensionGroupExists = false;
				for (DataContainer dataContainer : dataContainers) {

					// TableBasedDimensionGroupData tableBasedDimensionGroupData
					// = (TableBasedDimensionGroupData) dataContainer;
					// String recordPerspectiveID =
					// dataDomain.isColumnDimension() ? row.id
					// : column.id;
					// String dimensionPerspectiveID = dataDomain
					// .isColumnDimension() ? column.id : row.id;

					String recordPerspectiveID = row.id;
					String dimensionPerspectiveID = column.id;

					if (dataContainer.getDimensionPerspective().getID()
							.equals(dimensionPerspectiveID)
							&& dataContainer.getRecordPerspective().getID()
									.equals(recordPerspectiveID)) {
						numSubdivisions++;
						if (numSubdivisions >= rows.size()) {
							numSubdivisions = rows.size();
						}
						dimensionGroupExists = true;
						DimensionGroupRenderer dimensionGroupRenderer = new DimensionGroupRenderer(
								dataContainer, view, dragAndDropController,
								node, dataDomain.getColor().getRGBA());
						dimensionGroupRenderer
								.setRenderDimensionGroupLabel(false);
						cells.put(row.id + column.id, dimensionGroupRenderer);
						dimensionGroupRenderers.put(dimensionGroupRenderer,
								new Pair<CellContainer, CellContainer>(row,
										column));
						row.isVisible = true;
						column.isVisible = true;
						break;
					}
				}
				if (!dimensionGroupExists) {
					EmptyCellRenderer emptyCellRenderer = new EmptyCellRenderer(
							emptyCellId++);
					cells.put(row.id + column.id, emptyCellRenderer);
					emptyCellRenderers
							.put(emptyCellRenderer,
									new Pair<CellContainer, CellContainer>(row,
											column));
				}
			}
			column.numSubdivisions = numSubdivisions;
		}
	}

	@Override
	public void render(GL2 gl) {

		renderingStrategy.render(gl, rows, columns, cells,
				bottomDimensionGroupPositions, topDimensionGroupPositions, x,
				y, node, view, pickingIDsToBePushed);

		// float captionColumnWidth = calcMaxTextWidth(rows);
		// float captionRowHeight = calcMaxTextWidth(columns);
		// CaleydoTextRenderer textRenderer = view.getTextRenderer();
		//
		// PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		//
		// float currentPositionX = (x / 2.0f)
		// - pixelGLConverter
		// .getGLWidthForPixelWidth(getMinWidthPixels() / 2);
		// float rowHeight = pixelGLConverter
		// .getGLHeightForPixelHeight(ROW_HEIGHT_PIXELS);
		// float captionSpacingY = pixelGLConverter
		// .getGLHeightForPixelHeight(CAPTION_SPACING_PIXELS);
		//
		// float captionSpacingX = pixelGLConverter
		// .getGLWidthForPixelWidth(CAPTION_SPACING_PIXELS);
		//
		// float currentPositionY = y - captionRowHeight - captionSpacingY;
		// float textHeight = pixelGLConverter
		// .getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS);
		//
		// for (CellContainer row : rows) {
		// float textPositionY = currentPositionY - rowHeight
		// + (rowHeight - textHeight) / 2.0f
		// + pixelGLConverter.getGLHeightForPixelHeight(2);
		//
		// if (row.parentContainer == null) {
		//
		// gl.glColor3f(0.7f, 0.7f, 0.7f);
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(currentPositionX, currentPositionY - rowHeight, 0);
		// gl.glVertex3f(
		// currentPositionX
		// + captionColumnWidth
		// + pixelGLConverter
		// .getGLWidthForPixelWidth(CAPTION_SPACING_PIXELS),
		// currentPositionY - rowHeight, 0);
		// gl.glVertex3f(
		// currentPositionX
		// + captionColumnWidth
		// + pixelGLConverter
		// .getGLWidthForPixelWidth(CAPTION_SPACING_PIXELS),
		// currentPositionY, 0);
		// gl.glVertex3f(currentPositionX, currentPositionY, 0);
		// gl.glEnd();
		// }
		//
		// // gl.glColor3f(0, 0, 0);
		// textRenderer.setColor(new float[] { 0, 0, 0 });
		// textRenderer.renderTextInBounds(gl, row.caption, currentPositionX
		// + captionSpacingX, textPositionY, 0, captionColumnWidth - 2
		// * captionSpacingX, textHeight);
		//
		// gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
		// gl.glColor3f(0, 0, 0);
		// gl.glLineWidth(1);
		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3f(0, currentPositionY, 0.1f);
		// gl.glVertex3f(x, currentPositionY, 0.1f);
		// gl.glEnd();
		// gl.glPopAttrib();
		//
		// row.position = currentPositionY;
		//
		// currentPositionY -= rowHeight;
		//
		// }
		//
		// float columnWidth = pixelGLConverter
		// .getGLWidthForPixelWidth(COLUMN_WIDTH_PIXELS);
		// currentPositionX += captionColumnWidth
		// + pixelGLConverter
		// .getGLWidthForPixelWidth(CAPTION_SPACING_PIXELS);
		//
		// for (int i = 0; i < columns.size(); i++) {
		// CellContainer column = columns.get(i);
		// if (!column.isVisible) {
		// continue;
		// }
		// float currentColumnWidth = columnWidth * column.numSubdivisions;
		//
		// gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_LINE_BIT);
		//
		// float childIndent = 0;
		//
		// gl.glColor3f(0.7f, 0.7f, 0.7f);
		// if (column.parentContainer == null) {
		//
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(currentPositionX, y - captionRowHeight
		// - captionSpacingY, 0);
		// gl.glVertex3f(currentPositionX + currentColumnWidth, y
		// - captionRowHeight - captionSpacingY, 0);
		// gl.glVertex3f(currentPositionX + currentColumnWidth, y, 0);
		// gl.glVertex3f(currentPositionX, y, 0);
		// gl.glEnd();
		// } else {
		//
		// childIndent = captionSpacingY * 2;
		//
		// gl.glColor3f(0.8f, 0.8f, 0.8f);
		//
		// gl.glBegin(GL2.GL_QUADS);
		// gl.glVertex3f(currentPositionX, y - captionRowHeight
		// - captionSpacingY, 0);
		// gl.glVertex3f(currentPositionX + currentColumnWidth, y
		// - captionRowHeight - captionSpacingY, 0);
		// gl.glVertex3f(currentPositionX + currentColumnWidth, y, 0);
		// gl.glVertex3f(currentPositionX, y, 0);
		//
		// gl.glColor3f(0.7f, 0.7f, 0.7f);
		//
		// gl.glVertex3f(currentPositionX, y - childIndent, 0);
		// gl.glVertex3f(currentPositionX + currentColumnWidth, y
		// - childIndent, 0);
		// gl.glVertex3f(currentPositionX + currentColumnWidth, y, 0);
		// gl.glVertex3f(currentPositionX, y, 0);
		//
		// gl.glEnd();
		//
		// // gl.glColor3f(1,1,1);
		// // gl.glBegin(GL2.GL_LINES);
		// // gl.glVertex3f(currentPositionX, y - captionRowHeight
		// // - captionSpacingY, 1);
		// // gl.glVertex3f(currentPositionX, y - childIndent, 1);
		// // gl.glEnd();
		// }
		//
		// float textPositionX = currentPositionX
		// + (currentColumnWidth - textHeight) / 2.0f
		// + pixelGLConverter.getGLHeightForPixelHeight(2);
		//
		// gl.glPushMatrix();
		// gl.glTranslatef(textPositionX, y - childIndent - captionSpacingY, 0);
		// gl.glRotatef(-90, 0, 0, 1);
		// // gl.glColor3f(0, 0, 0);
		// textRenderer.setColor(new float[] { 0, 0, 0 });
		// textRenderer.renderTextInBounds(gl, column.caption, 0, 0, 0,
		// captionRowHeight - childIndent - 2 * captionSpacingY,
		// textHeight);
		// gl.glPopMatrix();
		//
		// gl.glColor3f(0, 0, 0);
		// if ((column.parentContainer != null) && (i != 0)
		// && (columns.get(i - 1) != column.parentContainer)) {
		// gl.glColor3f(0.5f, 0.5f, 0.5f);
		// }
		// gl.glLineWidth(1);
		// gl.glBegin(GL2.GL_LINES);
		// gl.glVertex3f(currentPositionX, 0, 0);
		// gl.glVertex3f(currentPositionX, y - childIndent, 0);
		// // for (int i = 1; i < column.numSubdivisions; i++) {
		// // gl.glVertex3f(currentPositionX + i * columnWidth, 0, 0);
		// // gl.glVertex3f(currentPositionX + i * columnWidth, y
		// // - captionRowHeight - captionSpacingY, 0);
		// // }
		// gl.glEnd();
		//
		// float currentDimGroupPositionX = currentPositionX;
		//
		// for (CellContainer row : rows) {
		// float cellSpacingX = pixelGLConverter
		// .getGLWidthForPixelWidth(CELL_SPACING_PIXELS);
		// float cellSpacingY = pixelGLConverter
		// .getGLHeightForPixelHeight(CELL_SPACING_PIXELS);
		//
		// float emptyCellPositionX = currentPositionX
		// + currentColumnWidth - columnWidth;
		//
		// // boolean dimensionGroupExists = false;
		//
		// ColorRenderer cell = cells.get(row.id + column.id);
		//
		// gl.glPushMatrix();
		// int pickingID = 0;
		// if (cell instanceof DimensionGroupRenderer) {
		//
		// pickingID = view.getPickingManager().getPickingID(
		// view.getID(),
		// DIMENSION_GROUP_PICKING_TYPE + node.getID(),
		// ((DimensionGroupRenderer) cell)
		// .getDimensionGroupData().getID());
		//
		// gl.glTranslatef(currentDimGroupPositionX + cellSpacingX,
		// row.position - rowHeight + cellSpacingY, 0);
		//
		// Point2D bottomPosition1 = new Point2D.Float(
		// currentDimGroupPositionX + cellSpacingX,
		// row.position - rowHeight + cellSpacingY);
		// Point2D bottomPosition2 = new Point2D.Float(
		// (float) bottomPosition1.getX()
		// + pixelGLConverter
		// .getGLWidthForPixelWidth(CELL_SIZE_PIXELS),
		// (float) bottomPosition1.getY());
		// Point2D topPosition1 = new Point2D.Float(
		// (float) bottomPosition1.getX(), row.position
		// - cellSpacingY);
		// Point2D topPosition2 = new Point2D.Float(
		// (float) bottomPosition2.getX(),
		// (float) topPosition1.getY());
		//
		// bottomDimensionGroupPositions.put(
		// ((DimensionGroupRenderer) cell)
		// .getDimensionGroupData().getID(),
		// new Pair<Point2D, Point2D>(bottomPosition1,
		// bottomPosition2));
		// topDimensionGroupPositions.put(
		// ((DimensionGroupRenderer) cell)
		// .getDimensionGroupData().getID(),
		// new Pair<Point2D, Point2D>(topPosition1,
		// topPosition2));
		//
		// currentDimGroupPositionX += columnWidth;
		// } else {
		//
		// pickingID = view.getPickingManager().getPickingID(
		// view.getID(),
		// EMPTY_CELL_PICKING_TYPE + node.getID(),
		// ((EmptyCellRenderer) cell).getID());
		//
		// gl.glTranslatef(emptyCellPositionX + cellSpacingX,
		// row.position - rowHeight + cellSpacingY, 0);
		// }
		// cell.setLimits(pixelGLConverter
		// .getGLWidthForPixelWidth(CELL_SIZE_PIXELS),
		// pixelGLConverter
		// .getGLHeightForPixelHeight(CELL_SIZE_PIXELS));
		// gl.glPushName(pickingID);
		// cell.render(gl);
		// gl.glPopName();
		// gl.glPopMatrix();
		// }
		//
		// gl.glPopAttrib();
		//
		// column.position = currentPositionX;
		//
		// currentPositionX += currentColumnWidth;
		// }

	}

	// private float calcMaxTextWidth(List<CellContainer> containers) {
	//
	// CaleydoTextRenderer textRenderer = view.getTextRenderer();
	// PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
	//
	// float maxTextWidth = Float.MIN_VALUE;
	//
	// for (CellContainer container : containers) {
	// float textWidth = textRenderer.getRequiredTextWidthWithMax(
	// container.id, pixelGLConverter
	// .getGLHeightForPixelHeight(TEXT_HEIGHT_PIXELS),
	// pixelGLConverter
	// .getGLWidthForPixelWidth(MAX_TEXT_WIDTH_PIXELS));
	// if (textWidth > maxTextWidth)
	// maxTextWidth = textWidth;
	// }
	//
	// return maxTextWidth;
	// }

	@Override
	public int getMinWidthPixels() {

		return renderingStrategy.getMinWidthPixels(rows, columns, view);

		// PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		//
		// int captionWidth = pixelGLConverter
		// .getPixelWidthForGLWidth(calcMaxTextWidth(rows));
		//
		// int sumColumnWidth = 0;
		//
		// for (CellContainer column : columns) {
		// if (column.isVisible) {
		// sumColumnWidth += column.numSubdivisions * COLUMN_WIDTH_PIXELS;
		// }
		// }
		//
		// return captionWidth + sumColumnWidth + CAPTION_SPACING_PIXELS;

	}

	@Override
	public int getMinHeightPixels() {

		return renderingStrategy.getMinHeightPixels(rows, columns, view);
		// PixelGLConverter pixelGLConverter = view.getPixelGLConverter();
		//
		// int captionWidth = pixelGLConverter
		// .getPixelHeightForGLHeight(calcMaxTextWidth(columns));
		//
		// return captionWidth + rows.size() * ROW_HEIGHT_PIXELS
		// + CAPTION_SPACING_PIXELS;

	}

	@Override
	public void setDataContainers(List<DataContainer> dataContainers) {
		createRowsAndColumns(dataContainers);
	}

	@Override
	public void setUpsideDown(boolean isUpsideDown) {
		this.isUpsideDown = isUpsideDown;
		renderingStrategy = (isUpsideDown) ? new BottomUpDataContainerMatrixRenderingStrategy()
				: new TopDownDataContainerMatrixRenderingStrategy();
	}

	@Override
	public void removePickingListeners() {
		view.removeMultiIDPickingListeners(EMPTY_CELL_PICKING_TYPE
				+ node.getID());
		view.removeMultiIDPickingListeners(DIMENSION_GROUP_PICKING_TYPE
				+ node.getID());
		view.removeMultiIDPickingListeners(COLLAPSE_BUTTON_PICKING_TYPE
				+ node.getID());

	}

}
