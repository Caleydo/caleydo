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
package org.caleydo.view.enroute;

import java.util.List;

import org.caleydo.view.enroute.node.ANode;

/**
 * Class that describes different properties of the spacing between two anchor
 * nodes, i.e. nodes that have associated data rows.
 * 
 * @author Christian
 * 
 */
class AnchorNodeSpacing {

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
