/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph;

import java.util.List;

import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

/**
 * A pathway path is a wrapper object that contains the GraphPath as returned by
 * the path search algorithm. In addition, the class provides convenience
 * methods for accessing the path data.
 *
 * @author Marc Streit
 *
 */
public class PathwayPath {

	/**
	 * The path as determined by the user who specified a start and end node.
	 */
	private final GraphPath<PathwayVertexRep, DefaultEdge> path;

	public PathwayPath(GraphPath<PathwayVertexRep, DefaultEdge> path) {
		this.path = path;
	}

	/**
	 * @return the path, see {@link #path}
	 */
	public GraphPath<PathwayVertexRep, DefaultEdge> getPath() {
		return path;
	}

	/**
	 * @return the nodes along the path
	 */
	public List<PathwayVertexRep> getNodes() {

		return Graphs.getPathVertexList(path);
	}

	/**
	 * @return The associated pathway.
	 */
	public PathwayGraph getPathway() {
		return (PathwayGraph) path.getGraph();
	}
}
