/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.data.graph.tree;

import org.caleydo.core.data.collection.EDataClass;
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

		tree =
			new Tree<DefaultNode>(IDType.registerType("test", IDCategory.registerCategory("testcategory"),
				EDataClass.NATURAL_NUMBER), 3);
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
