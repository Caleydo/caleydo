package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.HyperbolicRenderStyle;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawableobjects.DrawAbleObjectFallback;

/**
 * Implementation of a draw able TestNode, just to show how our nodes would work.
 * 
 * @author Georg Neubauer
 */
public final class TestNode
	extends ADrawAbleNode {
	public TestNode(ClusterNode clNode){//String nodeName, int iComparableValue) {
		super(clNode);
		registerDrawAbleObject(EDrawAbleNodeDetailLevel.VeryHigh, new DrawAbleObjectFallback());
		registerDrawAbleObject(EDrawAbleNodeDetailLevel.High, new DrawAbleObjectFallback());
		registerDrawAbleObject(EDrawAbleNodeDetailLevel.Normal, new DrawAbleObjectFallback());
		registerDrawAbleObject(EDrawAbleNodeDetailLevel.Low, new DrawAbleObjectFallback());
		registerDrawAbleObject(EDrawAbleNodeDetailLevel.VeryLow, new DrawAbleObjectFallback());
	}
}