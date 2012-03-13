package org.caleydo.view.datagraph;

import org.caleydo.view.datagraph.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.datagraph.node.IDVINode;

public class Edge {
	private IDVINode node1;
	private IDVINode node2;
	private AEdgeRenderer edgeRenderer;

	public Edge(IDVINode node1, IDVINode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public IDVINode getNode1() {
		return node1;
	}

	public void setNode1(IDVINode node1) {
		this.node1 = node1;
	}

	public IDVINode getNode2() {
		return node2;
	}

	public void setNode2(IDVINode node2) {
		this.node2 = node2;
	}

	public AEdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}

	public void setEdgeRenderer(AEdgeRenderer edgeRenderer) {
		this.edgeRenderer = edgeRenderer;
	}

}
