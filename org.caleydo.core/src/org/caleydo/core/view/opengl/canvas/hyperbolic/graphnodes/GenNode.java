package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

public class GenNode
	extends ADrawAbleNode {

	public GenNode(String nodeName, int iComparableValue) {
		super(nodeName, iComparableValue, HyperbolicRenderStyle.DA_GEN_NODE_DL_OBJ);
	}

}
