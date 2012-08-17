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
package org.caleydo.view.dvi.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.ToolTipPickingListener;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.contextmenu.CreateClusteringItem;
import org.caleydo.view.dvi.contextmenu.LoadGroupingItem;
import org.caleydo.view.dvi.contextmenu.ShowViewWithoutDataItem;
import org.caleydo.view.dvi.event.OpenVendingMachineEvent;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.caleydo.view.dvi.tableperspective.AMultiTablePerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.PerspectiveRenderer;
import org.caleydo.view.dvi.tableperspective.TablePerspectiveListRenderer;
import org.caleydo.view.dvi.tableperspective.matrix.TablePerspectiveMatrixRenderer;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class TableBasedDataNode extends ADataNode implements IDropArea {

	private final static String TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_TYPE = "org.caleydo.view.dvi.toggletableperspectivebutton";
	private final static int TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_ID = 0;

	private final static String TRIGGER_VENDING_MACHINE_BUTTON_PICKING_TYPE = "org.caleydo.view.dvi.triggervendingmachinebutton";
	private final static int TRIGGER_VENDING_MACHINE_BUTTON_PICKING_ID = 1;

	private ATableBasedDataDomain dataDomain;

	private ButtonRenderer toggleTablePerspectiveButtonRenderer;
	private Button toggleTablePerspectiveButton;

	private ButtonRenderer triggerVendingMachineButtonRenderer;
	private Button triggerVendingMachineButton;

	private ALayoutState currentState;
	private OverviewState overviewState;
	private DetailState detailState;
	private ElementLayout tablePerspectiveLayout;
	private AMultiTablePerspectiveRenderer tablePerspectiveRenderer;
	private Row bodyRow;
	private List<TablePerspective> tablePerspectives;

	private abstract class ALayoutState {
		protected AMultiTablePerspectiveRenderer tablePerspectiveRenderer;
		protected int textureRotation;

		public void apply() {
			TableBasedDataNode.this.tablePerspectiveRenderer.unregisterPickingListeners();
			TableBasedDataNode.this.tablePerspectiveRenderer = tablePerspectiveRenderer;
			tablePerspectiveRenderer.setUpsideDown(isUpsideDown);
			tablePerspectiveLayout.setRenderer(tablePerspectiveRenderer);
			toggleTablePerspectiveButtonRenderer
					.setTextureRotation(currentState.textureRotation);
			tablePerspectiveRenderer.setTablePerspectives(getTablePerspectives());
			tablePerspectiveRenderer.unregisterPickingListeners();
			tablePerspectiveRenderer.registerPickingListeners();
			recalculateNodeSize();
			graphLayout.fitNodesToDrawingArea(view.calculateGraphDrawingArea());
		}

		public abstract ALayoutState getNextState();
	}

	private class OverviewState extends ALayoutState {

		public OverviewState() {
			tablePerspectiveRenderer = new TablePerspectiveListRenderer(
					TableBasedDataNode.this, view, dragAndDropController,
					getTablePerspectives());
			List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
			pickingIDsToBePushed.add(new Pair<String, Integer>(
					DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

			tablePerspectiveRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);
			textureRotation = ButtonRenderer.TEXTURE_ROTATION_270;
		}

		@Override
		public ALayoutState getNextState() {
			return detailState;
		}

		@Override
		public void apply() {
			super.apply();
			bodyRow.clearBackgroundRenderers();
			if (getTablePerspectives().size() > 0) {
				bodyRow.addBackgroundRenderer(new ColorRenderer(
						new float[] { 1, 1, 1, 1 }));
			}
		}
	}

	private class DetailState extends ALayoutState {

		public DetailState() {
			tablePerspectiveRenderer = new TablePerspectiveMatrixRenderer(dataDomain,
					view, TableBasedDataNode.this, dragAndDropController);
			List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
			pickingIDsToBePushed.add(new Pair<String, Integer>(
					DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

			tablePerspectiveRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);
			textureRotation = ButtonRenderer.TEXTURE_ROTATION_90;
		}

		@Override
		public ALayoutState getNextState() {
			return overviewState;
		}

		@Override
		public void apply() {
			super.apply();
			bodyRow.clearBackgroundRenderers();
			bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
		}
	}

	public TableBasedDataNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, Integer id,
			IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id, dataDomain);
		this.dataDomain = (ATableBasedDataDomain) dataDomain;

		overviewState = new OverviewState();
		detailState = new DetailState();
		currentState = overviewState;
		tablePerspectiveRenderer = currentState.tablePerspectiveRenderer;
	}

	@Override
	protected void registerPickingListeners() {
		super.registerPickingListeners();

		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				currentState = currentState.getNextState();
				currentState.apply();
				// recalculateNodeSize();
				// graphLayout.updateNodePositions();

				view.setDisplayListDirty();
			}

		}, TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_TYPE + getID(),
				TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_ID);

		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {

				OpenVendingMachineEvent event = new OpenVendingMachineEvent(dataDomain);
				event.setSender(view);
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}

		}, TRIGGER_VENDING_MACHINE_BUTTON_PICKING_TYPE + getID(),
				TRIGGER_VENDING_MACHINE_BUTTON_PICKING_ID);

		view.addIDPickingListener(new APickingListener() {

			@Override
			public void dragged(Pick pick) {

				// DragAndDropController dragAndDropController =
				// dragAndDropController;
				if (dragAndDropController.isDragging()
						&& dragAndDropController.getDraggingMode().equals(
								"PerspectiveDrag")) {
					dragAndDropController.setDropArea(TableBasedDataNode.this);
				}

			}

			@Override
			public void rightClicked(Pick pick) {
				ContextMenuCreator contextMenuCreator = view.getContextMenuCreator();
				contextMenuCreator.addContextMenuItem(new LoadGroupingItem(dataDomain,
						dataDomain.getDimensionIDCategory()));
				contextMenuCreator.addContextMenuItem(new LoadGroupingItem(dataDomain,
						dataDomain.getRecordIDCategory()));

				contextMenuCreator.addContextMenuItem(new CreateClusteringItem(
						dataDomain, true));
				contextMenuCreator.addContextMenuItem(new CreateClusteringItem(
						dataDomain, false));
			}

		}, DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);

		// FIXME: Bad hack
		if (dataDomain.getLabel().toLowerCase().contains("copy")
				|| dataDomain.getLabel().toLowerCase().contains("mutation")) {
			final boolean isCopyNumber = dataDomain.getLabel().toLowerCase()
					.contains("copy");

			view.addIDPickingListener(new ToolTipPickingListener(view, "To create a "
					+ (isCopyNumber ? "copy number" : "mutation status")
					+ " categorization for one gene use the Search view."),
					DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);

			view.addIDPickingListener(new APickingListener() {

				@Override
				public void rightClicked(Pick pick) {

					IExtensionRegistry registry = Platform.getExtensionRegistry();
					IConfigurationElement[] viewElements = registry
							.getConfigurationElementsFor("org.eclipse.ui.views");
					boolean viewExists = false;
					for (IConfigurationElement element : viewElements) {

						String bundleID = element.getAttribute("id");
						if (bundleID.startsWith("org.caleydo.view.search")) {
							viewExists = true;
							break;
						}

					}
					if (viewExists) {
						view.getContextMenuCreator().addContextMenuItem(
								new ShowViewWithoutDataItem("org.caleydo.view.search",
										"Create Categorization of a Gene's "
												+ (isCopyNumber ? "Copy Number"
														: "Mutation") + " Status"));

					}

				}
			}, DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
		}
		if (dataDomain.getLabel().contains("Clinical")) {
			view.addIDPickingListener(
					new ToolTipPickingListener(view,
							"To add clinical data to StratomeX use context menu of a data column in StratomeX."),
					DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
		}
	}

	@Override
	protected ElementLayout setupLayout() {

		Row baseRow = createDefaultBaseRow(dataDomain.getColor().getRGBA(), getID());
		ElementLayout spacingLayoutX = createDefaultSpacingX();

		baseColumn = new Column();

		baseRow.append(spacingLayoutX);
		baseRow.append(baseColumn);
		baseRow.append(spacingLayoutX);

		Row titleRow = new Row("titleRow");

		ElementLayout captionLayout = createDefaultCaptionLayout(getID());

		titleRow.append(captionLayout);
		titleRow.setYDynamic(true);

		ElementLayout lineSeparatorLayout = createDefaultLineSeparatorLayout();

		if (view.isVendingMachineMode()) {
			ElementLayout vendingMachineButtonLayout = new ElementLayout(
					"vendingMachineButtonLayout");
			vendingMachineButtonLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
			vendingMachineButtonLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
			triggerVendingMachineButton = new Button(
					TRIGGER_VENDING_MACHINE_BUTTON_PICKING_TYPE + getID(),
					TRIGGER_VENDING_MACHINE_BUTTON_PICKING_ID,
					EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);
			triggerVendingMachineButtonRenderer = new ButtonRenderer(
					triggerVendingMachineButton, view);
			triggerVendingMachineButtonRenderer.addPickingID(
					DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
			triggerVendingMachineButtonRenderer.setZCoordinate(1);
			vendingMachineButtonLayout.setRenderer(triggerVendingMachineButtonRenderer);
			titleRow.append(spacingLayoutX);
			titleRow.append(vendingMachineButtonLayout);
		}

		ElementLayout toggleTablePerspectiveButtonLayout = new ElementLayout(
				"toggleTablePerspectiveLayout");
		toggleTablePerspectiveButtonLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		toggleTablePerspectiveButtonLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
		toggleTablePerspectiveButton = new Button(
				TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_TYPE + getID(),
				TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_ID,
				EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);

		if (dataDomain.getRecordPerspectiveIDs().size() < 1
				&& dataDomain.getDimensionPerspectiveIDs().size() < 1) {
			toggleTablePerspectiveButton.setVisible(false);
		}
		toggleTablePerspectiveButtonRenderer = new ButtonRenderer(
				toggleTablePerspectiveButton, view);
		toggleTablePerspectiveButtonRenderer.addPickingID(
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
		toggleTablePerspectiveButtonRenderer.setZCoordinate(1);
		toggleTablePerspectiveButtonLayout
				.setRenderer(toggleTablePerspectiveButtonRenderer);

		// FIXME: Very bad hack
		if ((!dataDomain.getLabel().toLowerCase().contains("copy"))
				&& (!dataDomain.getLabel().toLowerCase().contains("clinical"))
				&& (!dataDomain.getLabel().toLowerCase().contains("mutation"))) {
			titleRow.append(spacingLayoutX);
			titleRow.append(toggleTablePerspectiveButtonLayout);
		}

		bodyRow = new Row("bodyRow");

		bodyColumn = new Column("bodyColumn");

		tablePerspectiveLayout = new ElementLayout("tablePerspectiveLayout");
		tablePerspectiveLayout.setRatioSizeY(1);
		tablePerspectiveLayout.setRenderer(tablePerspectiveRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(tablePerspectiveLayout);
		bodyColumn.append(spacingLayoutY);

		bodyRow.append(bodyColumn);

		baseColumn.append(spacingLayoutY);
		baseColumn.append(bodyRow);
		baseColumn.append(spacingLayoutY);
		baseColumn.append(lineSeparatorLayout);
		baseColumn.append(titleRow);
		baseColumn.append(spacingLayoutY);

		setUpsideDown(isUpsideDown);

		currentState.apply();
		// recalculateNodeSize();
		graphLayout.fitNodesToDrawingArea(view.calculateGraphDrawingArea());

		return baseRow;
	}

	@Override
	public void destroy() {
		view.removeAllIDPickingListeners(TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_TYPE
				+ getID(), TOGGLE_TABLE_PERSPECTIVE_BUTTON_PICKING_ID);
		view.removeAllIDPickingListeners(TRIGGER_VENDING_MACHINE_BUTTON_PICKING_TYPE
				+ getID(), TRIGGER_VENDING_MACHINE_BUTTON_PICKING_ID);
		view.removeAllIDPickingListeners(DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
		tablePerspectiveRenderer.destroy();
	}

	@Override
	public void update() {

		retrieveTablePerspectives();
		if (dataDomain.getRecordPerspectiveIDs().size() > 1
				|| dataDomain.getDimensionPerspectiveIDs().size() > 1) {
			toggleTablePerspectiveButton.setVisible(true);
		}
		currentState.apply();
		tablePerspectiveRenderer.setTablePerspectives(getTablePerspectives());
	}

	@Override
	protected AMultiTablePerspectiveRenderer getTablePerspectiveRenderer() {
		return tablePerspectiveRenderer;
	}

	protected void retrieveTablePerspectives() {
		Collection<TablePerspective> containerCollection = dataDomain
				.getAllTablePerspectives();
		if (containerCollection == null) {
			tablePerspectives = new ArrayList<TablePerspective>();
			return;
		}
		// List<Pair<String, TablePerspective>> sortedParentTablePerspectives =
		// new
		// ArrayList<Pair<String, TablePerspective>>();
		// for (TablePerspective container : containerCollection) {
		// sortedParentTablePerspectives.add(new Pair<String,
		// TablePerspective>(container
		// .getLabel(), container));
		// }

		Set<String> recordPerspectiveIDs = dataDomain.getRecordPerspectiveIDs();

		List<Pair<String, RecordPerspective>> parentRecordPerspectives = new ArrayList<Pair<String, RecordPerspective>>();
		Map<RecordPerspective, List<Pair<String, RecordPerspective>>> childRecordPerspectiveLists = new HashMap<RecordPerspective, List<Pair<String, RecordPerspective>>>();

		for (String perspectiveID : recordPerspectiveIDs) {
			RecordPerspective perspective = dataDomain.getTable().getRecordPerspective(
					perspectiveID);

			if (perspective.isPrivate()) {
				continue;
			}

			parentRecordPerspectives.add(new Pair<String, RecordPerspective>(perspective
					.getLabel(), perspective));

			RecordGroupList groupList = perspective.getVirtualArray().getGroupList();

			if (groupList != null) {
				List<Pair<String, RecordPerspective>> childList = new ArrayList<Pair<String, RecordPerspective>>(
						groupList.size());
				for (int i = 0; i < groupList.size(); i++) {

					Group group = groupList.get(i);
					if (group.getPerspectiveID() != null) {

						RecordPerspective childPerspective = dataDomain.getTable()
								.getRecordPerspective(group.getPerspectiveID());
						childList.add(new Pair<String, RecordPerspective>(
								childPerspective.getLabel(), childPerspective));
					}
				}

				// Collections.sort(childList);
				childRecordPerspectiveLists.put(perspective, childList);
			}

		}

		Collections.sort(parentRecordPerspectives);

		List<RecordPerspective> sortedRecordPerspectives = new ArrayList<RecordPerspective>();

		for (Pair<String, RecordPerspective> parentPair : parentRecordPerspectives) {
			sortedRecordPerspectives.add(parentPair.getSecond());

			List<Pair<String, RecordPerspective>> childList = childRecordPerspectiveLists
					.get(parentPair.getSecond());

			if (childList != null) {
				for (Pair<String, RecordPerspective> childPair : childList) {
					sortedRecordPerspectives.add(childPair.getSecond());
				}
			}
		}

		Set<String> dimensionPerspectiveIDs = dataDomain.getDimensionPerspectiveIDs();

		List<Pair<String, DimensionPerspective>> parentDimensionPerspectives = new ArrayList<Pair<String, DimensionPerspective>>();
		Map<DimensionPerspective, List<Pair<String, DimensionPerspective>>> childDimensionPerspectiveLists = new HashMap<DimensionPerspective, List<Pair<String, DimensionPerspective>>>();

		for (String perspectiveID : dimensionPerspectiveIDs) {
			DimensionPerspective perspective = dataDomain.getTable()
					.getDimensionPerspective(perspectiveID);

			if (perspective.isPrivate()) {
				continue;
			}

			parentDimensionPerspectives.add(new Pair<String, DimensionPerspective>(
					perspective.getLabel(), perspective));

			DimensionGroupList groupList = perspective.getVirtualArray().getGroupList();

			if (groupList != null) {
				List<Pair<String, DimensionPerspective>> childList = new ArrayList<Pair<String, DimensionPerspective>>(
						groupList.size());
				for (int i = 0; i < groupList.size(); i++) {

					Group group = groupList.get(i);
					if (group.getPerspectiveID() != null) {

						DimensionPerspective childPerspective = dataDomain.getTable()
								.getDimensionPerspective(group.getPerspectiveID());
						childList.add(new Pair<String, DimensionPerspective>(
								childPerspective.getLabel(), childPerspective));
					}
				}

				// Collections.sort(childList);
				childDimensionPerspectiveLists.put(perspective, childList);
			}

		}

		Collections.sort(parentDimensionPerspectives);

		List<DimensionPerspective> sortedDimensionPerspectives = new ArrayList<DimensionPerspective>();

		for (Pair<String, DimensionPerspective> parentPair : parentDimensionPerspectives) {
			sortedDimensionPerspectives.add(parentPair.getSecond());

			List<Pair<String, DimensionPerspective>> childList = childDimensionPerspectiveLists
					.get(parentPair.getSecond());

			if (childList != null) {
				for (Pair<String, DimensionPerspective> childPair : childList) {
					sortedDimensionPerspectives.add(childPair.getSecond());
				}
			}
		}

		tablePerspectives = new ArrayList<TablePerspective>(containerCollection.size());

		for (DimensionPerspective dimensionPerspective : sortedDimensionPerspectives) {
			for (RecordPerspective recordPerspective : sortedRecordPerspectives) {
				if (dataDomain.hasTablePerspective(recordPerspective.getPerspectiveID(),
						dimensionPerspective.getPerspectiveID())) {
					tablePerspectives.add(dataDomain.getTablePerspective(
							recordPerspective.getPerspectiveID(),
							dimensionPerspective.getPerspectiveID()));
				}
			}
		}

		// for (Pair<String, TablePerspective> pair :
		// sortedParentTablePerspectives) {
		// tablePerspectives.add(pair.getSecond());
		// }
	}

	@Override
	public List<TablePerspective> getTablePerspectives() {

		if (tablePerspectives == null)
			retrieveTablePerspectives();

		return tablePerspectives;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {
		// ArrayList<TablePerspective> tablePerspectives = new
		// ArrayList<TablePerspective>();
		for (IDraggable draggable : draggables) {
			if (draggable instanceof PerspectiveRenderer) {
				PerspectiveRenderer perspectiveRenderer = (PerspectiveRenderer) draggable;
				ATableBasedDataDomain foreignDataDomain = perspectiveRenderer
						.getDataDomain();
				if (foreignDataDomain != this.dataDomain) {
					if (perspectiveRenderer.isRecordPerspective()) {
						RecordPerspective recordPerspective = foreignDataDomain
								.getTable().getRecordPerspective(
										perspectiveRenderer.getPerspectiveID());

						RecordPerspective convertedPerspective = this.dataDomain
								.convertForeignRecordPerspective(recordPerspective);
						convertedPerspective.setDefault(false);
						this.dataDomain.getTable().registerRecordPerspective(
								convertedPerspective);
					}
				}
				// tablePerspectives.add(dimensionGroupRenderer.getTablePerspective());
			}
		}

		// if (!tablePerspectives.isEmpty())
		// {
		// // FIXME: this needs to be looked at again
		// // System.out.println("Drop");
		// AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(
		// tablePerspectives.get(0));
		// event.setReceiver((GLVisBricks) representedView);
		// event.setSender(this);
		// GeneralManager.get().getEventPublisher().triggerEvent(event);
		// }

	}

	@Override
	protected int getMinTitleBarWidthPixels() {
		float textWidth = view.getTextRenderer().getRequiredTextWidth(
				dataDomain.getLabel(),
				pixelGLConverter.getGLHeightForPixelHeight(CAPTION_HEIGHT_PIXELS));

		return pixelGLConverter.getPixelWidthForGLWidth(textWidth)
				+ CAPTION_HEIGHT_PIXELS + SPACING_PIXELS;
	}

	@Override
	public void handleDropAreaReplaced() {
		// TODO Auto-generated method stub

	}

}
