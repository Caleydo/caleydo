package org.caleydo.core.data.graph.tree;

public class DefaultNode
	extends AHierarchyElement<DefaultNode> {

	String nodeName;
	int iComparableValue;

	DefaultNode(Tree<DefaultNode> tree, String nodeName, int iComparableValue) {
		super(tree);
		this.iComparableValue = iComparableValue;
		this.nodeName = nodeName;
	}

	public String getNodeName() {
		return nodeName;
	}

	@Override
	public String toString() {
		return nodeName + " " + iComparableValue;
	}

}
