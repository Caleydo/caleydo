package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.caleydo.view.datagraph.bandlayout.AEdgeRenderer;
import org.caleydo.view.datagraph.node.IDataGraphNode;

public abstract class AGraphLayout {

	protected Graph graph = null;
	protected Map<Object, Point2D> nodePositions = null;
	protected GLDataGraph view;

	public AGraphLayout(GLDataGraph view, Graph graph) {
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

}
