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
package org.caleydo.view.enroute.path;

import java.util.List;

import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;

/**
 * Strategy for {@link APathwayPathRenderer}s that shall always be in sync with the currently selected path.
 *
 * @author Christian Partl
 *
 */
public class SelectedPathUpdateStrategy extends APathUpdateStrategy {

	/**
	 * @param renderer
	 */
	public SelectedPathUpdateStrategy(APathwayPathRenderer renderer, String pathwayPathEventSpace) {
		super(renderer, pathwayPathEventSpace);
	}

	@Override
	public void onEnablePathSelection(EnablePathSelectionEvent event) {
		// path selection is done by manipulating the displayed path itself
	}

	@Override
	public void onSelectedPathChanged(PathwayPathSelectionEvent event) {
		renderer.setPath(event.getPathSegmentsAsVertexList());
	}

	@Override
	public void triggerPathUpdate() {
		triggerPathUpdate(renderer.pathSegments);
	}

	@Override
	public void nodesCreated() {
		// nothing to do
	}

	@Override
	public boolean isPathChangePermitted(List<List<PathwayVertexRep>> newPath) {
		return true;
	}

}
