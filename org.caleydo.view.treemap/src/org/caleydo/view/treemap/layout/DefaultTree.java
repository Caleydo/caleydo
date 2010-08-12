package org.caleydo.view.treemap.layout;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.util.clusterer.ClusterNode;

public class DefaultTree extends AbstractTree {

	public ATreeMapNode root;
	
	@Override
	public ATreeMapNode getRoot() {
		// TODO Auto-generated method stub
		return root;
	}
	
	public static AbstractTree createFromClusterTree(Tree<ClusterNode> clusterTree){
//		clusterTree.getChildren(clusterTree.getRoot()));
		return null;
	}
	
//	public static AbstractTree createSampleTree(){
//		DefaultTree tree = new DefaultTree();
//		
//		ArrayList<ATreeMapNode> children;
//		DefaultTreeNode node;
//		
//		children = new ArrayList<ATreeMapNode>();
//		children.add(new DefaultTreeNode(tree, 1.0/12, Color.ORANGE, new ArrayList<ATreeMapNode>(),"3.1"));
//		children.add(new DefaultTreeNode(tree, 1.0/12, Color.MAGENTA, new ArrayList<ATreeMapNode>(),"3.2"));
//		
//		node=new DefaultTreeNode(tree, 1.0/6, null, children);
//		children = new ArrayList<ATreeMapNode>();
//		children.add(node);
//		children.add(new DefaultTreeNode(tree, 1.0/6, Color.GRAY, new ArrayList<ATreeMapNode>(),"2.1"));
//		node = new DefaultTreeNode(tree, 1.0/3, null, children);
//		
//		ArrayList<ATreeMapNode> children2 = new ArrayList<ATreeMapNode>();
//		children2.add(new DefaultTreeNode(tree, 1.0/9, Color.RED, new ArrayList<ATreeMapNode>(),"2.2"));
//		children2.add(new DefaultTreeNode(tree, 1.0/9, Color.GREEN, new ArrayList<ATreeMapNode>(),"2.3"));
//		children2.add(new DefaultTreeNode(tree, 1.0/9, Color.BLUE, new ArrayList<ATreeMapNode>(),"2.4"));
//		
//		ArrayList<ATreeMapNode> children3 = new ArrayList<ATreeMapNode>();
//		children3.add(new DefaultTreeNode(tree, 1.0/3, Color.CYAN, new ArrayList<ATreeMapNode>(),"1.1"));
//		children3.add(node);
//		children3.add(new DefaultTreeNode(tree, 1.0/3, null, children2));
//		
//		
//		tree.root= new DefaultTreeNode(tree, 1, null, children3);
//		return tree;
//	}

}
