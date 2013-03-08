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

import org.caleydo.datadomain.pathway.graph.PathwayGraph;
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
public final class PathwayRankings {

	private PathwayRankings() {
	}

	public static final IPathwayRanking SIZE = new IPathwayRanking() {

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

	public static class CommonVerticesRanking implements IPathwayRanking {

		private PathwayGraph pathway;

		public CommonVerticesRanking(PathwayGraph pathway) {
			this.pathway = pathway;
		}

		@Override
		public String getRankingCriterion() {
			return "Common nodes / pathway size";
		}

		@Override
		public IFloatFunction<IRow> getRankingFunction() {
			return new AFloatFunction<IRow>() {
				@Override
				public float applyPrimitive(IRow in) {

					PathwayRow r = (PathwayRow) in;
					return (float) PathwayManager.get().getNumEquivalentVertexReps(pathway, r.getPathway())
							/ (float) PathwayManager.get().filterEquivalentVertexReps(r.getPathway()).size();
				}
			};
		}

	}

}
