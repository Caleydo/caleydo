/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.data.datadomain.graph;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDCategory;

public final class Edge {
	// String vertex1;
	// String vertex2;
	private final IDataDomain vertex1;
	private final IDataDomain vertex2;
	private final IDCategory idCategory;
	private String infoVertex1;
	private String infoVertex2;

	public Edge(IDataDomain vertex1, IDataDomain vertex2) {
		this(vertex1, vertex1, null);
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

	public String getInfoVertex2() {
		return infoVertex2;
	}

	public void setInfoVertex2(String infoVertex2) {
		this.infoVertex2 = infoVertex2;
	}

}
