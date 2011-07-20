package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.picking.APickingListener;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;

public abstract class ADraggableDataGraphNode implements IDataGraphNode {

	protected ForceDirectedGraphLayout graphLayout;
	protected GLDataGraph view;
	protected PixelGLConverter pixelGLConverter;
	protected int id;
	protected DragAndDropController dragAndDropController;

	private float prevDraggingMouseX;
	private float prevDraggingMouseY;

	public ADraggableDataGraphNode(ForceDirectedGraphLayout graphLayout,
			GLDataGraph view,
			final DragAndDropController dragAndDropController, int id) {
		this.graphLayout = graphLayout;
		this.view = view;
		this.pixelGLConverter = view.getPixelGLConverter();
		this.id = id;

		this.dragAndDropController = dragAndDropController;

		createPickingListener();
	}

	private void createPickingListener() {
		view.addSingleIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingStartPosition(pick
						.getPickedPoint());
				dragAndDropController
						.addDraggable(ADraggableDataGraphNode.this);
			}

			@Override
			public void mouseOver(Pick pick) {
			}

			@Override
			public void dragged(Pick pick) {
				if (!dragAndDropController.isDragging()) {
					dragAndDropController.startDragging();
				}
			}

		}, EPickingType.DATA_GRAPH_NODE.name(), id);
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX,
			float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		if ((prevDraggingMouseX >= mouseCoordinateX - 0.01 && prevDraggingMouseX <= mouseCoordinateX + 0.01)
				&& (prevDraggingMouseY >= mouseCoordinateY - 0.01 && prevDraggingMouseY <= mouseCoordinateY + 0.01))
			return;

		float mouseDeltaX = prevDraggingMouseX - mouseCoordinateX;
		float mouseDeltaY = prevDraggingMouseY - mouseCoordinateY;
		int mouseDeltaXPixels = pixelGLConverter
				.getPixelWidthForGLWidth(mouseDeltaX);
		int mouseDeltaYPixels = pixelGLConverter
				.getPixelHeightForGLHeight(mouseDeltaY);

		Point2D position = graphLayout.getNodePosition(this, true);

		position.setLocation(position.getX() - mouseDeltaXPixels,
				position.getY() - mouseDeltaYPixels);

		graphLayout.setNodePosition(this, position);

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

		view.setDisplayListDirty();
		view.setApplyAutomaticLayout(false);

	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX,
			float mouseCoordinateY) {
		dragAndDropController.clearDraggables();
	}

	@Override
	public int getID() {
		return id;
	}

}
