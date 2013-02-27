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

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.PathwayPath;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.listener.EnablePathSelectionEvent;
import org.caleydo.datadomain.pathway.listener.PathwayPathSelectionEvent;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphPathImpl;

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
		List<PathwayPath> segments = event.getPathSegments();
		List<List<PathwayVertexRep>> pathSegments = new ArrayList<>(segments.size());
		for (PathwayPath path : segments) {
			pathSegments.add(path.getNodes());
		}

		renderer.setPath(pathSegments);
	}

	@Override
	public void triggerPathUpdate() {

		List<PathwayPath> segments = new ArrayList<>(renderer.pathSegments.size());
		for (List<PathwayVertexRep> segment : renderer.pathSegments) {
			PathwayVertexRep startVertexRep = segment.get(0);
			PathwayVertexRep endVertexRep = segment.get(segment.size() - 1);
			List<DefaultEdge> edges = new ArrayList<DefaultEdge>();
			PathwayGraph pathway = startVertexRep.getPathway();

			for (int i = 0; i < segment.size() - 1; i++) {
				PathwayVertexRep currentVertexRep = segment.get(i);
				PathwayVertexRep nextVertexRep = segment.get(i + 1);

				DefaultEdge edge = pathway.getEdge(currentVertexRep, nextVertexRep);
				if (edge == null)
					edge = pathway.getEdge(nextVertexRep, currentVertexRep);
				edges.add(edge);
			}
			GraphPath<PathwayVertexRep, DefaultEdge> graphPath = new GraphPathImpl<PathwayVertexRep, DefaultEdge>(
					pathway, startVertexRep, endVertexRep, edges, edges.size());

			segments.add(new PathwayPath(graphPath));
		}

		PathwayPathSelectionEvent event = new PathwayPathSelectionEvent();
		event.setEventSpace(pathwayPathEventSpace);
		event.setPathSegments(segments);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	@Override
	public void nodesCreated() {
		// nothing to do
	}

}
