package org.caleydo.core.manager.datadomain;

public class Edge {

//	String vertex1;
//	String vertex2;
	IDataDomain vertex1;
	IDataDomain vertex2;

	public Edge(IDataDomain vertex1, IDataDomain vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
	}

	public IDataDomain getOtherSideOf(IDataDomain vertex) {
		if (vertex.equals(vertex1))
			return vertex2;
		else if (vertex.equals(vertex2))
			return vertex1;
		else
			return null;
	}

}
