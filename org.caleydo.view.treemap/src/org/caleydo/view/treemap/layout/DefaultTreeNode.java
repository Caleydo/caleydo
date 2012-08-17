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
package org.caleydo.view.treemap.layout;

import java.awt.Color;
import java.util.ArrayList;
import org.caleydo.core.data.graph.tree.Tree;

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
		tree.addChild(node, new DefaultTreeNode(tree, 1.0 / 3, Color.CYAN.getColorComponents(null), new ArrayList<ATreeMapNode>(), "1.1", 11));

		DefaultTreeNode node2 = new DefaultTreeNode(tree, 12);
		tree.addChild(node, node2);
		tree.addChild(node2, new DefaultTreeNode(tree, 1.0 / 6, Color.GRAY.getColorComponents(null), "2.1", 21));

		DefaultTreeNode node3 = new DefaultTreeNode(tree, 20);
		tree.addChild(node2, node3);
		tree.addChild(node3, new DefaultTreeNode(tree, 1.0 / 12, Color.ORANGE.getColorComponents(null), new ArrayList<ATreeMapNode>(), "3.1", 31));
		tree.addChild(node3, new DefaultTreeNode(tree, 1.0 / 12, Color.MAGENTA.getColorComponents(null), new ArrayList<ATreeMapNode>(), "3.2", 32));

		DefaultTreeNode node4 = new DefaultTreeNode(tree, 13);
		tree.addChild(node, node4);
		tree.addChild(node4, new DefaultTreeNode(tree, 1.0 / 9, Color.RED.getColorComponents(null), new ArrayList<ATreeMapNode>(), "2.2", 22));
		tree.addChild(node4, new DefaultTreeNode(tree, 1.0 / 9, Color.GREEN.getColorComponents(null), new ArrayList<ATreeMapNode>(), "2.3", 23));
		tree.addChild(node4, new DefaultTreeNode(tree, 1.0 / 9, Color.BLUE.getColorComponents(null), new ArrayList<ATreeMapNode>(), "2.4", 24));

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
