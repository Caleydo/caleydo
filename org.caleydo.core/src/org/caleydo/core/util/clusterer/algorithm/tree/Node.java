/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
