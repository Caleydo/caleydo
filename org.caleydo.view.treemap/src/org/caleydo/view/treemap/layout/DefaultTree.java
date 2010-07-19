package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.Vector;

public class DefaultTree implements AbstractTree {

	public AbstractTreeNode root;
	
	@Override
	public AbstractTreeNode getRoot() {
		// TODO Auto-generated method stub
		return root;
	}
	
	public static AbstractTree createSampleTree(){
		Vector<AbstractTreeNode> children;
		DefaultTreeNode node;
		
		children = new Vector<AbstractTreeNode>();
		children.add(new DefaultTreeNode(1.0/12, Color.GREEN, new Vector<AbstractTreeNode>(),"3.1"));
		children.add(new DefaultTreeNode(1.0/12, Color.RED, new Vector<AbstractTreeNode>(),"3.2"));
		
		node=new DefaultTreeNode(2.0/12, null, children);
		children = new Vector<AbstractTreeNode>();
		children.add(node);
		children.add(new DefaultTreeNode(1.0/6, Color.BLUE, new Vector<AbstractTreeNode>(),"2.1"));
		node = new DefaultTreeNode(1.0/3, null, children);
		
		Vector<AbstractTreeNode> children2 = new Vector<AbstractTreeNode>();
		children2.add(new DefaultTreeNode(1.0/9, Color.RED, new Vector<AbstractTreeNode>(),"2.2"));
		children2.add(new DefaultTreeNode(1.0/9, Color.GREEN, new Vector<AbstractTreeNode>(),"2.3"));
		children2.add(new DefaultTreeNode(1.0/9, Color.BLUE, new Vector<AbstractTreeNode>(),"2.4"));
		
		Vector<AbstractTreeNode> children3 = new Vector<AbstractTreeNode>();
		children3.add(new DefaultTreeNode(1.0/3, Color.RED, new Vector<AbstractTreeNode>(),"1.1"));
		children3.add(node);
		children3.add(new DefaultTreeNode(1.0/3, null, children2));
		
		DefaultTree tree = new DefaultTree();
		tree.root= new DefaultTreeNode(1, null, children3);
		return tree;
	}

}
