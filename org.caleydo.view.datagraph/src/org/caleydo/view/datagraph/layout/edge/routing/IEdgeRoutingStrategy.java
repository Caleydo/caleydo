package org.caleydo.view.datagraph.layout.edge.routing;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.view.datagraph.node.IDataGraphNode;

public interface IEdgeRoutingStrategy {
	
	public void setNodes(IDataGraphNode node1, IDataGraphNode node2);
	
	public void createEdge(List<Point2D> edgePoints);

}
