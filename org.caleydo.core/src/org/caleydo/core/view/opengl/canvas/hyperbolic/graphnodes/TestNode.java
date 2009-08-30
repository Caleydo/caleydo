package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;

/**
 * Implementation of a draw able TestNode, just to show how our nodes would work.
 * 
 * @author Georg Neubauer
 */
public final class TestNode
	extends ADrawAbleNode {
	public TestNode(String nodeName, int iComparableValue) {
		super(nodeName, iComparableValue, HyperbolicRenderStyle.DA_TEST_NODE_DL_OBJ);
	}
}