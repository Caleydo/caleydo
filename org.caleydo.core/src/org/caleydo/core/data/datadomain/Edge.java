package org.caleydo.core.data.datadomain;

import org.caleydo.core.data.id.IDCategory;

public class Edge {

	// String vertex1;
	// String vertex2;
	IDataDomain vertex1;
	IDataDomain vertex2;
	String infoVertex1;
	String infoVertex2;
	IDCategory idCategory;

	public Edge(IDataDomain vertex1, IDataDomain vertex2) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
	}

	public Edge(IDataDomain vertex1, IDataDomain vertex2, IDCategory idCategory) {
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.idCategory = idCategory;
	}

	public IDataDomain getOtherSideOf(IDataDomain vertex) {
		if (vertex.equals(vertex1))
			return vertex2;
		else if (vertex.equals(vertex2))
			return vertex1;
		else
			return null;
	}

	public String getInfoVertex1() {
		return infoVertex1;
	}

	public void setInfoVertex1(String infoVertex1) {
		this.infoVertex1 = infoVertex1;
	}

	public IDCategory getIdCategory() {
		return idCategory;
	}

	public void setIdCategory(IDCategory idCategory) {
		this.idCategory = idCategory;
	}

	public String getInfoVertex2() {
		return infoVertex2;
	}

	public void setInfoVertex2(String infoVertex2) {
		this.infoVertex2 = infoVertex2;
	}

}
