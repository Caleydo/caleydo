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
package org.caleydo.view.entourage.ranking;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.entourage.GLEntourage;

/**
 * Common pathway filters.
 *
 * @author Christian Partl
 *
 */
public final class PathwayFilters {

	private PathwayFilters() {
	}

	public static final IPathwayFilter NONE = new IPathwayFilter() {

		@Override
		public boolean showPathway(PathwayGraph pathway) {
			return true;
		}
	};

	/**
	 * Selects only pathways that contain a specified {@link PathwayVertexRep}.
	 *
	 * @author Christian Partl
	 *
	 */
	public static class CommonVertexFilter implements IPathwayFilter {

		private Set<PathwayGraph> pathways = new HashSet<>();

		public CommonVertexFilter(PathwayVertexRep vertexRep, boolean selectSourcePathway) {
			Set<PathwayVertexRep> vertices = PathwayManager.get().getEquivalentVertexReps(vertexRep);
			for (PathwayVertexRep v : vertices) {
				PathwayGraph pathway = v.getPathway();
				if (!selectSourcePathway && vertexRep.getPathway() == pathway)
					continue;
				pathways.add(pathway);
			}
			if (selectSourcePathway) {
				pathways.add(vertexRep.getPathway());
			}
		}

		@Override
		public boolean showPathway(PathwayGraph pathway) {
			return pathways.contains(pathway);
		}

	}

	/**
	 * Selects only pathways that have {@link PathwayVertexRep}s in common with a specified pathway.
	 *
	 * @author Christian Partl
	 *
	 */
	public static class CommonVerticesFilter implements IPathwayFilter {

		private PathwayGraph pathway;
		private boolean selectSourcePathway = false;

		public CommonVerticesFilter(PathwayGraph pathway, boolean selectSourcePathway) {
			this.pathway = pathway;
			this.selectSourcePathway = selectSourcePathway;
		}

		@Override
		public boolean showPathway(PathwayGraph pathway) {
			if (this.pathway == pathway && !selectSourcePathway)
				return false;
			return PathwayManager.get().getNumEquivalentVertexReps(this.pathway, pathway) > 0;
		}
	}

	public static class NoPresentPathwaysFilter implements IPathwayFilter {

		private GLEntourage view;

		public NoPresentPathwaysFilter(GLEntourage view) {
			this.view = view;
		}

		@Override
		public boolean showPathway(PathwayGraph pathway) {
			return !view.hasPathway(pathway);
		}
	}

	/**
	 * Specify a set of pathways that should be present.
	 *
	 * @author Christian
	 *
	 */
	public static class PathwaySetFilter implements IPathwayFilter {

		private final Set<PathwayGraph> pathways;

		public PathwaySetFilter(Set<PathwayGraph> pathways) {
			this.pathways = pathways;
		}

		@Override
		public boolean showPathway(PathwayGraph pathway) {
			return pathways.contains(pathway);
		}

	}
}
