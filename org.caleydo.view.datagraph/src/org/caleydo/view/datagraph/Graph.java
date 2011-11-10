package org.caleydo.view.datagraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.caleydo.view.datagraph.node.IDataGraphNode;

public class Graph {
	Vector<IDataGraphNode> nodes = null;
	Map<IDataGraphNode, Set<Edge>> nodeConnections = null;
	Set<Edge> edges;

	// TODO custom constructor has to be created

	public Graph() {
		nodes = new Vector<IDataGraphNode>();
		nodeConnections = new HashMap<IDataGraphNode, Set<Edge>>();
		edges = new HashSet<Edge>();
	}

	// node sets
	public Collection<IDataGraphNode> getNodes() {
		return nodes;
	}

	public int getNumberOfNodes() {
		if (nodes == null)
			return 0;

		return nodes.size();
	}

	public Set<Edge> getEdgesOfNode(IDataGraphNode node) {
		return nodeConnections.get(node);
	}

	public boolean incident(IDataGraphNode node1, IDataGraphNode node2) {
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

	public void addNode(IDataGraphNode node) {
		if (nodes.contains(node))
			return;

		nodes.add(node);
	}

	public Edge addEdge(IDataGraphNode node1, IDataGraphNode node2) {
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

	public void removeNode(IDataGraphNode node) {

		Set<Edge> nodeEdges = nodeConnections.get(node);

		if (nodeEdges != null) {
			for (Edge edge : nodeEdges) {
				IDataGraphNode neighbor = edge.getNode1() == node ? edge.getNode2()
						: edge.getNode1();
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
