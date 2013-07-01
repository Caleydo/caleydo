/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.layout.edge.routing;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.view.dvi.node.IDVINode;

public interface IEdgeRoutingStrategy {

	public void createEdge(IDVINode node1, IDVINode node2, List<Point2D> edgePoints);

}
