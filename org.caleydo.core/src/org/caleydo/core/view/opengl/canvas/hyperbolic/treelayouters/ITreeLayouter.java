package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

/**
 * TODO: replace with standard tree someday! 
 */
import javax.media.opengl.GL;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.ADrawAbleNode;

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
	
	/**
	 * Set the boarder space in percent.
	 * 
	 * @param fXBoarderSpacePercentage
	 * @param fYBoarderSpacePercentage
	 */
	public void setBoarderSpaces(float fXBoarderSpacePercentage, float fYBoarderSpacePercentage);

	void renderTreeLayout(GL gl, Tree<ADrawAbleNode> tree);
}
