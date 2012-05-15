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
package org.caleydo.view.dvi;

import org.caleydo.view.dvi.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.dvi.node.IDVINode;

public class Edge {
	private IDVINode node1;
	private IDVINode node2;
	private AEdgeRenderer edgeRenderer;

	public Edge(IDVINode node1, IDVINode node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	public IDVINode getNode1() {
		return node1;
	}

	public void setNode1(IDVINode node1) {
		this.node1 = node1;
	}

	public IDVINode getNode2() {
		return node2;
	}

	public void setNode2(IDVINode node2) {
		this.node2 = node2;
	}

	public AEdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}

	public void setEdgeRenderer(AEdgeRenderer edgeRenderer) {
		this.edgeRenderer = edgeRenderer;
	}

}
