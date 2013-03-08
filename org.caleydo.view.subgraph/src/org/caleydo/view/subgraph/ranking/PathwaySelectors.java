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
package org.caleydo.view.subgraph.ranking;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.vis.rank.data.AFloatFunction;
import org.caleydo.vis.rank.data.IFloatFunction;
import org.caleydo.vis.rank.model.IRow;

/**
 * Common selectors.
 *
 * @author Christian Partl
 *
 */
public final class PathwaySelectors {

	public static final IPathwaySelector ALL = new IPathwaySelector() {

		@Override
		public boolean isSelected(PathwayGraph pathway) {
			return true;
		}

		@Override
		public String getRankingCriterion() {
			return "Size";
		}

		@Override
		public IFloatFunction<IRow> getRankingFunction() {
			return new AFloatFunction<IRow>() {
				@Override
				public float applyPrimitive(IRow in) {
					PathwayRow r = (PathwayRow) in;
					return r.getPathway().vertexSet().size();
				}
			};
		}
	};

	public static class EquivalentVertexSelector implements IPathwaySelector {

		private Set<PathwayGraph> pathways = new HashSet<>();
		private PathwayVertexRep vertexRep;

		public EquivalentVertexSelector(PathwayVertexRep vertexRep, boolean selectSourcePathway) {
			this.vertexRep = vertexRep;
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
		public boolean isSelected(PathwayGraph pathway) {
			return pathways.contains(pathway);
		}

		@Override
		public String getRankingCriterion() {
			return "Common Nodes";
		}

		@Override
		public IFloatFunction<IRow> getRankingFunction() {
			return new AFloatFunction<IRow>() {
				@Override
				public float applyPrimitive(IRow in) {

					PathwayRow r = (PathwayRow) in;
					if (!pathways.contains(r.getPathway()))
						return 0;
					return PathwayManager.get().getNumEquivalentVertexReps(vertexRep.getPathway(), r.getPathway());
				}
			};
		}

	}

}
