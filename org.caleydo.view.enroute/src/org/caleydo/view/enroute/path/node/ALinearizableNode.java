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
/**
 *
 */
package org.caleydo.view.enroute.path.node;

import java.util.ArrayList;
import java.util.List;

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
		PathwayVertexRep primaryVertexRep = getPrimaryPathwayVertexRep();
		if (primaryVertexRep != null) {
			if (primaryVertexRep.getDavidIDs().size() != pathwayVertexRep.getDavidIDs().size()
					|| !primaryVertexRep.getDavidIDs().containsAll(pathwayVertexRep.getDavidIDs())) {
				throw new IllegalArgumentException("Tried to add a vertex rep to node " + this
						+ " that is not equivalent to existing ones.");
			}
		}
		vertexReps.add(pathwayVertexRep);
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
		return new ArrayList<>(getPrimaryPathwayVertexRep().getDavidIDs());
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
