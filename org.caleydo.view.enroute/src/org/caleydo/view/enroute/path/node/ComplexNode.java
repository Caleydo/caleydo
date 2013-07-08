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

import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.enroute.path.APathwayPathRenderer;
import org.caleydo.view.enroute.path.node.mode.ALinearizeableNodeMode;
import org.caleydo.view.enroute.path.node.mode.IComplexNodeMode;

/**
 * The complex node renderer renders a node that represents multiple {@link PathwayVertexRep} objects.
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
	public ComplexNode(APathwayPathRenderer pathwayPathRenderer, CaleydoTextRenderer textRenderer, AGLView view,
			ALinearizeableNodeMode mode) {
		super(pathwayPathRenderer, view, mode);
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
	public void setMode(ALinearizeableNodeMode mode) {
		super.setMode(mode);
		// for (ALinearizableNode node : nodes) {
		// node.setMode(mode);
		// }
	}

	@Override
	public void destroy() {
		super.destroy();
		for (ALinearizableNode node : nodes) {
			node.destroy();
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

	@Override
	public List<Integer> getMappedDavidIDs() {
		List<Integer> davidIDs = new ArrayList<>();
		for (ALinearizableNode node : nodes) {
			davidIDs.addAll(node.getMappedDavidIDs());
		}
		return davidIDs;
	}

}
