package org.caleydo.view.datagraph.bandlayout;

import java.awt.geom.Point2D;
import java.util.List;

public interface IEdgeRoutingStrategy {
	
	public void createEdge(List<Point2D> edgePoints);

}
