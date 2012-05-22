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
package org.caleydo.view.dvi.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.Graph;
import org.caleydo.view.dvi.layout.edge.rendering.AEdgeRenderer;

public abstract class AGraphLayout {

	protected Graph graph = null;
	protected Map<Object, Point2D> nodePositions = null;
	protected GLDataViewIntegrator view;

	public AGraphLayout(GLDataViewIntegrator view, Graph graph) {
		this.view = view;
		this.graph = graph;
	}

	public abstract void setNodePosition(Object node, Point2D position);

	// --- getter ---
	// node position
	public abstract Point2D getNodePosition(Object node);

	public abstract void layout(Rectangle2D area);

	public abstract void updateNodePositions();

	public abstract void clearNodePositions();

	public abstract AEdgeRenderer getLayoutSpecificEdgeRenderer(Edge edge);

	public abstract AEdgeRenderer getCustomLayoutEdgeRenderer(Edge edge);

	public Graph getGraph() {
		return graph;
	}

}
