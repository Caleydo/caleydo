/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi;

import org.caleydo.view.dvi.layout.edge.rendering.AEdgeRenderer;
import org.caleydo.view.dvi.node.IDVINode;

public class Edge {
	private final int id;
	private IDVINode node1;
	private IDVINode node2;
	private AEdgeRenderer edgeRenderer;

	public Edge(IDVINode node1, IDVINode node2, int id) {
		this.node1 = node1;
		this.node2 = node2;
		this.id = id;
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

	/**
	 * @return the id, see {@link #id}
	 */
	public int getId() {
		return id;
	}

}
