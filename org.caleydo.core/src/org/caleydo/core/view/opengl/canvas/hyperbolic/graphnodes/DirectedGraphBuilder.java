package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.caleydo.core.view.opengl.canvas.hyperbolic.lineartree.DefaultNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public final class DirectedGraphBuilder
	implements IDirectedGraphBuilder {
	
	DirectedGraph<ADrawableNode, DefaultEdge> graph;

	private ADrawableNode rootNode;
	
	private HashMap<Integer, ADrawableNode> hashNodes;
	
	int iDeph = 0;
	
	int iNumberOfNodes = 0;
	
	//ArrayList<NodeType> folios;
	private HashMap<Integer, ArrayList<DefaultNode>> layerMap;
	
	/**
	 * Constructor
	 */
	
	public DirectedGraphBuilder()
	{
		graph = new DefaultDirectedGraph<ADrawableNode, DefaultEdge>(DefaultEdge.class);
		hashNodes = new HashMap<Integer, ADrawableNode>();
	}
//	@Override
//	public ADrawableNode getChildren(ADrawableNode parent) {
//		Set<DefaultEdge> setEdges = graph.outgoingEdgesOf(parent);
//
//		ArrayList<ADrawableNode> alNodes = new ArrayList<ADrawableNode>();
//		for (DefaultEdge tempEdge : setEdges) {
//			alNodes.add(graph.getEdgeTarget(tempEdge));
//		}
//
//		Collections.sort(alNodes);
//
//		if (alNodes.isEmpty())
//			return null;
//		else
//			return alNodes;
//	}

	@Override
	public int getDephOfDirectedGraph() {
		return layerMap.size();
	}

	@Override
	public int getLayerOfNode(ADrawableNode node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfNodes() {
		return iNumberOfNodes;
	}

	@Override
	public ADrawableNode getPartentNode(ADrawableNode child) {
		Set<DefaultEdge> setEdges = graph.incomingEdgesOf(child);
		DefaultEdge edge = null;

		for (DefaultEdge tempEdge : setEdges) {
			edge = (DefaultEdge) tempEdge;
		}
		if (edge == null) {
			// this is the root node
			return null;
		}

		ADrawableNode parent = graph.getEdgeSource(edge);
		return parent;
	}

	@Override
	public ADrawableNode getRootNode() {
		return rootNode;
	}

	@Override
	public void addChild(ADrawableNode child, ADrawableNode parent) {
		graph.addVertex(child);
		graph.addEdge(parent, child);
		increaseNumberOfNodes();

	}
	@Override
	public void setLayerOfNode(int layer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setParentNode(ADrawableNode parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRootNode(ADrawableNode rootNode) {
		this.rootNode = rootNode;
		graph.addVertex(rootNode);
		iDeph = 1;
	}
	
	public int getNumberOfNodesInLayer(int layer)
	{
		return layerMap.get(layer).size();
	}
	private void increaseNumberOfNodes()
	{
		iNumberOfNodes++;
	}

}
