package org.caleydo.view.datagraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class Graph {
	Vector<Object> nodes = null;
	Map<Object, Set<Object>> nodeConnections = null;

	// TODO custom constructor has to be created

	public Graph() {
		nodes = new Vector<Object>();
		nodeConnections = new HashMap<Object, Set<Object>>();
	}

	// node sets
	public Collection<Object> getNodes() {
		return nodes;
	}

	public int getNumberOfNodes() {
		if (nodes == null)
			return 0;

		return nodes.size();
	}

	public boolean incident(Object node1, Object node2) {
		if (nodeConnections == null)
			return false;

		Set<Object> connections = nodeConnections.get(node1);
		if (connections == null)
			return false;

		return connections.contains(node2);
	}

	public boolean hasEdges() {
		if (nodeConnections == null)
			return false;
		if (nodeConnections.size() == 0)
			return false;

		return true;
	}

	public void addNode(Object node) {
		if (nodes.contains(node))
			return;

		nodes.add(node);
	}

	public void addEdge(Object node1, Object node2) {
		if (!nodes.contains(node1) || !nodes.contains(node2))
			return;

		Set<Object> node1Edges = nodeConnections.get(node1);

		if (node1Edges == null) {
			node1Edges = new HashSet<Object>();
		}
		node1Edges.add(node2);

		Set<Object> node2Edges = nodeConnections.get(node2);

		if (node2Edges == null) {
			node2Edges = new HashSet<Object>();
		}
		node2Edges.add(node1);
	}
}
