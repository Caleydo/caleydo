/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain.graph;

import org.caleydo.core.data.datadomain.IDataDomain;

public class EdgeFactory
	implements org.jgrapht.EdgeFactory<IDataDomain, Edge> {

	@Override
	public Edge createEdge(IDataDomain vertex1, IDataDomain vertex2) {
		Edge edge = new Edge(vertex1, vertex2);
		return edge;
	}

}
