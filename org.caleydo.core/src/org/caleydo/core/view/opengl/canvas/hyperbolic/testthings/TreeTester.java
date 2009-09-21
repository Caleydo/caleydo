package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.IDrawAbleNode;


public class TreeTester {

	Tree<IDrawAbleNode> tree;
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//		TreeTester tester = new TreeTester();
//		tester.runTest();
//	}
	//TODO: DELETE AS SOON AS POSSIBLE!!!
	
	public void runTest()
	{
//		DirectedGraph<String, DefaultEdge> graph =
//			new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
//		graph.addVertex("Test");
//		graph.addVertex("Child");
//		graph.addVertex("Child2");
//
//		graph.addEdge("Test", "Child");
//		graph.addEdge("Test", "Child2");
//
//		System.out.println(graph.toString());

		tree = new Tree<IDrawAbleNode>();
//		tree.getRoot();
		IDrawAbleNode node = new IDrawAbleNode("Root", 1);
		tree.setRootNode(node);
		IDrawAbleNode childNode = new IDrawAbleNode("Child1 l1", 1);
		tree.addChild(node, childNode);

		
		tree.addChild(node, new IDrawAbleNode("Child2 l1", 3));
		tree.addChild(node, new IDrawAbleNode("Child3 l1", 5));

		int iCount = 50;
		for (IDrawAbleNode tempNode : tree.getChildren(node)) {
			for (int i = 1; i <= 4; i++)
			{
				String childname = "Child " + i;
				IDrawAbleNode grandChild = new IDrawAbleNode(childname, iCount--);
				tree.addChild(tempNode, new IDrawAbleNode(childname, iCount--));
				
				//TO Test
//				tree.addChild(tempNode, grandChild);
//				tree.addChild(grandChild, new DefaultNode(childname, iCount--));
				
				
			}
			//tree.addChild(tempNode, new DefaultNode("Child5 l1", iCount--));
		}
//		DefaultNode tempNode = tree.getRoot();
//		for (int i=0;i<30;i++)
//		{
//			DefaultNode tn = new DefaultNode("karli " + i,60 + i);
//			tree.addChild(tempNode, tn);
//			tempNode = tn;
//		}
		
		
		System.out.println(tree.getGraph().toString());

		
//		tempNode = tree.getRoot();
//		printChildren(tempNode);


	//	System.out.println(tree.getDephOfTree());
		
		
	}
	
	private  boolean printChildren(IDrawAbleNode node)
	{	
		System.out.println(node.toString());
		if(tree.hasChildren(node))
		{
			
			for(IDrawAbleNode tempNode : tree.getChildren(node))
			{
				printChildren(tempNode);
			}
			return true;
		}
		return false;
	}
	public Tree<IDrawAbleNode> getTree()
	{
		return tree;
	}

}
