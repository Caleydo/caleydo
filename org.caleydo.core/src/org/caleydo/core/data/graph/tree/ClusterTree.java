/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.graph.tree;

import org.caleydo.core.id.IDType;

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
