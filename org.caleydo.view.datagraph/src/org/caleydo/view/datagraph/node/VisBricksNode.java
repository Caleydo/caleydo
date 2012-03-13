package org.caleydo.view.datagraph.node;

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
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;
import org.caleydo.datadomain.pathway.data.PathwayDataContainer;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.contextmenu.OpenViewItem;
import org.caleydo.view.datagraph.datacontainer.ADataContainerRenderer;
import org.caleydo.view.datagraph.datacontainer.DataContainerListRenderer;
import org.caleydo.view.datagraph.datacontainer.DimensionGroupRenderer;
import org.caleydo.view.datagraph.layout.AGraphLayout;
import org.caleydo.view.visbricks.GLVisBricks;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class VisBricksNode
	extends ViewNode
	implements IDropArea
{

	protected DataContainerListRenderer dataContainerListRenderer;
	protected List<DataContainer> dataContainers;

	public VisBricksNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			DragAndDropController dragAndDropController, Integer id, AGLView representedView)
	{
		super(graphLayout, view, dragAndDropController, id, representedView);

	}

	@Override
	protected void registerPickingListeners()
	{

		view.addIDPickingListener(new APickingListener()
		{

			@Override
			public void dragged(Pick pick)
			{

				DragAndDropController dragAndDropController = VisBricksNode.this.dragAndDropController;
				if (dragAndDropController.isDragging()
						&& dragAndDropController.getDraggingMode()
								.equals("DimensionGroupDrag"))
				{
					dragAndDropController.setDropArea(VisBricksNode.this);
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
	protected ElementLayout setupLayout()
	{
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
	protected ADataContainerRenderer getDataContainerRenderer()
	{
		return dataContainerListRenderer;
	}

	@Override
	public boolean showsDataContainers()
	{
		return true;
	}

	@Override
	public void handleDragOver(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataContainer> getDataContainers()
	{

		if (dataContainers == null)
		{
			retrieveDataContainers();
		}
		
//		update();
		
//		retrieveDataContainers();
//		dataContainerListRenderer.setDataContainers(dataContainers);

		// List<ADimensionGroupData> groups = representedView.get();
		// if (groups == null) {
		return dataContainers;
		// }
		// return new ArrayList<DataContainer>(groups);
	}

	protected void retrieveDataContainers()
	{
		dataContainers = new ArrayList<DataContainer>();

		List<DataContainer> containers = ((IDataContainerBasedView) representedView)
				.getDataContainers();

		Set<IDataDomain> dataDomains = new HashSet<IDataDomain>();

		for (DataContainer container : containers)
		{
			if (container instanceof PathwayDataContainer)
			{
				dataDomains.add(((PathwayDataContainer) container).getPathwayDataDomain());
			}
			else
			{
				dataDomains.add(container.getDataDomain());
			}
		}

		List<Pair<Float, ADataNode>> sortedDataNodes = new ArrayList<Pair<Float, ADataNode>>();

		for (IDataDomain dataDomain : dataDomains)
		{
			ADataNode dataNode = view.getDataNode(dataDomain);
			if (dataNode != null)
			{
				sortedDataNodes.add(new Pair<Float, ADataNode>((float) dataNode.getPosition()
						.getX(), dataNode));
			}
		}

		Collections.sort(sortedDataNodes);

		for (Pair<Float, ADataNode> dataNodePair : sortedDataNodes)
		{
			ADataNode dataNode = dataNodePair.getSecond();

			List<DataContainer> sortedNodeDataContainers = dataNode.getDataContainers();

			for (DataContainer nodeContainer : sortedNodeDataContainers)
			{
				for (DataContainer container : containers)
				{
					if (nodeContainer == container)
					{
						dataContainers.add(container);
						break;
					}
				}
			}
		}
	}

	@Override
	public void handleDrop(GL2 gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController)
	{
		ArrayList<DataContainer> dataContainers = new ArrayList<DataContainer>();
		for (IDraggable draggable : draggables)
		{
			if (draggable instanceof DimensionGroupRenderer)
			{
				DimensionGroupRenderer dimensionGroupRenderer = (DimensionGroupRenderer) draggable;
				dataContainers.add(dimensionGroupRenderer.getDataContainer());
			}
		}

		if (!dataContainers.isEmpty())
		{
			// FIXME: this needs to be looked at again
			System.out.println("Drop");
			AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent(
					dataContainers.get(0));
			event.setReceiver((GLVisBricks) representedView);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}

		// dragAndDropController.clearDraggables();

	}

	@Override
	public void destroy()
	{
		super.destroy();
		// overviewDataContainerRenderer.destroy();
		view.removeAllIDPickingListeners(DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
	}

	@Override
	public void update()
	{
		retrieveDataContainers();
		dataContainerListRenderer.setDataContainers(getDataContainers());		
		recalculateNodeSize();
		graphLayout.updateNodePositions();
		view.setDisplayListDirty();
	}
	
	@Override
	public void render(GL2 gl) {
		retrieveDataContainers();
		dataContainerListRenderer.setDataContainers(getDataContainers());
		super.render(gl);
	}

	@Override
	public void handleDropAreaReplaced()
	{
		// TODO Auto-generated method stub
		
	}

}
