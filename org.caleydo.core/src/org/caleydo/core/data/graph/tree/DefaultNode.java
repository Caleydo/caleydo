package org.caleydo.core.data.graph.tree;

public class DefaultNode
	implements Comparable<DefaultNode> {

	String nodeName;
	int iComparableValue;

	DefaultNode(String nodeName, int iComparableValue) {
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
