package org.caleydo.core.util.clusterer;

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
