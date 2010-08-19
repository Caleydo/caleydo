package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.util.clusterer.ClusterNode;

public class ClusterTreeMapNode extends ATreeMapNode{

	public static ClusterTreeMapNode createFromClusterNodeTree(Tree<ClusterNode> clusterTree){
		ClusterNode clusterNode = clusterTree.getRoot();
		if(clusterNode!=null){
			Tree<ATreeMapNode> tree = new Tree<ATreeMapNode>();
			ClusterTreeMapNode treemapNode = new ClusterTreeMapNode();
			tree.setRootNode(treemapNode);
			treemapNode.setTree(tree);
			treemapNode.data=clusterNode;
			createHelp(treemapNode, clusterNode);
			
			return treemapNode;
		}
		return null;
	}
	
	private static void createHelp(ClusterTreeMapNode treemapNode, ClusterNode clusterNode){
		if(clusterNode.getChildren()==null)
			return;
		for(ClusterNode clusterChild : clusterNode.getChildren()){
			ClusterTreeMapNode treemapChild = new ClusterTreeMapNode();
			treemapChild.data=clusterChild;
//			treemapNode.children.add(treemapChild);
			treemapNode.tree.addChild(treemapNode, treemapChild);
			createHelp(treemapChild, clusterChild);
		}
	}
	
	ClusterNode data;
	//ArrayList<ATreeMapNode> children = new ArrayList<ATreeMapNode>();



	@Override
	public Color getColorAttribute() {
		// TODO return right attribute
		return Color.BLACK;
	}

	@Override
	public float getSizeAttribute() {
		// TODO return right attribute
		return 0;
	}

	@Override
	public String getLabel() {
		return data.getLabel();
	}

	@Override
	public int getPickingID() {
		return data.getID();
	}
	
	public ClusterNode getData() {
		return data;
	}

	public void setData(ClusterNode data) {
		this.data = data;
	}

	@Override
	public ArrayList<ATreeMapNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
