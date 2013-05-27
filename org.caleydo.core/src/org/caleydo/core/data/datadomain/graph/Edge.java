/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
