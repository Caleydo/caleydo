package org.caleydo.view.datagraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.caleydo.core.util.collection.Pair;

public class Graph<NodeType extends IDataGraphNode> {
	Vector<NodeType> nodes = null;
	Map<NodeType, Set<NodeType>> nodeConnections = null;
	Set<Pair<NodeType, NodeType>> edges;

	// TODO custom constructor has to be created

	public Graph() {
		nodes = new Vector<NodeType>();
		nodeConnections = new HashMap<NodeType, Set<NodeType>>();
		edges = new HashSet<Pair<NodeType, NodeType>>();
	}

	// node sets
	public Collection<NodeType> getNodes() {
		return nodes;
	}

	public int getNumberOfNodes() {
		if (nodes == null)
			return 0;

		return nodes.size();
	}

	public boolean incident(NodeType node1, NodeType node2) {
		if (nodeConnections == null)
			return false;

		Set<NodeType> connections = nodeConnections.get(node1);
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

	public void addNode(NodeType node) {
		if (nodes.contains(node))
			return;

		nodes.add(node);
	}

	public void addEdge(NodeType node1, NodeType node2) {
		if (!nodes.contains(node1))
			nodes.add(node1);
		if (!nodes.contains(node2))
			nodes.add(node2);
		
		Set<NodeType> node1Edges = nodeConnections.get(node1);

		if (node1Edges == null) {
			node1Edges = new HashSet<NodeType>();
		}
		node1Edges.add(node2);
		nodeConnections.put(node1, node1Edges);

		Set<NodeType> node2Edges = nodeConnections.get(node2);

		if (node2Edges == null) {
			node2Edges = new HashSet<NodeType>();
		}
		node2Edges.add(node1);
		nodeConnections.put(node2, node2Edges);

		edges.add(new Pair<NodeType, NodeType>(node1, node2));
	}

	public Set<Pair<NodeType, NodeType>> getAllEdges() {
		return edges;
	}
	
	public void removeNode(NodeType node) {
		
		Set<NodeType> neighbors = nodeConnections.get(node);
		
		if(neighbors != null) {
			for(NodeType neighbor : neighbors) {
				Set<NodeType> neighborConnections = nodeConnections.get(neighbor);
				if(neighborConnections != null) {
					neighborConnections.remove(node);
				}
			}
		}
		
		Set<Pair<NodeType, NodeType>> edgesToRemove = new HashSet<Pair<NodeType,NodeType>>();
		
		for(Pair<NodeType, NodeType> edge : edges) {
			if(edge.getFirst() == node || edge.getSecond() == node) {
				edgesToRemove.add(edge);
			}
		}
		
		for(Pair<NodeType, NodeType> edge : edgesToRemove) {
			edges.remove(edge);
		}
		
		nodeConnections.remove(node);
		nodes.remove(node);
	}
}
