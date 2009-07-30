package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.DrawAbleObjectsFactory;

/**
 * Implementation of a draw able TestNode, just to show how our nodes would work.
 * 
 * @author Georg Neubauer
 */
public final class TestNode
	extends ADrawableNode {

	public TestNode(String nodeName, int iComparableValue) {
		super(nodeName, iComparableValue);
		for (EDrawAbleNodeDetailLevel e : EDrawAbleNodeDetailLevel.values())
			mRepresantations.put(e,
				DrawAbleObjectsFactory.getDrawAbleObject(HyperbolicRenderStyle.DA_TEST_NODE_DL_OBJ.valueOf(
					e.toString()).toString()));
	}
}