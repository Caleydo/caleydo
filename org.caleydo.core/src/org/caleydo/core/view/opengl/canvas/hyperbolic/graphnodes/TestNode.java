package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.DrawAbleObjectFallback;

/**
 * Implementation of a draw able TestNode, just to show how our nodes would work.
 * 
 * @author Georg Neubauer
 */
public final class TestNode
	extends ADrawAbleNode {
	public TestNode(ClusterNode clNode) {
		super(clNode);
		registerDrawAbleObject(new DrawAbleObjectFallback(), new DrawAbleObjectFallback(),
			new DrawAbleObjectFallback(), new DrawAbleObjectFallback(), new DrawAbleObjectFallback());
	}
}