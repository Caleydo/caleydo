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

import org.caleydo.core.data.graph.tree.Tree;

public class DataWindowsNode extends PoincareNode {

	public DataWindowsNode(Tree<PoincareNode> tree, String nodeName, int iComparableValue) {
		super(tree, nodeName, iComparableValue);
		// TODO Auto-generated constructor stub
	}

	private boolean highLighted;
	private int levelOfDetail;

	public void setHighLighted(boolean highLighted) {
		this.highLighted = highLighted;
	}

	public boolean isHighLighted() {
		return highLighted;
	}

	@Override
	public void setLevelOfDetail(int levelOfDetail) {
		this.levelOfDetail = levelOfDetail;
	}

	@Override
	public int getLevelOfDetail() {
		return levelOfDetail;
	}

}
