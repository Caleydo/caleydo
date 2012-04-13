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
package org.caleydo.view.datawindows;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.Tree;

public class PoincareNode extends AHierarchyElement<PoincareNode> {

	private float distanceFromOrigin;
	private float[] position;
	private float[] zoomedPosition;
	private float childrenAngleOffset;
	private PoincareNode openLink;
	private boolean linked = false;
	private int levelOfDetail = 0;

	public boolean markedToRemove = false;
	public boolean nonExistent = false;
	public boolean eyeTrackable = false;

	String nodeName;
	int iComparableValue;
	public boolean highLighted = false;

	public PoincareNode(Tree<PoincareNode> tree, String nodeName, int iComparableValue) {
		super(tree);

		this.iComparableValue = iComparableValue;
		this.nodeName = nodeName;
		this.id = iComparableValue;

		this.position = new float[2];

	}

	public void setLinked(boolean linked) {
		this.linked = linked;
	}

	public boolean isLinked() {
		return linked;
	}

	public void setDistanceFromOrigin(float distanceFromOrigin) {
		this.distanceFromOrigin = distanceFromOrigin;
	}

	public float getDistanceFromOrigin() {
		return distanceFromOrigin;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public float[] getPosition() {
		return position;
	}

	public void setOpenLink(PoincareNode openLink) {
		this.openLink = openLink;
	}

	public PoincareNode getOpenLink() {
		return openLink;
	}

	public void setLevelOfDetail(int levelOfDetail) {
		this.levelOfDetail = levelOfDetail;
	}

	public int getLevelOfDetail() {
		return levelOfDetail;
	}

	public void setZoomedPosition(float[] zoomedPosition) {
		this.zoomedPosition = zoomedPosition;
	}

	public float[] getZoomedPosition() {
		return zoomedPosition;
	}

	public void setChildrenAngleOffset(float childrenAngleOffset) {
		this.childrenAngleOffset = childrenAngleOffset;
	}

	public float getChildrenAngleOffset() {
		return childrenAngleOffset;
	}

}
