/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.algorithm.tree;

/**
 * Helper class used in @link {@link TreeClusterer}. Stores indices of the corresponding nodes and the
 * similarity (correlation) of two nodes.
 * 
 * @author Bernhard Schlegl
 */
public class Node {
	private float correlation;
	private int left;
	private int right;

	public Node() {
		this.setCorrelation(0);
		this.setLeft(0);
		this.setRight(0);
	}

	public void setCorrelation(float correlation) {
		this.correlation = correlation;
	}

	public float getCorrelation() {
		return correlation;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getLeft() {
		return left;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public int getRight() {
		return right;
	}
}
