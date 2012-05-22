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
	};

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
