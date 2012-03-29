package org.caleydo.view.datagraph.layout.edge.rendering;

import javax.media.opengl.GL2;
import org.caleydo.core.view.opengl.util.spline.ConnectionBandRenderer;
import org.caleydo.view.datagraph.Edge;
import org.caleydo.view.datagraph.GLDataViewIntegrator;
import org.caleydo.view.datagraph.layout.edge.routing.IEdgeRoutingStrategy;

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
