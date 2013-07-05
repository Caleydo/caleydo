/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.graph.tree;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;

public class TreeTester {

	Tree<DefaultNode> tree;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TreeTester tester = new TreeTester();
		tester.runTest();
	}

	private void runTest() {
		// DirectedGraph<String, DefaultEdge> graph =
		// new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		// graph.addVertex("Test");
		// graph.addVertex("Child");
		// graph.addVertex("Child2");
		//
		// graph.addEdge("Test", "Child");
		// graph.addEdge("Test", "Child2");
		//
		// System.out.println(graph.toString());

		tree = new Tree<DefaultNode>(IDType.registerType("test", IDCategory.registerCategory("testcategory"),
				EDataType.INTEGER), 3);
		// tree.getRoot();
		DefaultNode node = new DefaultNode(tree, "Root", 1);
		tree.setRootNode(node);
		tree.addChild(node, new DefaultNode(tree, "Child1 l1", 1));
		tree.addChild(node, new DefaultNode(tree, "Child2 l1", 3));

		int iCount = 5;
		for (DefaultNode tempNode : tree.getChildren(node)) {
			tree.addChild(tempNode, new DefaultNode(tree, "Child3 l1", iCount--));
			tree.addChild(tempNode, new DefaultNode(tree, "Child4 l1", iCount--));
		}

		System.out.println(tree.getGraph().toString());

		DefaultNode tempNode = tree.getRoot();
		printChildren(tempNode);

	}

	private boolean printChildren(DefaultNode node) {
		System.out.println(node.toString());
		if (tree.hasChildren(node)) {

			for (DefaultNode tempNode : tree.getChildren(node)) {
				printChildren(tempNode);
			}
			return true;
		}
		return false;
	}

}
