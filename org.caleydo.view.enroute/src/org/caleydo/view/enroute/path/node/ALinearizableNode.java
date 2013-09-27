/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.path.node;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.mode.ALinearizeableNodeMode;

/**
 * Base class for all nodes that can be linearized.
 *
 * @author Christian Partl
 *
 */
public abstract class ALinearizableNode extends ANode implements ILabelProvider {

	/**
	 * The {@link SelectionType} of the node.
	 */
	protected SelectionType selectionType;

	/**
	 * The current mode of the node.
	 */
	protected ALinearizeableNodeMode mode;

	/**
	 * The {@link PathwayVertexRep}s this node belongs to.
	 */
	protected List<PathwayVertexRep> vertexReps = new ArrayList<>(2);

	/**
	 * The {@link ComplexNode} this node belongs to. Null if this node is not part of a complex node.
	 */
	protected ComplexNode parentNode;

	/**
	 * Renderer that uses this node.
	 */
	protected APathwayPathRenderer pathwayPathRenderer;

	/**
	 * @param pixelGLConverter
	 * @param view
	 * @param nodeId
	 */
	public ALinearizableNode(APathwayPathRenderer pathwayPathRenderer, AGLView view, ALinearizeableNodeMode mode) {
		super(view);
		this.pathwayPathRenderer = pathwayPathRenderer;
		this.mode = mode;
		mode.apply(this);
	}

	@Override
	public void render(GL2 gl, GLU glu) {
		mode.render(gl, glu);
	}

	@Override
	public void renderHighlight(GL2 gl, GLU glu) {
		mode.renderHighlight(gl, glu);
	}

	/**
	 * @param mode
	 *            setter, see {@link mode}
	 */
	public void setMode(ALinearizeableNodeMode mode) {
		this.mode.destroy();
		this.mode = mode;
		mode.apply(this);
	}

	/**
	 * @param selectionType
	 *            setter, see {@link #selectionType}
	 */
	public void setSelectionType(SelectionType selectionType) {
		this.selectionType = selectionType;
	}

	/**
	 * @return the selectionType, see {@link #selectionType}
	 */
	public SelectionType getSelectionType() {
		return selectionType;
	}

	@Override
	public int getHeightPixels() {
		return mode.getMinHeightPixels();
	}

	@Override
	public int getWidthPixels() {
		return mode.getMinWidthPixels();
	}

	/**
	 * Adds a {@link PathwayVertexRep} to this node. Note that only equivalent vertexReps are allowed to be added.
	 *
	 * @param pathwayVertexRep
	 */
	public void addPathwayVertexRep(PathwayVertexRep pathwayVertexRep) {
		// PathwayVertexRep primaryVertexRep = getPrimaryPathwayVertexRep();
		// if (primaryVertexRep != null) {
		// if (primaryVertexRep.getDavidIDs().size() != pathwayVertexRep.getDavidIDs().size()
		// || !primaryVertexRep.getDavidIDs().containsAll(pathwayVertexRep.getDavidIDs())) {
		// throw new IllegalArgumentException("Tried to add a vertex rep to node " + this
		// + " that is not equivalent to existing ones.");
		// }
		// }
		vertexReps.add(pathwayVertexRep);
		mode.apply(this);
	}

	/**
	 * Gets the first vertexRep this node represents.
	 *
	 * @return
	 */
	public PathwayVertexRep getPrimaryPathwayVertexRep() {
		if (vertexReps.size() > 0)
			return vertexReps.get(0);
		return null;
	}

	/**
	 * @param parentNode
	 *            setter, see {@link #parentNode}
	 */
	public void setParentNode(ComplexNode parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * @return the parentNode, see {@link #parentNode}
	 */
	public ComplexNode getParentNode() {
		return parentNode;
	}

	@Override
	public void init() {
		view.addIDPickingTooltipListener(this, EPickingType.LINEARIZABLE_NODE.name(), hashCode());
	}

	@Override
	public void destroy() {
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(), hashCode());
		mode.destroy();
	}

	/**
	 * @return List of all davidIDs this node maps to.
	 */
	public List<Integer> getMappedDavidIDs() {

		// The DavidIDs must be the same for all vertex reps
		Set<Integer> uniqueIDs = new LinkedHashSet<>();
		for (PathwayVertexRep vertexRep : vertexReps) {
			uniqueIDs.addAll(vertexRep.getDavidIDs());
		}

		return new ArrayList<>(uniqueIDs);
	}

	public void update() {
		destroy();
		init();
		mode.apply(this);
	}

	/**
	 * @return the vertexReps, see {@link #vertexReps}
	 */
	public List<PathwayVertexRep> getVertexReps() {
		return vertexReps;
	}

	public void addPickingListener(IPickingListener listener) {
		view.addIDPickingListener(listener, EPickingType.LINEARIZABLE_NODE.name(), hashCode());
	}

}
