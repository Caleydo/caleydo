package org.caleydo.view.datagraph.node;

import java.awt.geom.Point2D;
import java.util.Set;
import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.datagraph.Edge;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.Graph;
import org.caleydo.view.datagraph.layout.AGraphLayout;
import org.caleydo.view.datagraph.layout.edge.rendering.AEdgeRenderer;

public abstract class ADraggableDataGraphNode
	implements IDVINode
{

	protected final static String DATA_GRAPH_NODE_PICKING_TYPE = "org.caleydo.view.datagraph.node";
	protected final static String DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE = "org.caleydo.view.datagraph.node_penetrating";

	protected AGraphLayout graphLayout;
	protected GLDataViewIntegrator view;
	protected PixelGLConverter pixelGLConverter;
	protected int id;
	protected DragAndDropController dragAndDropController;
	protected boolean isCustomPosition = false;
	private IPickingListener pickingListener;
	private IPickingListener pickingListenerPenetrating;
	private float prevDraggingMouseX;
	private float prevDraggingMouseY;

	public ADraggableDataGraphNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			final DragAndDropController dragAndDropController, int id)
	{
		this.graphLayout = graphLayout;
		this.view = view;
		this.pixelGLConverter = view.getPixelGLConverter();
		this.id = id;

		this.dragAndDropController = dragAndDropController;

		createPickingListeners();
	}

	protected void createPickingListeners()
	{
		pickingListener = new APickingListener()
		{

			@Override
			public void clicked(Pick pick)
			{
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick.getPickedPoint());
				dragAndDropController.addDraggable(ADraggableDataGraphNode.this);
				dragAndDropController.setDraggingMode("NodeDrag");
			}

			@Override
			public void dragged(Pick pick)
			{
				String draggingMode = dragAndDropController.getDraggingMode();
				if (!dragAndDropController.isDragging()
						&& dragAndDropController.hasDraggables() && draggingMode != null
						&& draggingMode.equals("NodeDrag"))
				{
					dragAndDropController.startDragging();
				}
			}

		};

		pickingListenerPenetrating = new APickingListener()
		{

			@Override
			public void mouseOver(Pick pick)
			{
				if (!dragAndDropController.isDragging())
				{
					view.setCurrentMouseOverNode(ADraggableDataGraphNode.this);
					view.setDisplayListDirty();
				}
			}

			@Override
			public void mouseOut(Pick pick)
			{
				if (view.getCurrentMouseOverNode() == ADraggableDataGraphNode.this
						&& !dragAndDropController.isDragging())
				{
					view.setCurrentMouseOverNode(null);
					view.setDisplayListDirty();
				}
			}

		};

		view.addIDPickingListener(pickingListener, DATA_GRAPH_NODE_PICKING_TYPE, id);
		view.addIDPickingListener(pickingListenerPenetrating,
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY)
	{
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY)
	{
		if ((prevDraggingMouseX >= mouseCoordinateX - 0.01 && prevDraggingMouseX <= mouseCoordinateX + 0.01)
				&& (prevDraggingMouseY >= mouseCoordinateY - 0.01 && prevDraggingMouseY <= mouseCoordinateY + 0.01))
			return;

		float mouseDeltaX = prevDraggingMouseX - mouseCoordinateX;
		float mouseDeltaY = prevDraggingMouseY - mouseCoordinateY;
		int mouseDeltaXPixels = pixelGLConverter.getPixelWidthForGLWidth(mouseDeltaX);
		int mouseDeltaYPixels = pixelGLConverter.getPixelHeightForGLHeight(mouseDeltaY);

		Point2D position = graphLayout.getNodePosition(this);

		position.setLocation(position.getX() - mouseDeltaXPixels, position.getY()
				- mouseDeltaYPixels);

		graphLayout.setNodePosition(this, position);

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

		view.setDisplayListDirty();
		view.setApplyAutomaticLayout(false);

		if (!isCustomPosition)
		{
			Graph graph = graphLayout.getGraph();

			Set<Edge> edges = graph.getEdgesOfNode(this);

			if (edges != null)
			{
				for (Edge edge : edges)
				{
					AEdgeRenderer edgeRenderer = graphLayout.getCustomLayoutEdgeRenderer(edge);
					edge.setEdgeRenderer(edgeRenderer);
				}
			}
		}

		isCustomPosition = true;
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY)
	{
		dragAndDropController.clearDraggables();
		view.updateMinWindowSize(true);
	}

	@Override
	public int getID()
	{
		return id;
	}

	@Override
	public void destroy()
	{
		view.removeIDPickingListener(pickingListener, DATA_GRAPH_NODE_PICKING_TYPE, id);
		view.removeIDPickingListener(pickingListenerPenetrating,
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
	}

	@Override
	public boolean isCustomPosition()
	{
		return isCustomPosition;
	}

	@Override
	public void setCustomPosition(boolean isCustomPosition)
	{
		this.isCustomPosition = isCustomPosition;
	}

	@Override
	public void setGraphLayout(AGraphLayout graphLayout)
	{
		this.graphLayout = graphLayout;
	}

}
