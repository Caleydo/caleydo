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
package org.caleydo.view.dvi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.caleydo.view.dvi.node.IDVINode;

public class Graph {
	Vector<IDVINode> nodes = null;
	Map<IDVINode, Set<Edge>> nodeConnections = null;
	Set<Edge> edges;

	// TODO custom constructor has to be created

	public Graph() {
		nodes = new Vector<IDVINode>();
		nodeConnections = new HashMap<IDVINode, Set<Edge>>();
		edges = new HashSet<Edge>();
	}

	// node sets
	public Collection<IDVINode> getNodes() {
		return nodes;
	}

	public int getNumberOfNodes() {
		if (nodes == null)
			return 0;

		return nodes.size();
	}

	public Set<Edge> getEdgesOfNode(IDVINode node) {
		return nodeConnections.get(node);
	}

	public boolean incident(IDVINode node1, IDVINode node2) {
		if (nodeConnections == null)
			return false;

		Set<Edge> connections = nodeConnections.get(node1);
		if (connections == null)
			return false;

		for (Edge edge : connections) {
			if (edge.getNode1() == node2 || edge.getNode2() == node2) {
				return true;
			}
		}

		return false;
	}

	public boolean hasEdges() {
		if (nodeConnections == null)
			return false;
		if (nodeConnections.size() == 0)
			return false;

		return true;
	}

	public void addNode(IDVINode node) {
		if (nodes.contains(node))
			return;

		nodes.add(node);
	}

	/**
	 * Removes the edge specified by the nodes.
	 * 
	 * @param node1
	 * @param node2
	 * @return True, if the edge was removed, false, if no edge was found.
	 */
	public boolean removeEdge(IDVINode node1, IDVINode node2) {
		if (!nodes.contains(node1))
			return false;
		if (!nodes.contains(node2))
			return false;

		boolean edgeRemoved = false;
		Set<Edge> edgesCopy = new HashSet<Edge>(edges);
		
		for (Edge edge : edgesCopy) {
			if ((edge.getNode1() == node1 && edge.getNode2() == node2)
					|| (edge.getNode1() == node2 && edge.getNode2() == node1)) {
				edges.remove(edge);
				edgeRemoved = true;
			}
		}
		return edgeRemoved;
	}

	public Edge addEdge(IDVINode node1, IDVINode node2) {
		if (!nodes.contains(node1))
			nodes.add(node1);
		if (!nodes.contains(node2))
			nodes.add(node2);

		Edge newEdge = null;

		boolean edgeExists = false;
		for (Edge edge : edges) {
			if ((edge.getNode1() == node1 && edge.getNode2() == node2)
					|| (edge.getNode1() == node2 && edge.getNode2() == node1)) {
				edgeExists = true;
				newEdge = edge;
				break;
			}
		}

		if (!edgeExists) {
			newEdge = new Edge(node1, node2);
			edges.add(newEdge);

			Set<Edge> node1Edges = nodeConnections.get(node1);

			if (node1Edges == null) {
				node1Edges = new HashSet<Edge>();
			}
			node1Edges.add(newEdge);
			nodeConnections.put(node1, node1Edges);

			Set<Edge> node2Edges = nodeConnections.get(node2);

			if (node2Edges == null) {
				node2Edges = new HashSet<Edge>();
			}
			node2Edges.add(newEdge);
			nodeConnections.put(node2, node2Edges);
		}

		return newEdge;
	}

	public Set<Edge> getAllEdges() {
		return edges;
	}

	public void removeNode(IDVINode node) {

		Set<Edge> nodeEdges = nodeConnections.get(node);

		if (nodeEdges != null) {
			for (Edge edge : nodeEdges) {
				IDVINode neighbor = edge.getNode1() == node ? edge.getNode2() : edge
						.getNode1();
				Set<Edge> neighborEdges = nodeConnections.get(neighbor);
				if (neighborEdges != null) {
					neighborEdges.remove(edge);
				}

				edges.remove(edge);
			}
		}

		// Set<Edge> edgesToRemove = new HashSet<Edge>();
		//
		// for (Edge edge : edges) {
		// if (edge.getNode1() == node || edge.getNode2() == node) {
		// edgesToRemove.add(edge);
		// }
		// }
		//
		// for (Edge edge : edgesToRemove) {
		// edges.remove(edge);
		// }

		nodeConnections.remove(node);
		nodes.remove(node);
	}
}
