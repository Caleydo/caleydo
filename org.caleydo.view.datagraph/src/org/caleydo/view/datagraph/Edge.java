package org.caleydo.view.datagraph;

import org.caleydo.view.datagraph.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public class Edge {
	private IDataGraphNode node1;
	private IDataGraphNode node2;
	private AEdgeRenderer edgeRenderer;

	public Edge(IDataGraphNode node1, IDataGraphNode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public IDataGraphNode getNode1() {
		return node1;
	}

	public void setNode1(IDataGraphNode node1) {
		this.node1 = node1;
	}

	public IDataGraphNode getNode2() {
		return node2;
	}

	public void setNode2(IDataGraphNode node2) {
		this.node2 = node2;
	}

	public AEdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}

	public void setEdgeRenderer(AEdgeRenderer edgeRenderer) {
		this.edgeRenderer = edgeRenderer;
	}

}
