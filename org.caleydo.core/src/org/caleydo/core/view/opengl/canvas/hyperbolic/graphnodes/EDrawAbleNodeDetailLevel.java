package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

/**
 * Enum determines the detail level of draw able nodes.
 * Nodes have to specify their output on specific detail level on their own.
 * 
 * @author Georg Neubauer
 */
public enum EDrawAbleNodeDetailLevel {
	/**
	 * Used for root node in standalone view 
	 */
	VeryHigh,
	High,
	Normal,
	Low,
	VeryLow
}