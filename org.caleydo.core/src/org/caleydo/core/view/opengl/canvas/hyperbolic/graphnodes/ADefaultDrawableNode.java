package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes;

import gleem.linalg.Vec4f;

import javax.media.opengl.GL;

public abstract class ADefaultDrawableNode
	implements Comparable<ADefaultDrawableNode>, IDrawableNode {
	String nodeName;
	int iComparableValue;

	float fRed = 0;
	float fGreen = 0;
	float fBlue = 0;
	float fAlpha = 1;

	float fXCoord = 0;
	float fYCoord = 0;

	float fHeight = 0;
	float fWidth = 0;

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
