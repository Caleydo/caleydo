package org.caleydo.core.view.opengl.canvas.hyperbolic.testthings;

public class DefaultNode
	implements Comparable<DefaultNode> {

	String nodeName;
	int iComparableValue;

	public DefaultNode(String nodeName, int iComparableValue) {
		this.iComparableValue = iComparableValue;
		this.nodeName = nodeName;
	}

	public String getNodeName() {
		return nodeName;
	}

	@Override
	public int compareTo(DefaultNode node) {
		return iComparableValue - node.iComparableValue;
	}

	@Override
	public String toString() {
		return nodeName + " " + iComparableValue;
	}

}
