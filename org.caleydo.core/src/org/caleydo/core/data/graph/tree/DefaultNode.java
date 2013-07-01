/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.graph.tree;

public class DefaultNode
	extends AHierarchyElement<DefaultNode> {

	String nodeName;
	int iComparableValue;

	public DefaultNode(Tree<DefaultNode> tree, String nodeName, int iComparableValue) {
		super(tree);
		this.iComparableValue = iComparableValue;
		this.nodeName = nodeName;
		this.id = iComparableValue;
	}

	public String getNodeName() {
		return nodeName;
	}

	@Override
	public String toString() {
		return nodeName + " " + iComparableValue;
	}

	@Override
	public int getComparableValue() {
		return iComparableValue;
	}
}
