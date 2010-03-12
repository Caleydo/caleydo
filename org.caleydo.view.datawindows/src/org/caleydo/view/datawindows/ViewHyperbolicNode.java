package org.caleydo.view.datawindows;

import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.view.opengl.canvas.AGLView;


public class ViewHyperbolicNode
extends PoincareNode {

	private AGLView glView;
	
	public ViewHyperbolicNode(Tree<PoincareNode> tree, String nodeName,
			int iComparableValue, AGLView glView) {
		super(tree, nodeName, iComparableValue);
		
		this.glView = glView;
	}

	public AGLView getGlView() {
		return glView;
	}
}
