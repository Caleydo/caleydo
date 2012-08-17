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

import java.awt.geom.Point2D;
import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.Graph;
import org.caleydo.view.dvi.layout.AGraphLayout;
import org.caleydo.view.dvi.layout.edge.rendering.AEdgeRenderer;

public abstract class ADraggableDataGraphNode implements IDVINode {

	protected final static String DATA_GRAPH_NODE_PICKING_TYPE = "org.caleydo.view.dvi.node";
	protected final static String DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE = "org.caleydo.view.dvi.node_penetrating";

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
			final DragAndDropController dragAndDropController, int id) {
		this.graphLayout = graphLayout;
		this.view = view;
		this.pixelGLConverter = view.getPixelGLConverter();
		this.id = id;

		this.dragAndDropController = dragAndDropController;
	}

	@Override
	public void init() {
		registerPickingListeners();
	}

	/**
	 * Concrete subclasses that need to register their own
	 * {@link IPickingListener} objects, are supposed to override this method.
	 * Subclasses must call the super implementation of this method in order to
	 * register their picking listeners.
	 */
	protected void registerPickingListeners() {
		pickingListener = new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingProperties(pick.getPickedPoint(),
						"NodeDrag");
				dragAndDropController.addDraggable(ADraggableDataGraphNode.this);
			}
		};

		pickingListenerPenetrating = new APickingListener() {

			@Override
			public void mouseOver(Pick pick) {
				if (!dragAndDropController.isDragging()) {
					view.setCurrentMouseOverNode(ADraggableDataGraphNode.this);
					view.setDisplayListDirty();
				}
			}

			@Override
			public void mouseOut(Pick pick) {
				if (view.getCurrentMouseOverNode() == ADraggableDataGraphNode.this
						&& !dragAndDropController.isDragging()) {
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
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
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

		if (!isCustomPosition) {
			Graph graph = graphLayout.getGraph();

			Set<Edge> edges = graph.getEdgesOfNode(this);

			if (edges != null) {
				for (Edge edge : edges) {
					AEdgeRenderer edgeRenderer = graphLayout
							.getCustomLayoutEdgeRenderer(edge);
					edge.setEdgeRenderer(edgeRenderer);
				}
			}
		}

		isCustomPosition = true;
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {
		dragAndDropController.clearDraggables();
		view.updateMinWindowSize(true);
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void destroy() {
		view.removeIDPickingListener(pickingListener, DATA_GRAPH_NODE_PICKING_TYPE, id);
		view.removeIDPickingListener(pickingListenerPenetrating,
				DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
	}

	@Override
	public boolean isCustomPosition() {
		return isCustomPosition;
	}

	@Override
	public void setCustomPosition(boolean isCustomPosition) {
		this.isCustomPosition = isCustomPosition;
	}

	@Override
	public void setGraphLayout(AGraphLayout graphLayout) {
		this.graphLayout = graphLayout;
	}

}
