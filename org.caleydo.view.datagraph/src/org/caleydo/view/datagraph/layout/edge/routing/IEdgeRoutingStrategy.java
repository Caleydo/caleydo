package org.caleydo.view.datagraph.layout.edge.routing;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.view.datagraph.node.IDVINode;

public interface IEdgeRoutingStrategy {

	public void setNodes(IDVINode node1, IDVINode node2);

	public void createEdge(List<Point2D> edgePoints);

}
