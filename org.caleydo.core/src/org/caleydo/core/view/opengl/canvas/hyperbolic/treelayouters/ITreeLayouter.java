package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import javax.media.opengl.GL;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;

/**
 * Defines standard interface to the tree layouter.
 * 
 * @author Georg Neubauer
 *
 */
public interface ITreeLayouter {

	/**
	 * Set the tree to view by the layouter.
	 * 
	 * @param tree
	 * @return
	 */
//	public void setTree(Tree<IDrawableNode> tree);
	
	/**
	 * Draw the tree layout.
	 * 
	 * TODO: specify params and maybe naming (espacially GLLists and Animation)
	 * @return
	 */
//	public void renderTreeLayout(GL gl);  
	
//	/**
//	 * Set the boarder space in percent.
//	 * 
//	 * @param fXBoarderSpacePercentage
//	 * @param fYBoarderSpacePercentage
//	 */
//	void setBoarderSpaces(float fXBoarderSpacePercentage, float fYBoarderSpacePercentage);

//	void renderTreeLayout(GL gl);

	void setHighlightedNode(int iNodeID);
	
	void setHiglightedLine(int iLineID);

	void setTree(Tree<IDrawAbleNode> tree);

	void init(GL gl);

	void display(GL gl);

	void animateToNewTree(Tree<IDrawAbleNode> tree);

	void setLayoutDirty();

	void buildDisplayLists(GL gl);

	void resetHighlight();
}
