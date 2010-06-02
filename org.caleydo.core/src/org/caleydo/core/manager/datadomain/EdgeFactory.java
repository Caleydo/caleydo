package org.caleydo.core.manager.datadomain;

public class EdgeFactory
	implements org.jgrapht.EdgeFactory<String, Edge> {

	@Override
	public Edge createEdge(String vertex1, String vertex2) {
		Edge edge = new Edge(vertex1, vertex2);
		return edge;
	}

}
