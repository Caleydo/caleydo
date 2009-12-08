package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters;

import gleem.linalg.Vec3f;

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

	void setHiglightedConnection(int iLineID);

	void setTree(Tree<IDrawAbleNode> tree);

	void init(int iGLDisplayListNode, int iGLDisplayListConnection);

	void display(GL gl);

//	void animateToNewTree(Tree<IDrawAbleNode> tree);
	
	void animateToNewTree(int iExternalID);

	void setLayoutDirty();

	void buildDisplayLists(GL gl);

	int getID();

	boolean isAnimating();

	void setInformationText(String strInformation);
	
	Vec3f getTranslationVector(Vec3f source, Vec3f dest);
	
	Vec3f translateTree(int tree);

	Vec3f getLastTranslationVector();

	void setNewTranslatedTree(Vec3f vec);

	//void changeCanvasDrawing();
}
