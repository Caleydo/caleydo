/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.layout;

import java.util.ArrayList;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.util.color.Color;

/**
 * Static treemap model. For testing purpose only.
 *
 * @author Michael Lafer
 *
 */

public class DefaultTreeNode extends ATreeMapNode {

	public static ATreeMapNode createSampleTree() {
		// DefaultTree tree = new DefaultTree();

		// ArrayList<ATreeMapNode> children;
		// DefaultTreeNode node;

		Tree<ATreeMapNode> tree = new Tree<ATreeMapNode>();

		DefaultTreeNode node = new DefaultTreeNode(tree, 1);
		tree.setRootNode(node);
		tree.addChild(node, new DefaultTreeNode(tree, 1.0 / 3, Color.CYAN.getRGBA(), new ArrayList<ATreeMapNode>(),
				"1.1", 11));

		DefaultTreeNode node2 = new DefaultTreeNode(tree, 12);
		tree.addChild(node, node2);
		tree.addChild(node2, new DefaultTreeNode(tree, 1.0 / 6, Color.GRAY.getRGBA(), "2.1", 21));

		DefaultTreeNode node3 = new DefaultTreeNode(tree, 20);
		tree.addChild(node2, node3);
		tree.addChild(node3, new DefaultTreeNode(tree, 1.0 / 12, Color.ORANGE.getRGBA(), new ArrayList<ATreeMapNode>(),
				"3.1", 31));
		tree.addChild(node3, new DefaultTreeNode(tree, 1.0 / 12, Color.MAGENTA.getRGBA(),
				new ArrayList<ATreeMapNode>(), "3.2", 32));

		DefaultTreeNode node4 = new DefaultTreeNode(tree, 13);
		tree.addChild(node, node4);
		tree.addChild(node4, new DefaultTreeNode(tree, 1.0 / 9, Color.RED.getRGBA(), new ArrayList<ATreeMapNode>(),
				"2.2", 22));
		tree.addChild(node4, new DefaultTreeNode(tree, 1.0 / 9, Color.GREEN.getRGBA(), new ArrayList<ATreeMapNode>(),
				"2.3", 23));
		tree.addChild(node4, new DefaultTreeNode(tree, 1.0 / 9, Color.BLUE.getRGBA(), new ArrayList<ATreeMapNode>(),
				"2.4", 24));

		node.calculateHierarchyLevels(0);

		// ArrayList<ATreeMapNode> children1 = new ArrayList<ATreeMapNode>();
		// children1.add(new DefaultTreeNode(tree, 1.0/12, Color.ORANGE, new
		// ArrayList<ATreeMapNode>(),"3.1",31));
		// children1.add(new DefaultTreeNode(tree, 1.0/12, Color.MAGENTA, new
		// ArrayList<ATreeMapNode>(),"3.2",32));
		//
		// node=new DefaultTreeNode(tree, 1.0/6, null, children);
		// ArrayList<ATreeMapNode> children2 = new ArrayList<ATreeMapNode>();
		// children2.add(node);
		// children2.add(new DefaultTreeNode(tree, 1.0/6, Color.GRAY, new
		// ArrayList<ATreeMapNode>(),"2.1",21));
		// node = new DefaultTreeNode(tree, 1.0/3, null, children2);
		//
		// ArrayList<ATreeMapNode> children2 = new ArrayList<ATreeMapNode>();
		// children2.add(new DefaultTreeNode(tree, 1.0/9, Color.RED, new
		// ArrayList<ATreeMapNode>(),"2.2",22));
		// children2.add(new DefaultTreeNode(tree, 1.0/9, Color.GREEN, new
		// ArrayList<ATreeMapNode>(),"2.3",23));
		// children2.add(new DefaultTreeNode(tree, 1.0/9, Color.BLUE, new
		// ArrayList<ATreeMapNode>(),"2.4",24));
		//
		// ArrayList<ATreeMapNode> children3 = new ArrayList<ATreeMapNode>();
		// children3.add(new DefaultTreeNode(tree, 1.0/3, Color.CYAN, new
		// ArrayList<ATreeMapNode>(),"1.1",11));
		// children3.add(node);
		// children3.add(new DefaultTreeNode(tree, 1.0/3, null, children2));
		//
		//
		// return new DefaultTreeNode(tree, 1, null, children3);
		return node;
	}

	float size;
	float[] color;
	// ArrayList<ATreeMapNode> children;
	String label = "";

	// int id;

	public DefaultTreeNode(Tree<ATreeMapNode> tree, double size, float[] color, ArrayList<ATreeMapNode> children, String label, int id) {
		this.tree = tree;
		this.size = (float) size;
		this.color = color;
		// this.children=children;
		tree.addChildren(this, children);
		this.label = label;
		this.id = id;

	}

	public DefaultTreeNode(Tree<ATreeMapNode> tree, double size, float[] color, ArrayList<ATreeMapNode> children) {
		this(tree, size, color, children, "", 0);
	}

	public DefaultTreeNode(Tree<ATreeMapNode> tree, int id) {
		this.tree = tree;
		this.id = id;
	}

	public DefaultTreeNode(Tree<ATreeMapNode> tree, double size, float[] color, String label, int id) {
		this.tree = tree;
		this.size = (float) size;
		this.color = color;
		this.label = label;
		this.id = id;
	}

	@Override
	public float getSizeAttribute() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public float[] getColorAttribute() {
		// TODO Auto-generated method stub
		return color;

	}

	// @Override
	// public ArrayList<ATreeMapNode> getChildren() {
	// // TODO Auto-generated method stub
	// return children;
	// }

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return label;
	}

	@Override
	public String toString() {
		return label == null ? "" : label;
	}

	@Override
	public Integer getID() {
		return id;
	}

	@Override
	public Tree<ATreeMapNode> getTree() {
		return tree;
	}

}
