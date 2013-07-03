/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
