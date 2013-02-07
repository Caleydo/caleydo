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

	// /**
	// * Determines whether the node shows a preview of its data.
	// */
	// protected boolean isPreviewMode = false;

	// /**
	// * Determines whether the button to remove the
	// */
	// protected boolean showRemoveButton = false;

	/**
	 * The {@link SelectionType} of the node.
	 */
	protected SelectionType selectionType;

	/**
	 * The current mode of the node.
	 */
	protected ALinearizeableNodeMode mode;

	/**
	 * The {@link PathwayVertexRep} in the graph this node belongs to. This can either be a direct relationship, or the
	 * vertex can contain multiple genes.
	 */
	protected PathwayVertexRep pathwayVertexRep;

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
	public ALinearizableNode(APathwayPathRenderer pathwayPathRenderer, AGLView view, int nodeId,
			ALinearizeableNodeMode mode) {
		super(view, nodeId);
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
		this.mode.unregisterPickingListeners();
		this.mode = mode;
		mode.apply(this);
	}

	// /**
	// * @param isPreviewMode
	// * setter, see {@link #isPreviewMode}
	// */
	// public void setPreviewMode(boolean isPreviewMode) {
	//
	// if (this.isPreviewMode == isPreviewMode)
	// return;
	// this.isPreviewMode = isPreviewMode;
	// mode.unregisterPickingListeners();
	//
	// if (isPreviewMode) {
	// mode = getPreviewMode();
	// } else {
	// mode = getLinearizedMode();
	// }
	// mode.apply(this);
	// }

	// /**
	// * @return the isPreviewMode, see {@link #isPreviewMode}
	// */
	// public boolean isPreviewMode() {
	// return isPreviewMode;
	// }

	// /**
	// * @return A new linearized mode object for the concrete node.
	// */
	// protected abstract ALinearizeableNodeMode getLinearizedMode();
	//
	// /**
	// * @return A new preview mode object for the concrete node.
	// */
	// protected abstract ALinearizeableNodeMode getPreviewMode();

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
	 * @param pathwayVertexRep
	 *            setter, see {@link #pathwayVertexRep}
	 */
	public void setPathwayVertexRep(PathwayVertexRep pathwayVertexRep) {
		this.pathwayVertexRep = pathwayVertexRep;
	}

	/**
	 * @return the pathwayVertexRep, see {@link #pathwayVertexRep}
	 */
	public PathwayVertexRep getPathwayVertexRep() {
		return pathwayVertexRep;
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
		view.addIDPickingTooltipListener(this, EPickingType.LINEARIZABLE_NODE.name(), nodeId);
	}

	@Override
	public void destroy() {
		view.removeAllIDPickingListeners(EPickingType.LINEARIZABLE_NODE.name(), nodeId);
		mode.unregisterPickingListeners();
	}

	/**
	 * @return List of all davidIDs this node maps to.
	 */
	public List<Integer> getMappedDavidIDs() {
		return new ArrayList<>(pathwayVertexRep.getDavidIDs());
	}

	public void update() {
		destroy();
		init();
		mode.apply(this);
	}

}
