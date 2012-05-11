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
package org.caleydo.view.datagraph.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.group.DimensionGroupList;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.RecordGroupList;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.layout.util.ColorRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DataContainerListRenderer;
import org.caleydo.view.datagraph.datacontainer.PerspectiveRenderer;
import org.caleydo.view.datagraph.datacontainer.matrix.DataContainerMatrixRenderer;
import org.caleydo.view.datagraph.event.OpenVendingMachineEvent;
import org.caleydo.view.datagraph.layout.AGraphLayout;

public class TableBasedDataNode
	extends ADataNode
	implements IDropArea {

	private final static String TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE = "org.caleydo.view.datagraph.toggledatacontainerbutton";
	private final static int TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID = 0;

	private final static String TRIGGER_VENDING_MACHINE_BUTTON_PICKING_TYPE = "org.caleydo.view.datagraph.triggervendingmachinebutton";
	private final static int TRIGGER_VENDING_MACHINE_BUTTON_PICKING_ID = 1;

	private ATableBasedDataDomain dataDomain;

	private ButtonRenderer toggleDataContainerButtonRenderer;
	private Button toggleDataContainerButton;

	private ButtonRenderer triggerVendingMachineButtonRenderer;
	private Button triggerVendingMachineButton;

	private ALayoutState currentState;
	private OverviewState overviewState;
	private DetailState detailState;
	private ElementLayout dataContainerLayout;
	private ADataContainerRenderer dataContainerRenderer;
	private Row bodyRow;
	private List<DataContainer> dataContainers;

	private abstract class ALayoutState {
		protected ADataContainerRenderer dataContainerRenderer;
		protected int textureRotation;

		public void apply() {
			TableBasedDataNode.this.dataContainerRenderer.unregisterPickingListeners();
			TableBasedDataNode.this.dataContainerRenderer = dataContainerRenderer;
			dataContainerRenderer.registerPickingListeners();
			dataContainerRenderer.setUpsideDown(isUpsideDown);
			dataContainerLayout.setRenderer(dataContainerRenderer);
			toggleDataContainerButtonRenderer.setTextureRotation(currentState.textureRotation);
			dataContainerRenderer.setDataContainers(getDataContainers());
			recalculateNodeSize();
			graphLayout.updateNodePositions();
		}

		public abstract ALayoutState getNextState();
	}

	private class OverviewState
		extends ALayoutState {

		public OverviewState() {
			dataContainerRenderer = new DataContainerListRenderer(TableBasedDataNode.this,
					view, dragAndDropController, getDataContainers());
			List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
			pickingIDsToBePushed.add(new Pair<String, Integer>(
					DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

			dataContainerRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);
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
			if (getDataContainers().size() > 0) {
				bodyRow.addBackgroundRenderer(new ColorRenderer(new float[] { 1, 1, 1, 1 }));
			}
		}
	}

	private class DetailState
		extends ALayoutState {

		public DetailState() {
			dataContainerRenderer = new DataContainerMatrixRenderer(dataDomain, view,
					TableBasedDataNode.this, dragAndDropController);
			List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
			pickingIDsToBePushed.add(new Pair<String, Integer>(
					DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

			dataContainerRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);
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
			DragAndDropController dragAndDropController, Integer id, IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id, dataDomain);
		this.dataDomain = (ATableBasedDataDomain) dataDomain;

		overviewState = new OverviewState();
		detailState = new DetailState();
		currentState = overviewState;
		dataContainerRenderer = currentState.dataContainerRenderer;

		addPickingListeners();
	}

	private void addPickingListeners() {
		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				currentState = currentState.getNextState();
				currentState.apply();
				// recalculateNodeSize();
				// graphLayout.updateNodePositions();

				view.setDisplayListDirty();
			}

		}, TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + getID(),
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID);

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
						&& dragAndDropController.getDraggingMode().equals("PerspectiveDrag")) {
					dragAndDropController.setDropArea(TableBasedDataNode.this);
				}

			}
		}, DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
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

		ElementLayout captionLayout = createDefaultCaptionLayout(dataDomain.getLabel(),
				getID());

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

		ElementLayout toggleDataContainerButtonLayout = new ElementLayout(
				"toggleDataContainerLayout");
		toggleDataContainerButtonLayout.setPixelSizeY(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerButtonLayout.setPixelSizeX(CAPTION_HEIGHT_PIXELS);
		toggleDataContainerButton = new Button(TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE
				+ getID(), TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID,
				EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);

		if (dataDomain.getRecordPerspectiveIDs().size() < 1
				&& dataDomain.getDimensionPerspectiveIDs().size() < 1) {
			toggleDataContainerButton.setVisible(false);
		}
		toggleDataContainerButtonRenderer = new ButtonRenderer(toggleDataContainerButton,
				view);
		toggleDataContainerButtonRenderer.addPickingID(
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
		toggleDataContainerButtonRenderer.setZCoordinate(1);
		toggleDataContainerButtonLayout.setRenderer(toggleDataContainerButtonRenderer);
		titleRow.append(spacingLayoutX);
		titleRow.append(toggleDataContainerButtonLayout);

		bodyRow = new Row("bodyRow");

		bodyColumn = new Column("bodyColumn");

		dataContainerLayout = new ElementLayout("dataContainerLayout");
		dataContainerLayout.setRatioSizeY(1);
		dataContainerLayout.setRenderer(dataContainerRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(dataContainerLayout);
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
		graphLayout.updateNodePositions();

		return baseRow;
	}

	@Override
	public void destroy() {
		view.removeAllIDPickingListeners(TOGGLE_DATA_CONTAINER_BUTTON_PICKING_TYPE + getID(),
				TOGGLE_DATA_CONTAINER_BUTTON_PICKING_ID);
		view.removeAllIDPickingListeners(
				TRIGGER_VENDING_MACHINE_BUTTON_PICKING_TYPE + getID(),
				TRIGGER_VENDING_MACHINE_BUTTON_PICKING_ID);
		view.removeAllIDPickingListeners(DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
		dataContainerRenderer.destroy();
	}

	@Override
	public void update() {

		retrieveDataContainers();
		if (dataDomain.getRecordPerspectiveIDs().size() > 1
				|| dataDomain.getDimensionPerspectiveIDs().size() > 1) {
			toggleDataContainerButton.setVisible(true);
		}
		currentState.apply();
		dataContainerRenderer.setDataContainers(getDataContainers());
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer() {
		return dataContainerRenderer;
	}

	protected void retrieveDataContainers() {
		Collection<DataContainer> containerCollection = dataDomain.getAllDataContainers();
		if (containerCollection == null) {
			dataContainers = new ArrayList<DataContainer>();
			return;
		}
//		List<Pair<String, DataContainer>> sortedParentDataContainers = new ArrayList<Pair<String, DataContainer>>();
//		for (DataContainer container : containerCollection) {
//			sortedParentDataContainers.add(new Pair<String, DataContainer>(container
//					.getLabel(), container));
//		}

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
						childList.add(new Pair<String, RecordPerspective>(childPerspective
								.getLabel(), childPerspective));
					}
				}

//				Collections.sort(childList);
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
			DimensionPerspective perspective = dataDomain.getTable().getDimensionPerspective(
					perspectiveID);

			if (perspective.isPrivate()) {
				continue;
			}

			parentDimensionPerspectives.add(new Pair<String, DimensionPerspective>(perspective
					.getLabel(), perspective));

			DimensionGroupList groupList = perspective.getVirtualArray().getGroupList();

			if (groupList != null) {
				List<Pair<String, DimensionPerspective>> childList = new ArrayList<Pair<String, DimensionPerspective>>(
						groupList.size());
				for (int i = 0; i < groupList.size(); i++) {

					Group group = groupList.get(i);
					if (group.getPerspectiveID() != null) {

						DimensionPerspective childPerspective = dataDomain.getTable()
								.getDimensionPerspective(group.getPerspectiveID());
						childList.add(new Pair<String, DimensionPerspective>(childPerspective
								.getLabel(), childPerspective));
					}
				}

//				Collections.sort(childList);
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

		dataContainers = new ArrayList<DataContainer>(containerCollection.size());

		for (DimensionPerspective dimensionPerspective : sortedDimensionPerspectives) {
			for (RecordPerspective recordPerspective : sortedRecordPerspectives) {
				if (dataDomain.hasDataContainer(recordPerspective.getID(),
						dimensionPerspective.getID())) {
					dataContainers.add(dataDomain.getDataContainer(recordPerspective.getID(),
							dimensionPerspective.getID()));
				}
			}
		}

//		for (Pair<String, DataContainer> pair : sortedParentDataContainers) {
//			dataContainers.add(pair.getSecond());
//		}
	}

	@Override
	public List<DataContainer> getDataContainers() {

		if (dataContainers == null)
			retrieveDataContainers();

		return dataContainers;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {
		// ArrayList<DataContainer> dataContainers = new
		// ArrayList<DataContainer>();
		for (IDraggable draggable : draggables) {
			if (draggable instanceof PerspectiveRenderer) {
				PerspectiveRenderer perspectiveRenderer = (PerspectiveRenderer) draggable;
				ATableBasedDataDomain foreignDataDomain = perspectiveRenderer.getDataDomain();
				if (foreignDataDomain != this.dataDomain) {
					if (perspectiveRenderer.isRecordPerspective()) {
						RecordPerspective recordPerspective = foreignDataDomain.getTable()
								.getRecordPerspective(perspectiveRenderer.getPerspectiveID());

						RecordPerspective convertedPerspective = this.dataDomain
								.convertForeignRecordPerspective(recordPerspective);
						convertedPerspective.setDefault(false);
						this.dataDomain.getTable().registerRecordPerspective(
								convertedPerspective);
					}
				}
				// dataContainers.add(dimensionGroupRenderer.getDataContainer());
			}
		}

		// if (!dataContainers.isEmpty())
		// {
		// // FIXME: this needs to be looked at again
		// // System.out.println("Drop");
		// AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(
		// dataContainers.get(0));
		// event.setReceiver((GLVisBricks) representedView);
		// event.setSender(this);
		// GeneralManager.get().getEventPublisher().triggerEvent(event);
		// }

	}

	@Override
	protected int getMinTitleBarWidthPixels() {
		float textWidth = view.getTextRenderer().getRequiredTextWidth(dataDomain.getLabel(),
				pixelGLConverter.getGLHeightForPixelHeight(CAPTION_HEIGHT_PIXELS));

		return pixelGLConverter.getPixelWidthForGLWidth(textWidth) + CAPTION_HEIGHT_PIXELS
				+ SPACING_PIXELS;
	}

	@Override
	public void handleDropAreaReplaced() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCaption() {
		return dataDomain.getLabel();
	}
}
