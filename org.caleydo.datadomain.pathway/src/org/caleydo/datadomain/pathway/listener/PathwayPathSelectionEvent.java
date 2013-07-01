/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
