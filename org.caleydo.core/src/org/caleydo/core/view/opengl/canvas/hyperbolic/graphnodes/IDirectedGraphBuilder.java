package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawableNode;

public interface IDirectedGraphBuilder {
	
	public void setRootNode(ADrawableNode rootNode);
	
	public ADrawableNode getRootNode();
	
	public void setParentNode(ADrawableNode parent);
	
	public ADrawableNode getPartentNode(ADrawableNode child);
	
	public void addChild(ADrawableNode child,  ADrawableNode parent);
	
//	public ADrawableNode getChildren(ADrawableNode parent);
	
	public int getNumberOfNodes();
	
	public int getDephOfDirectedGraph();
	
	public void setLayerOfNode(int layer);
	
	public int getLayerOfNode(ADrawableNode node);

}
