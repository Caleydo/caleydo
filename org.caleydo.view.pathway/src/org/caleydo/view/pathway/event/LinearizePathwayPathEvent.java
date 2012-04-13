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
package org.caleydo.view.pathway.event;

import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Event that specifies a pathway path as a list of {@link PathwayVertexRep}
 * objects in order to be linearized.
 * 
 * @author Christian
 * 
 */
public class LinearizePathwayPathEvent extends AEvent {

	/**
	 * List of {@link PathwayVertexRep} objects that specifies a path in a
	 * pathway. The first object represents the start and the last object the
	 * end of the path. If there are multiple objects that represent a complex
	 * node, these objects must be placed in a sequence.
	 */
	private List<PathwayVertexRep> path;
	
	/**
	 * The pathway whose path shall be linearized.
	 */
	private PathwayGraph pathway;

	@Override
	public boolean checkIntegrity() {
		return (pathway != null) && (path != null);
	}

	/**
	 * @param path
	 *            setter, see {@link #path}
	 */
	public void setPath(List<PathwayVertexRep> path) {
		this.path = path;
	}

	/**
	 * @return the path, see {@link #path}
	 */
	public List<PathwayVertexRep> getPath() {
		return path;
	}
	
	/**
	 * @param pathway setter, see {@link #pathway}
	 */
	public void setPathway(PathwayGraph pathway) {
		this.pathway = pathway;
	}
	
	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

}
