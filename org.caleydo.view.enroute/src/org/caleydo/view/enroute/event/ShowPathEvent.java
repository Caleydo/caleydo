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
package org.caleydo.view.enroute.event;

import java.util.List;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;

/**
 * Event signalling that a path shall be shown. This event is to be distinguished from {@link PathwayPathSelectionEvent}
 * , which refers to updating the display of the selected path within the system. The path from this event shall be
 * added to the existing pathway visualization.
 *
 * @author Christian Partl
 *
 */
public class ShowPathEvent extends AEvent {

	/**
	 * Path segments that shall be shown.
	 */
	protected List<List<PathwayVertexRep>> pathSegments;

	public ShowPathEvent(List<List<PathwayVertexRep>> pathSegments) {
		this.pathSegments = pathSegments;

	}

	@Override
	public boolean checkIntegrity() {
		return pathSegments != null;
	}

	/**
	 * @return the pathSegments, see {@link #pathSegments}
	 */
	public List<List<PathwayVertexRep>> getPathSegments() {
		return pathSegments;
	}

	/**
	 * @param pathSegments
	 *            setter, see {@link pathSegments}
	 */
	public void setPathSegments(List<List<PathwayVertexRep>> pathSegments) {
		this.pathSegments = pathSegments;
	}

}
