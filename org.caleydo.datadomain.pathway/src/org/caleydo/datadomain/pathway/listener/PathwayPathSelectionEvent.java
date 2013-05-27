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
/**
 *
 */
package org.caleydo.datadomain.pathway.listener;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Event that specifies a list of pathway path segments.
 *
 * @author Christian Partl
 *
 */
public class PathwayPathSelectionEvent extends AEvent {

	/**
	 * Path object that specifies a path.
	 */
	private List<PathwayPath> pathSegments;

	@Override
	public boolean checkIntegrity() {
		return pathSegments != null;
	}

	/**
	 * @return the pathSegments, see {@link #pathSegments}
	 */
	public List<PathwayPath> getPathSegments() {
		return pathSegments;
	}

	public List<List<PathwayVertexRep>> getPathSegmentsAsVertexList() {
		List<List<PathwayVertexRep>> segments = new ArrayList<>(pathSegments.size());
		for (PathwayPath path : pathSegments) {
			segments.add(path.getNodes());
		}
		return segments;
	}

	/**
	 * @param pathSegments
	 *            setter, see {@link pathSegments}
	 */
	public void setPathSegments(List<PathwayPath> pathSegments) {
		this.pathSegments = pathSegments;
	}
}
