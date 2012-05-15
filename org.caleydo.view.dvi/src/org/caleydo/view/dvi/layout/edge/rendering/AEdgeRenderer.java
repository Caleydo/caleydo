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
package org.caleydo.view.dvi.layout.edge.rendering;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.dvi.Edge;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.layout.edge.routing.IEdgeRoutingStrategy;

public abstract class AEdgeRenderer {

	protected Edge edge;
	protected IEdgeRoutingStrategy edgeRoutingStrategy;
	protected GLDataViewIntegrator view;

	public AEdgeRenderer(Edge edge, GLDataViewIntegrator view) {
		this.edge = edge;
		this.view = view;
	}

	public abstract void renderEdge(GL2 gl,
			ConnectionBandRenderer connectionBandRenderer, boolean highlight);

	public Edge getEdge() {
		return edge;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
	}

	public IEdgeRoutingStrategy getEdgeRoutingStrategy() {
		return edgeRoutingStrategy;
	}

	public void setEdgeRoutingStrategy(IEdgeRoutingStrategy edgeRoutingStrategy) {
		this.edgeRoutingStrategy = edgeRoutingStrategy;
	}

}
