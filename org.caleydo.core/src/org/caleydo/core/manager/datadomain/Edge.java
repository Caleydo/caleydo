package org.caleydo.core.manager.datadomain;

public class Edge {

	String vertex1;
	String vertex2;

	public Edge(String vertex1, String vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
	}

	public String getOtherSideOf(String vertex) {
		if (vertex.equals(vertex1))
			return vertex2;
		else if (vertex.equals(vertex2))
			return vertex1;
		else
			return null;
	}

}
