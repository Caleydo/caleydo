package org.caleydo.view.datawindows;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.DefaultNode;
import org.caleydo.core.data.graph.tree.Tree;

public class DataWindowsDisk extends PoincareDisk{

	@SuppressWarnings("unused")
	// muss wieder umgeändert werden:
	private Tree<DefaultNode> tree;
	
	public DataWindowsDisk(double diskRadius) {
		super(diskRadius);
		
	}

	public void drawTree(){
		
	}
	
	public void drawNode(){
		
	}
	
	public void drawLine() {
		
	}
	
	public void drawBackground(){
		
	}

	public void loadTree() {
		// creating a tree for testing
		System.out.println("loadTree Called");

		//PoincareNode node = new PoincareNode(tree, "Root", 1);
		DefaultNode node = new DefaultNode(tree, "Root",1);
	
		//stürzt ab:
		
		//tree.setRootNode(node);
//		tree.addChild(node, new PoincareNode(tree, "Child1 l1", 1));
//		tree.addChild(node, new PoincareNode(tree, "Child2 l1", 3));
//
//		int iCount = 5;
//		for (PoincareNode tempNode : tree.getChildren(node)) {
//			tree.addChild(tempNode, new PoincareNode(tree, "Child3 l1",
//					iCount--));
//			tree.addChild(tempNode, new PoincareNode(tree, "Child4 l1",
//					iCount--));
//		}

	}
}
