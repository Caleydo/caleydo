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
import org.caleydo.core.view.opengl.canvas.AGLView;

public class ViewHyperbolicNode extends PoincareNode {

	private AGLView glView;

	public ViewHyperbolicNode(Tree<PoincareNode> tree, String nodeName,
			int iComparableValue, AGLView glView) {
		super(tree, nodeName, iComparableValue);

		this.glView = glView;
	}

	public AGLView getGlView() {
		return glView;
	}
}
