package org.caleydo.view.datagraph.node;

import java.util.ArrayList;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.view.datagraph.GLDataGraph;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DataContainerListRenderer;
import org.caleydo.view.datagraph.datacontainer.DimensionGroupRenderer;
import org.caleydo.view.datagraph.layout.AGraphLayout;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class VisBricksNode extends ViewNode implements IDropArea {

	protected DataContainerListRenderer dataContainerListRenderer;

	public VisBricksNode(AGraphLayout graphLayout, GLDataGraph view,
			DragAndDropController dragAndDropController, Integer id,
			AGLView representedView) {
		super(graphLayout, view, dragAndDropController, id, representedView);

	}

	@Override
	protected void registerPickingListeners() {

		view.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void dragged(Pick pick) {

				DragAndDropController dragAndDropController = VisBricksNode.this.dragAndDropController;
				if (dragAndDropController.isDragging()
						&& dragAndDropController.getDraggingMode().equals(
								"DimensionGroupDrag")) {
					dragAndDropController.setDropArea(VisBricksNode.this);
				}

			}
		}, PickingType.DATA_GRAPH_NODE.name(), id);

	}

	@Override
	protected ElementLayout setupLayout() {
		ElementLayout baseLayout = super.setupLayout();

		bodyColumn.clear();

		ElementLayout dataContainerLayout = new ElementLayout(
				"datContainerList");

		dataContainerListRenderer = new DataContainerListRenderer(this, view,
				dragAndDropController, getDataContainers());

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
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables,
			float mouseCoordinateX, float mouseCoordinateY,
			DragAndDropController dragAndDropController) {
		ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>();
		for (IDraggable draggable : draggables) {
			if (draggable instanceof DimensionGroupRenderer) {
				DimensionGroupRenderer dimensionGroupRenderer = (DimensionGroupRenderer) draggable;
				dataContainers.add(dimensionGroupRenderer.getDataContainer());
			}
		}

		if (!dataContainers.isEmpty()) {
			// FIXME: this needs to be looked at again
			AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(
					dataContainers.get(0).getDataDomain().getDataDomainID(),
					dataContainers.get(0));
			// event.setDimensionGroupData(dimensionGroupData);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}

		dragAndDropController.clearDraggables();

	}

	@Override
	public void destroy() {
		super.destroy();
		// overviewDataContainerRenderer.destroy();
		view.removeSingleIDPickingListeners(PickingType.DATA_GRAPH_NODE.name(),
				id);
	}

	@Override
	public void update() {
		dataContainerListRenderer.setDataContainers(getDataContainers());

	}

}
