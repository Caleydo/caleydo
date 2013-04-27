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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDCategory;
import org.jgrapht.graph.Multigraph;

/**
 * @author Alexander Lex
 */
public class DataDomainGraph {
	Multigraph<IDataDomain, Edge> dataDomainGraph;

	public static final String CLINICAL = "org.caleydo.datadomain.clinical";
	public static final String TISSUE = "org.caleydo.datadomain.tissue";
	public static final String GENETIC = "org.caleydo.datadomain.genetic";
	public static final String PATHWAY = "org.caleydo.datadomain.pathway";
	public static final String ORGAN = "org.caleydo.datadomain.organ";

	public DataDomainGraph() {
		EdgeFactory edgeFactory = new EdgeFactory();
		dataDomainGraph = new Multigraph<IDataDomain, Edge>(edgeFactory);

		// initDataDomainGraph();
	}

	// public void initDataDomainGraph() {
	// dataDomainGraph.addVertex(CLINICAL);
	// dataDomainGraph.addVertex(TISSUE);
	// dataDomainGraph.addVertex(GENETIC);
	// dataDomainGraph.addVertex(PATHWAY);
	// dataDomainGraph.addVertex(ORGAN);
	//
	// dataDomainGraph.addEdge(CLINICAL, GENETIC);
	// dataDomainGraph.addEdge(CLINICAL, TISSUE);
	// dataDomainGraph.addEdge(CLINICAL, ORGAN);
	// dataDomainGraph.addEdge(GENETIC, PATHWAY);
	// dataDomainGraph.addEdge(TISSUE, GENETIC);
	// }

	public synchronized void addDataDomain(IDataDomain dataDomain) {
		if (dataDomainGraph.containsVertex(dataDomain))
			return;

		dataDomainGraph.addVertex(dataDomain);

		for (IDataDomain vertex : dataDomainGraph.vertexSet()) {
			if (vertex != dataDomain) {
				for (IDCategory category : vertex.getIDCategories()) {
					for (IDCategory currentCategory : dataDomain.getIDCategories()) {
						if (category == currentCategory) {
							Edge edge = new Edge(dataDomain, vertex, category);

							if (dataDomain instanceof ATableBasedDataDomain
								&& vertex instanceof ATableBasedDataDomain) {
								ATableBasedDataDomain tableBasedDataDomain =
									(ATableBasedDataDomain) dataDomain;
								ATableBasedDataDomain previouslyRegisteredDataDomain =
									(ATableBasedDataDomain) vertex;

								if (currentCategory == tableBasedDataDomain.getDimensionIDCategory()) {
									edge.setInfoVertex1("Column");
								}
								else if (currentCategory == tableBasedDataDomain.getRecordIDCategory()) {
									edge.setInfoVertex1("Row");
								}

								if (currentCategory == previouslyRegisteredDataDomain
									.getDimensionIDCategory()) {
									edge.setInfoVertex2("Column");
								}
								else if (currentCategory == previouslyRegisteredDataDomain
									.getRecordIDCategory()) {
									edge.setInfoVertex2("Row");
								}
							}
							dataDomainGraph.addEdge(dataDomain, vertex, edge);
						}
					}
				}
			}
		}

		// FIXME: This is not generic at all, move the IDTypes of the DataDomains into IDataDomain
		// for (IDataDomain vertex : dataDomainGraph.vertexSet()) {
		// // break;
		// //
		// if (vertex != dataDomain) {
		// boolean mappingExists = false;
		//
		// if (dataDomain instanceof ATableBasedDataDomain && vertex instanceof ATableBasedDataDomain) {
		// ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;
		// ATableBasedDataDomain previouslyRegisteredDataDomain = (ATableBasedDataDomain) vertex;
		//
		// // TODO: Also mapping between content and dimension?
		// boolean hasContentMapping = false;
		// if (tableBasedDataDomain.getRecordIDCategory() == previouslyRegisteredDataDomain
		// .getRecordIDCategory()) {
		// hasContentMapping = true;
		// }
		// boolean hasDimensionMapping = false;
		// if (tableBasedDataDomain.getDimensionIDCategory() == previouslyRegisteredDataDomain
		// .getDimensionIDCategory()) {
		// hasDimensionMapping = true;
		// }
		//
		// if (hasContentMapping || hasDimensionMapping) {
		// mappingExists = true;
		// }
		// }
		//
		// if ((dataDomain.getDataDomainID().startsWith(CLINICAL) && vertex.getDataDomainID()
		// .startsWith(TISSUE))
		// || (vertex.getDataDomainID().startsWith(CLINICAL) && dataDomain.getDataDomainID()
		// .startsWith(TISSUE))) {
		// mappingExists = true;
		// }
		// if ((dataDomain.getDataDomainID().startsWith(CLINICAL) && vertex.getDataDomainID()
		// .startsWith(ORGAN))
		// || (vertex.getDataDomainID().startsWith(CLINICAL) && dataDomain.getDataDomainID()
		// .startsWith(ORGAN))) {
		// mappingExists = true;
		// }
		//
		// if ((dataDomain.getDataDomainID().startsWith(GENETIC) && vertex.getDataDomainID().startsWith(
		// PATHWAY))
		// || (vertex.getDataDomainID().startsWith(GENETIC) && dataDomain.getDataDomainID()
		// .startsWith(PATHWAY))) {
		// mappingExists = true;
		// }
		//
		// if ((dataDomain.getDataDomainID().startsWith(GENETIC) && vertex.getDataDomainID().startsWith(
		// TISSUE))
		// || (vertex.getDataDomainID().startsWith(GENETIC) && dataDomain.getDataDomainID()
		// .startsWith(TISSUE))) {
		// mappingExists = true;
		// }
		//
		// if (dataDomain.getDataDomainID().startsWith(PATHWAY)
		// && vertex.getDataDomainID().startsWith(PATHWAY))
		// mappingExists = true;
		//
		// if (mappingExists) {
		// dataDomainGraph.addEdge(dataDomain, vertex);
		// }
		//
		// }
		// }
	}

	public Set<Edge> getEdges(IDataDomain dataDomain1, IDataDomain dataDomain2) {
		return new HashSet<Edge>(dataDomainGraph.getAllEdges(dataDomain1, dataDomain2));
	}

	public synchronized void removeDataDomain(IDataDomain dataDomain) {
		if (dataDomainGraph.containsVertex(dataDomain)) {
			dataDomainGraph.removeAllEdges(new ArrayList<Edge>(dataDomainGraph.edgesOf(dataDomain)));
			// work on a local copy to avoid concurrent modifications
			dataDomainGraph.removeVertex(dataDomain);
		}
	}

	public synchronized Set<IDataDomain> getNeighboursOf(IDataDomain vertex) {
		Set<Edge> edges = dataDomainGraph.edgesOf(vertex);
		Set<IDataDomain> vertices = new HashSet<IDataDomain>();
		for (Edge edge : edges) {
			vertices.add(edge.getOtherSideOf(vertex));
		}

		return vertices;

	}

	public synchronized Multigraph<IDataDomain, Edge> getGraph() {
		return dataDomainGraph;
	}

	public Set<IDataDomain> getDataDomains() {
		return new HashSet<IDataDomain>(dataDomainGraph.vertexSet());
	}

	public static void main(String args[]) {
		// DataDomainGraph graph = new DataDomainGraph();
		// System.out.println(graph.dataDomainGraph.edgesOf(CLINICAL));
		// System.out.println(graph.getNeighboursOf(GENETIC));
	}
}
