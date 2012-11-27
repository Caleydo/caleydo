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
package org.caleydo.view.enroute.node;

import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.PixelGLConverter;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.enroute.node.mode.ComplexNodeLinearizedMode;
import org.caleydo.view.enroute.node.mode.IComplexNodeMode;

/**
 * The complex node renderer renders a node that represents multiple
 * {@link PathwayVertexRep} objects.
 *
 * @author Christian
 *
 */
public class ComplexNode extends ALinearizableNode {

	public static final int TEXT_SPACING_PIXELS = 3;

	/**
	 * List of {@link ANode} objects that belong to this complex node.
	 */
	private List<ALinearizableNode> nodes = new ArrayList<ALinearizableNode>();

	/**
	 * @param pixelGLConverter
	 */
	public ComplexNode(PixelGLConverter pixelGLConverter,
			CaleydoTextRenderer textRenderer, GLEnRoutePathway view, int nodeId) {
		super(pixelGLConverter, view, nodeId);
	}

	@Override
	protected ALinearizeableNodeMode getLinearizedMode() {
		return new ComplexNodeLinearizedMode(view);
	}

	@Override
	protected ALinearizeableNodeMode getPreviewMode() {
		// FIXME: just temporary
		return new ComplexNodeLinearizedMode(view);
	}

	@Override
	public String getLabel() {
		return "Complex";
	}

	/**
	 * @param nodes
	 *            setter, see {@link #nodes}
	 */
	public void setNodes(List<ALinearizableNode> nodes) {
		this.nodes = nodes;
		mode.apply(this);
	}

	/**
	 * @return the nodes, see {@link #nodes}
	 */
	public List<ALinearizableNode> getNodes() {
		return nodes;
	}

	@Override
	public void setPreviewMode(boolean isPreviewMode) {
		super.setPreviewMode(isPreviewMode);

		for (ALinearizableNode node : nodes) {
			node.setPreviewMode(isPreviewMode);
		}
	}

	@Override
	public void unregisterPickingListeners() {
		super.unregisterPickingListeners();
		for (ALinearizableNode node : nodes) {
			node.unregisterPickingListeners();
		}
	}

	@Override
	public String getProviderName() {
		return "Complex Node";
	}

	@Override
	public void setPosition(Vec3f position) {
		super.setPosition(position);
		((IComplexNodeMode) mode).updateSubNodePositions();
	}

}
