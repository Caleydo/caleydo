package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.beans.FeatureDescriptor;
import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.util.mapping.color.ColorMapping;

public class ClusterTreeMapNode extends ATreeMapNode{

	public static ClusterTreeMapNode createFromClusterNodeTree(Tree<ClusterNode> clusterTree, ColorMapping colorMapper){
		
		ClusterNode clusterNode = clusterTree.getRoot();
		if(clusterNode!=null){
			Tree<ATreeMapNode> tree = new Tree<ATreeMapNode>();
			ClusterTreeMapNode treemapNode = new ClusterTreeMapNode();
			ClusterReferenzData referenz = new ClusterReferenzData();
			referenz.colorMapper=colorMapper;
			tree.setRootNode(treemapNode);
			treemapNode.setTree(tree);
			treemapNode.data=clusterNode;
			treemapNode.referenzData=referenz;
			createHelp(treemapNode, clusterNode,referenz);
			
			return treemapNode;
		}
		return null;
	}
	
	private static void createHelp(ClusterTreeMapNode treemapNode, ClusterNode clusterNode, ClusterReferenzData referenz){
		if(clusterNode.getChildren()==null){
			referenz.sizeReferenzValue+=clusterNode.getSize();
			
			return;
	}
		for(ClusterNode clusterChild : clusterNode.getChildren()){
			ClusterTreeMapNode treemapChild = new ClusterTreeMapNode();
			treemapChild.data=clusterChild;
			treemapChild.tree=treemapNode.tree;
			treemapChild.referenzData=referenz;
			treemapNode.tree.addChild(treemapNode, treemapChild);
			createHelp(treemapChild, clusterChild,referenz);
		}
	}
	
	ClusterReferenzData referenzData;
		
	ClusterNode data;



	@Override
	public float[] getColorAttribute() {
		// TODO return right attribute
		
		return referenzData.colorMapper.getColor(data.getAverageExpressionValue()/referenzData.colorReferenzSpace);
	}

	@Override
	public float getSizeAttribute() {
		// TODO return right attribute
		return data.getSize()/referenzData.sizeReferenzValue;
	}

	@Override
	public String getLabel() {
		return data.getLabel();
	}

	@Override
	public int getPickingID() {
		return data.getID();
	}
	
	public Integer getID(){
		return data.getID();
	}
	
	public ClusterNode getData() {
		return data;
	}

	public void setData(ClusterNode data) {
		this.data = data;
	}

}
