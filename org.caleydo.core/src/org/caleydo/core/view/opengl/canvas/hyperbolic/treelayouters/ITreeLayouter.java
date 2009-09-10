package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;

/**
 * Defines standard interface to the tree layouter.
 * 
 * @author Georg Neubauer
 */
public interface ITreeLayouter
	extends Comparable<ITreeLayouter> {

	void setHighlightedNode(int iNodeID);

	void setHiglightedLine(int iLineID);

	void setTree(Tree<IDrawAbleNode> tree);

	void init(int iGLDisplayListNode, int iGLDisplayListConnection);

	void display(GL gl);

	void animateToNewTree(Tree<IDrawAbleNode> tree);

	void setLayoutDirty();

	void buildDisplayLists(GL gl);

	int getID();
}
