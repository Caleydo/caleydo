package org.caleydo.core.manager.datadomain;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.SimpleGraph;

/**
 * @author Alexander Lex
 */
public class DataDomainGraph {
	SimpleGraph<String, Edge> dataDomainGraph;

	public static final String CLINICAL = "org.caleydo.datadomain.clinical";
	public static final String TISSUE = "org.caleydo.datadomain.tissue";
	public static final String GENETIC = "org.caleydo.datadomain.genetic";
	public static final String PATHWAY = "org.caleydo.datadomain.pathway";
	public static final String ORGAN = "org.caleydo.datadomain.organ";

	public DataDomainGraph() {
		EdgeFactory edgeFactory = new EdgeFactory();
		dataDomainGraph = new SimpleGraph<String, Edge>(edgeFactory);

		initDataDomainGraph();
	}

	public void initDataDomainGraph() {
		dataDomainGraph.addVertex(CLINICAL);
		dataDomainGraph.addVertex(TISSUE);
		dataDomainGraph.addVertex(GENETIC);
		dataDomainGraph.addVertex(PATHWAY);
		dataDomainGraph.addVertex(ORGAN);

		dataDomainGraph.addEdge(CLINICAL, GENETIC);
		dataDomainGraph.addEdge(CLINICAL, TISSUE);
		dataDomainGraph.addEdge(CLINICAL, ORGAN);
		dataDomainGraph.addEdge(GENETIC, PATHWAY);
		dataDomainGraph.addEdge(TISSUE, GENETIC);
	}

	public Set<String> getNeighboursOf(String vertex) {
		Set<Edge> edges = dataDomainGraph.edgesOf(vertex);
		Set<String> vertices = new HashSet<String>();
		for (Edge edge : edges) {
			vertices.add(edge.getOtherSideOf(vertex));
		}

		return vertices;

	}

	public SimpleGraph<String, Edge> getGraph() {
		return dataDomainGraph;
	}

	public static void main(String args[]) {
		DataDomainGraph graph = new DataDomainGraph();

		// System.out.println(graph.dataDomainGraph.edgesOf(CLINICAL));

		System.out.println(graph.getNeighboursOf(GENETIC));

	}
}
