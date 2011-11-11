package org.caleydo.core.data.graph.tree;

import org.caleydo.core.data.id.IDType;

public class ClusterTree
	extends Tree<ClusterNode> {

	/**
	 * This should only be used for de-serialization
	 */
	public ClusterTree() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param leaveIDType
	 *            the id type of the leaves
	 * @param expectedSize
	 *            An estimate for the expected size - good estimates improve performance
	 */
	public ClusterTree(IDType leaveIDType, int expectedSize) {
		super(leaveIDType, expectedSize);
	}

}
