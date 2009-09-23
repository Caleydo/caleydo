package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;

public interface IDirectedGraphBuilder {

	public void setRootNode(ADrawAbleNode rootNode);

	public ADrawAbleNode getRootNode();

	public void setParentNode(ADrawAbleNode parent);

	public ADrawAbleNode getPartentNode(ADrawAbleNode child);

	public void addChild(ADrawAbleNode child, ADrawAbleNode parent);

	// public ADrawableNode getChildren(ADrawableNode parent);

	public int getNumberOfNodes();

	public int getDephOfDirectedGraph();

	public void setLayerOfNode(int layer);

	public int getLayerOfNode(ADrawAbleNode node);

}
