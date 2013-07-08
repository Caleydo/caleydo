/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.treemap.layout;

import java.util.List;

import org.caleydo.core.data.graph.tree.AHierarchyElement;
import org.caleydo.core.data.graph.tree.Tree;

/**
 * Model for a treemap, provides some basic functionalitiy and abstract methods
 * mapping attributes.
 *
 * @author Michael Lafer
 */

public abstract class ATreeMapNode extends AHierarchyElement<ATreeMapNode> {

	private float minX, minY, maxX, maxY;

	public int selectionLevel = 0;

	public ATreeMapNode() {
		node = this;
	}

	public ATreeMapNode(Tree<ATreeMapNode> tree) {
		node = this;
		this.tree = tree;
	}

	/**
	 * Returns accumulated size for non-leave nodes.
	 */
	@Override
	public float getSize() {
		List<ATreeMapNode> list = getChildren();
		if (list == null || list.size() == 0)
			return getSizeAttribute();
		float size = 0;
		for (ATreeMapNode node : list) {
			size += node.getSize();
		}
		return size;
	}

	@Override
	public abstract Integer getID();

	public abstract float[] getColorAttribute();

	public abstract float getSizeAttribute();

	@Override
	public abstract String getLabel();

	public float getMinX() {
		return minX;
	}

	public void setMinX(float minX) {
		this.minX = minX;
	}

	public float getMinY() {
		return minY;
	}

	public void setMinY(float minY) {
		this.minY = minY;
	}

	public float getMaxX() {
		return maxX;
	}

	public void setMaxX(float maxX) {
		this.maxX = maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public void setMaxY(float maxY) {
		this.maxY = maxY;
	}
}
