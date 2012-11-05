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
	private GraphPath<PathwayVertexRep, DefaultEdge> path;

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
