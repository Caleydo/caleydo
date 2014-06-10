/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.path.node;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.ATimedMouseOutPickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.RemoveEnRouteNodeEvent;

/**
 * Renderer for a button to remove a node from the linearized pathway.
 *
 * @author Christian
 *
 */
public class RemoveNodeButtonAttributeRenderer extends ANodeAttributeRenderer {

	protected static final int REMOVE_BUTTON_WIDTH_PIXELS = 16;

	/**
	 * Determines whether the button to remove the associated node from the linearized pathway shall be shown.
	 */
	protected boolean showRemoveButton = false;

	/**
	 * Picking listener that triggers the node removal.
	 */
	private IPickingListener buttonPickingListener;

	/**
	 * Picking listener that shows and hides the node when hovering over a node.
	 */
	private IPickingListener showButtonPickingListener;

	/**
	 * List of nodeIds that shall be used for picking.
	 */
	private List<Integer> nodeIds = new ArrayList<Integer>();

	/**
	 * @param view
	 * @param node
	 */
	public RemoveNodeButtonAttributeRenderer(AGLView view, ANode node, APathwayPathRenderer pathwayPathRenderer) {
		super(view, node, pathwayPathRenderer);
	}

	@Override
	public void render(GL2 gl) {
		if (showRemoveButton) {

			// gl.glPushAttrib(GL2.GL_COLOR);
			for (Integer nodeId : nodeIds) {
				gl.glPushName(pickingManager.getPickingID(view.getID(), EPickingType.REMOVABLE_NODE.name(), nodeId));
				gl.glPushName(pickingManager.getPickingID(view.getID(), EPickingType.REMOVE_NODE_BUTTON.name(), nodeId));
			}
			// ALinearizableNode currentNode =
			Vec3f position = node.getPosition();
			float nodeHeight = pixelGLConverter.getGLHeightForPixelHeight(node.getHeightPixels());
			float nodeWidth = pixelGLConverter.getGLWidthForPixelWidth(node.getWidthPixels());
			float buttonWidth = pixelGLConverter.getGLWidthForPixelWidth(REMOVE_BUTTON_WIDTH_PIXELS);

			Vec3f lowerLeftCorner = new Vec3f(position.x() + nodeWidth / 2.0f, position.y() + nodeHeight / 2.0f,
					position.z());
			Vec3f lowerRightCorner = new Vec3f(lowerLeftCorner.x() + buttonWidth, lowerLeftCorner.y(), position.z());
			Vec3f upperRightCorner = new Vec3f(lowerRightCorner.x(), lowerRightCorner.y() + buttonWidth, position.z());
			Vec3f upperLeftCorner = new Vec3f(lowerLeftCorner.x(), upperRightCorner.y(), position.z());

			textureManager.renderTexture(gl, EIconTextures.REMOVE, lowerLeftCorner, lowerRightCorner, upperRightCorner,
					upperLeftCorner);

			for (int i = 0; i < nodeIds.size(); i++) {
				gl.glPopName();
				gl.glPopName();
			}

			// gl.glPopAttrib();

		}

	}

	@Override
	public void registerPickingListeners() {

		for (Integer nodeId : nodeIds) {
			showButtonPickingListener = new ATimedMouseOutPickingListener() {

				@Override
				protected void timedMouseOut(Pick pick) {
					showRemoveButton = false;
					// pathwayPathRenderer.setHighlightDirty(true);
				}

				@Override
				public void mouseOver(Pick pick) {
					if (!node.isPickable())
						return;
					// List<ALinearizableNode> pathNodes = pathwayPathRenderer.getPathNodes();
					// int index = pathNodes.indexOf(node);

					if (pathwayPathRenderer.getUpdateStrategy().isInnerNodeRemovalAllowed()
							|| (pathwayPathRenderer.isFirstNode((ALinearizableNode) node) || pathwayPathRenderer
									.isLastNode((ALinearizableNode) node))) {
						super.mouseOver(pick);
						showRemoveButton = true;
						// pathwayPathRenderer.setHighlightDirty(true);
					}
				}
			};

			view.addIDPickingListener(showButtonPickingListener, EPickingType.LINEARIZABLE_NODE.name(), nodeId);
			view.addIDPickingListener(showButtonPickingListener, EPickingType.REMOVABLE_NODE.name(), nodeId);

			buttonPickingListener = new APickingListener() {

				@Override
				public void clicked(Pick pick) {
					if (!node.isPickable())
						return;
					RemoveEnRouteNodeEvent event = new RemoveEnRouteNodeEvent((ALinearizableNode) node);
					event.setSender(this);
					
					EventPublisher.trigger(event);
					pathwayPathRenderer.removeNodeFromPath((ALinearizableNode) node);
				}

			};

			view.addIDPickingListener(buttonPickingListener, EPickingType.REMOVE_NODE_BUTTON.name(), nodeId);
		}
	}

	@Override
	public void unregisterPickingListeners() {
		for (Integer nodeId : nodeIds) {
			view.removeIDPickingListener(showButtonPickingListener, EPickingType.LINEARIZABLE_NODE.name(), nodeId);
			view.removeIDPickingListener(showButtonPickingListener, EPickingType.REMOVABLE_NODE.name(), nodeId);
			view.removeIDPickingListener(buttonPickingListener, EPickingType.REMOVE_NODE_BUTTON.name(), nodeId);
		}
	}

	/**
	 * @param nodeIds
	 *            setter, see {@link #nodeIds}
	 */
	public void setNodeIds(List<Integer> nodeIds) {
		this.nodeIds = nodeIds;
	}

	/**
	 * Adds a node id to {@link #nodeIds}.
	 *
	 * @param nodeId
	 */
	public void addNodeId(int nodeId) {
		nodeIds.add(nodeId);
	}
}
