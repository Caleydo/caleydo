package org.caleydo.core.manager.datadomain;

public class EdgeFactory
	implements org.jgrapht.EdgeFactory<IDataDomain, Edge> {

	@Override
	public Edge createEdge(IDataDomain vertex1, IDataDomain vertex2) {
		Edge edge = new Edge(vertex1, vertex2);
		return edge;
	}

}
