package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec4f;

import javax.media.opengl.GL;

public abstract class ADefaultDrawableNode
	implements Comparable<ADefaultDrawableNode>, IDrawableNode {
	String nodeName;
	int iComparableValue;

	public ADefaultDrawableNode(String nodeName, int iComparableValue) {
		this.nodeName = nodeName;
		this.iComparableValue = iComparableValue;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	@Override
	public int compareTo(ADefaultDrawableNode node) {
		return this.iComparableValue - node.iComparableValue;
	}

	@Override
	public String toString() {
		return this.nodeName + " " + this.iComparableValue;
	}

	@Override
	public abstract int drawAtPostion(GL gl, float fXCoord, float fYCoord, float fHeight, float fWidth,
		ENodeDetailLevelType eDetailLevel);
}
