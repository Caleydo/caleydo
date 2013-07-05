/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute.path;

import java.util.List;

import org.caleydo.view.enroute.path.node.ANode;

/**
 * Class that describes different properties of the spacing between two anchor
 * nodes, i.e. nodes that have associated data rows.
 *
 * @author Christian
 *
 */
public class AnchorNodeSpacing {

	/**
	 * First anchor node.
	 */
	private ANode startNode;
	/**
	 * Second anchor node.
	 */
	private ANode endNode;
	/**
	 * Nodes that are inbetween the anchor nodes.
	 */
	private List<ANode> nodesInbetween;
	/**
	 * The height of all nodes that are within the spacing. This also includes
	 * the halves of the anchor node heigths.
	 */
	private float totalNodeHeight;

	/**
	 * Current spacing between the anchor nodes.
	 */
	private float currentAnchorNodeSpacing;

	/**
	 * @param startNode
	 *            setter, see {@link #startNode}
	 */
	public void setStartNode(ANode startNode) {
		this.startNode = startNode;
	}

	/**
	 * @return the startNode, see {@link #startNode}
	 */
	public ANode getStartNode() {
		return startNode;
	}

	/**
	 * @param endNode
	 *            setter, see {@link #endNode}
	 */
	public void setEndNode(ANode endNode) {
		this.endNode = endNode;
	}

	/**
	 * @return the endNode, see {@link #endNode}
	 */
	public ANode getEndNode() {
		return endNode;
	}

	/**
	 * @param nodesInbetween
	 *            setter, see {@link #nodesInbetween}
	 */
	public void setNodesInbetween(List<ANode> nodesInbetween) {
		this.nodesInbetween = nodesInbetween;
	}

	/**
	 * @return the nodesInbetween, see {@link #nodesInbetween}
	 */
	public List<ANode> getNodesInbetween() {
		return nodesInbetween;
	}

	/**
	 * Calculates {@link #totalNodeHeight}.
	 */
	public void calcTotalNodeHeight() {
		totalNodeHeight = 0;

		if (startNode != null) {
			totalNodeHeight += startNode.getHeight() / 2.0f;
		}
		if (endNode != null) {
			totalNodeHeight += endNode.getHeight() / 2.0f;
		}

		for (ANode node : nodesInbetween) {
			totalNodeHeight += node.getHeight();
		}
	}

	/**
	 * @return the totalNodeHeight, see {@link #totalNodeHeight}
	 */
	public float getTotalNodeHeight() {
		return totalNodeHeight;
	}

	/**
	 * @param currentAnchorNodeSpacing
	 *            setter, see {@link #currentAnchorNodeSpacing}
	 */
	public void setCurrentAnchorNodeSpacing(float currentAnchorNodeSpacing) {
		this.currentAnchorNodeSpacing = currentAnchorNodeSpacing;
	}

	/**
	 * @return the currentAnchorNodeSpacing, see
	 *         {@link #currentAnchorNodeSpacing}
	 */
	public float getCurrentAnchorNodeSpacing() {
		return currentAnchorNodeSpacing;
	}

}
