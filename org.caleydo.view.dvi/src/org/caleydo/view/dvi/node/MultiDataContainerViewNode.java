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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.IDataContainerBasedView;
import org.caleydo.core.view.listener.AddDataContainersEvent;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.contextmenu.OpenViewItem;
import org.caleydo.view.dvi.datacontainer.ADataContainerRenderer;
import org.caleydo.view.dvi.datacontainer.DataContainerListRenderer;
import org.caleydo.view.dvi.datacontainer.DimensionGroupRenderer;
import org.caleydo.view.dvi.layout.AGraphLayout;

public class MultiDataContainerViewNode extends ViewNode implements IDropArea {

	protected DataContainerListRenderer dataContainerListRenderer;
	protected List<DataContainer> dataContainers;

	public MultiDataContainerViewNode(AGraphLayout graphLayout,
			GLDataViewIntegrator view, DragAndDropController dragAndDropController,
			Integer id, AGLView representedView) {
		super(graphLayout, view, dragAndDropController, id, representedView);

	}

	@Override
	protected void registerPickingListeners() {

		view.addIDPickingListener(new APickingListener() {

			@Override
			public void dragged(Pick pick) {

				DragAndDropController dragAndDropController = MultiDataContainerViewNode.this.dragAndDropController;
				if (dragAndDropController.isDragging()
						&& dragAndDropController.getDraggingMode().equals(
								"DimensionGroupDrag")) {
					dragAndDropController.setDropArea(MultiDataContainerViewNode.this);
				}

			}
		}, DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);

		view.addIDPickingListener(new APickingListener() {

			@Override
			public void rightClicked(Pick pick) {
				view.getContextMenuCreator().addContextMenuItem(
						new OpenViewItem(representedView));
			}

			@Override
			public void doubleClicked(Pick pick) {
				view.openView(representedView);
			}

		}, DATA_GRAPH_NODE_PICKING_TYPE, id);

	}

	@Override
	protected ElementLayout setupLayout() {
		ElementLayout baseLayout = super.setupLayout();

		bodyColumn.clear();

		ElementLayout dataContainerLayout = new ElementLayout("datContainerList");

		dataContainerListRenderer = new DataContainerListRenderer(this, view,
				dragAndDropController, getDataContainers());

		List<Pair<String, Integer>> pickingIDsToBePushed = new ArrayList<Pair<String, Integer>>();
		pickingIDsToBePushed.add(new Pair<String, Integer>(
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id));

		dataContainerListRenderer.setPickingIDsToBePushed(pickingIDsToBePushed);

		dataContainerLayout.setRatioSizeY(1);
		dataContainerLayout.setRenderer(dataContainerListRenderer);

		ElementLayout spacingLayoutY = createDefaultSpacingY();

		bodyColumn.append(dataContainerLayout);
		bodyColumn.append(spacingLayoutY);

		return baseLayout;
	}

	@Override
	protected ADataContainerRenderer getDataContainerRenderer() {
		return dataContainerListRenderer;
	}

	@Override
	public boolean showsDataContainers() {
		return true;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY) {

	}

	@Override
	public List<DataContainer> getDataContainers() {

		if (dataContainers == null) {
			retrieveDataContainers();
		}

		return dataContainers;
	}

	protected void retrieveDataContainers() {

		dataContainers = new ArrayList<DataContainer>(
				((IDataContainerBasedView) representedView).getDataContainers());
		if (dataContainers == null) {
			dataContainers = new ArrayList<DataContainer>();
			return;
		}

		Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();

		for (DataContainer container : dataContainers) {
			if (container instanceof PathwayDataContainer) {
				dataDomains
						.add(((PathwayDataContainer) container).getPathwayDataDomain());
			} else {
				dataDomains.add(container.getDataDomain());
			}
		}

		sortDataContainers();

	}

	private void sortDataContainers() {
		List<DataContainer> containers = new ArrayList<DataContainer>(dataContainers);

		List<Pair<Float, ADataNode>> sortedDataNodes = new ArrayList<Pair<Float, ADataNode>>();

		for (IDataDomain dataDomain : dataDomains) {
			ADataNode dataNode = view.getDataNode(dataDomain);
			if (dataNode != null) {
				sortedDataNodes.add(new Pair<Float, ADataNode>((float) dataNode
						.getPosition().getX(), dataNode));
			}
		}

		Collections.sort(sortedDataNodes);
		dataContainers.clear();

		for (Pair<Float, ADataNode> dataNodePair : sortedDataNodes) {
			ADataNode dataNode = dataNodePair.getSecond();

			List<DataContainer> sortedNodeDataContainers = dataNode.getDataContainers();

			for (DataContainer nodeContainer : sortedNodeDataContainers) {
				DataContainer addedContainer = null;
				for (DataContainer container : containers) {
					if (nodeContainer == container) {
						dataContainers.add(container);
						addedContainer = container;
						break;
					}
				}
				if (addedContainer != null)
					containers.remove(addedContainer);
			}
		}
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {
		ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>();
		for (IDraggable draggable : draggables) {
			if (draggable instanceof DimensionGroupRenderer) {
				DimensionGroupRenderer dimensionGroupRenderer = (DimensionGroupRenderer) draggable;
				dataContainers.add(dimensionGroupRenderer.getDataContainer());
			}
		}

		if (!dataContainers.isEmpty()) {
			// FIXME: this needs to be looked at again
			// System.out.println("Drop");
			DataContainer dataContainer = dataContainers.get(0);
			AddDataContainersEvent event = new AddDataContainersEvent(dataContainer);
			event.setReceiver(representedView);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);

			if (dataContainer instanceof PathwayDataContainer) {
				dataDomains.add(((PathwayDataContainer) dataContainer)
						.getPathwayDataDomain());
			} else {
				dataDomains.add(dataContainer.getDataDomain());
			}
			getDataContainers().add(dataContainer);
			sortDataContainers();
			dataContainerListRenderer.setDataContainers(getDataContainers());
			view.updateGraphEdgesOfViewNode(this);
			recalculateNodeSize();
			graphLayout.fitNodesToDrawingArea(view.calculateGraphDrawingArea());
			view.setDisplayListDirty();
		}

		// dragAndDropController.clearDraggables();

	}

	@Override
	public void destroy() {
		super.destroy();
		// overviewDataContainerRenderer.destroy();
		dataContainerListRenderer.destroy();
		view.removeAllIDPickingListeners(DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
	}

	@Override
	public void update() {
		dataDomains = representedView.getDataDomains();
		retrieveDataContainers();
		dataContainerListRenderer.setDataContainers(getDataContainers());
		recalculateNodeSize();
		graphLayout.fitNodesToDrawingArea(view.calculateGraphDrawingArea());
		view.setDisplayListDirty();
	}

	@Override
	public void render(GL2 gl) {
		// retrieveDataContainers();
		// dataContainerListRenderer.setDataContainers(getDataContainers());
		super.render(gl);
	}

	@Override
	public void handleDropAreaReplaced() {
		// TODO Auto-generated method stub

	}

}
