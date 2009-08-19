package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public final class DirectedGraphBuilder
	implements IDirectedGraphBuilder {

	DirectedGraph<ADrawAbleNode, DefaultEdge> graph;

	private ADrawAbleNode rootNode;

	private HashMap<Integer, ADrawAbleNode> hashNodes;

	int iDeph = 0;

	int iNumberOfNodes = 0;

	// ArrayList<NodeType> folios;
	private HashMap<Integer, ArrayList<DefaultNode>> layerMap;

	/**
	 * Constructor
	 */

	public DirectedGraphBuilder() {
		graph = new DefaultDirectedGraph<ADrawAbleNode, DefaultEdge>(DefaultEdge.class);
		hashNodes = new HashMap<Integer, ADrawAbleNode>();
	}

	// @Override
	// public ADrawableNode getChildren(ADrawableNode parent) {
	// Set<DefaultEdge> setEdges = graph.outgoingEdgesOf(parent);
	//
	// ArrayList<ADrawableNode> alNodes = new ArrayList<ADrawableNode>();
	// for (DefaultEdge tempEdge : setEdges) {
	// alNodes.add(graph.getEdgeTarget(tempEdge));
	// }
	//
	// Collections.sort(alNodes);
	//
	// if (alNodes.isEmpty())
	// return null;
	// else
	// return alNodes;
	// }

	@Override
	public int getDephOfDirectedGraph() {
		return layerMap.size();
	}

	@Override
	public int getLayerOfNode(ADrawAbleNode node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfNodes() {
		return iNumberOfNodes;
	}

	@Override
	public ADrawAbleNode getPartentNode(ADrawAbleNode child) {
		Set<DefaultEdge> setEdges = graph.incomingEdgesOf(child);
		DefaultEdge edge = null;

		for (DefaultEdge tempEdge : setEdges) {
			edge = (DefaultEdge) tempEdge;
		}
		if (edge == null) {
			// this is the root node
			return null;
		}

		ADrawAbleNode parent = graph.getEdgeSource(edge);
		return parent;
	}

	@Override
	public ADrawAbleNode getRootNode() {
		return rootNode;
	}

	@Override
	public void addChild(ADrawAbleNode child, ADrawAbleNode parent) {
		graph.addVertex(child);
		graph.addEdge(parent, child);
		increaseNumberOfNodes();

	}

	@Override
	public void setLayerOfNode(int layer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParentNode(ADrawAbleNode parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRootNode(ADrawAbleNode rootNode) {
		this.rootNode = rootNode;
		graph.addVertex(rootNode);
		iDeph = 1;
	}

	public int getNumberOfNodesInLayer(int layer) {
		return layerMap.get(layer).size();
	}

	private void increaseNumberOfNodes() {
		iNumberOfNodes++;
	}

}
