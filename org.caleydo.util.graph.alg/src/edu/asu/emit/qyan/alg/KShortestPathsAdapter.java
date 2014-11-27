/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package edu.asu.emit.qyan.alg;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;

/**
 * Adapter for computing k shortest paths using Yen's algorithm with DirectedGraphs from JGraphT.
 *
 * @author Christian
 * 
 */
public class KShortestPathsAdapter<VertexType, EdgeType> {

	private final DirectedGraph<VertexType, EdgeType> sourceGraph;
	private final Graph graph;

	public KShortestPathsAdapter(DirectedGraph<VertexType, EdgeType> graph) {
		this.sourceGraph = graph;
		this.graph = new Graph();
		convertGraph();
	}

	private void convertGraph() {
		graph.clear();
		for (VertexType v : sourceGraph.vertexSet()) {
			graph.addVertex(v);
		}
		for (EdgeType edge : sourceGraph.edgeSet()) {

			VertexType src = sourceGraph.getEdgeSource(edge);
			VertexType dest = sourceGraph.getEdgeTarget(edge);
			// The graph can not deal with loops, but they should be irrelevant for shortest paths
			if (src != dest)
				graph.addEdge(src, dest);
		}
	}

	@SuppressWarnings("unchecked")
	public List<GraphPath<VertexType, EdgeType>> getKShortestPaths(VertexType from, VertexType to, int k) {
		YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(graph);

		List<Path> paths = alg.get_shortest_paths(graph.getVertex(from), graph.getVertex(to), k);
		List<GraphPath<VertexType, EdgeType>> graphPaths = new ArrayList<>(paths.size());
		for (Path p : paths) {
			List<EdgeType> pathEdges = new ArrayList<>(p.get_vertices().size() - 1);
			List<BaseVertex> pathVertices = p.get_vertices();
			for (int i = 0; i < pathVertices.size() - 1; i++) {

				BaseVertex src = pathVertices.get(i);
				BaseVertex dest = pathVertices.get(i + 1);

				pathEdges.add(sourceGraph.getEdge((VertexType) src.getVertexData(), (VertexType) dest.getVertexData()));

			}
			GraphPath<VertexType, EdgeType> path = new GraphPathImpl<VertexType, EdgeType>(sourceGraph,
					(VertexType) pathVertices.get(0).getVertexData(), (VertexType) pathVertices.get(
							pathVertices.size() - 1).getVertexData(), pathEdges, 0);
			graphPaths.add(path);
		}
		return graphPaths;
	}

}
