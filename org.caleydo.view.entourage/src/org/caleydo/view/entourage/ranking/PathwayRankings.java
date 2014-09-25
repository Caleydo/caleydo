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

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.vis.lineup.data.ADoubleFunction;
import org.caleydo.vis.lineup.data.IDoubleFunction;
import org.caleydo.vis.lineup.model.IRow;

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
		public IDoubleFunction<IRow> getRankingFunction() {
			return new ADoubleFunction<IRow>() {
				@Override
				public double applyPrimitive(IRow in) {
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
		public IDoubleFunction<IRow> getRankingFunction() {
			return new ADoubleFunction<IRow>() {
				@Override
				public double applyPrimitive(IRow in) {

					PathwayRow r = (PathwayRow) in;
					return (double) PathwayManager.get().getNumEquivalentVertexReps(pathway, r.getPathway())
							/ (double) PathwayManager.get().filterEquivalentVertexReps(r.getPathway()).size();
				}
			};
		}

	}

	public static class NumberOfGenesFromSetRanking implements IPathwayRanking {

		private final Set<Object> geneIDs;
		private final IDType geneIDType;

		public NumberOfGenesFromSetRanking(Set<Object> geneIDs, IDType geneIDType) {
			this.geneIDs = geneIDs;
			this.geneIDType = geneIDType;
		}

		@Override
		public String getRankingCriterion() {
			return "Number of Genes from Set";
		}

		@Override
		public IDoubleFunction<IRow> getRankingFunction() {
			return new ADoubleFunction<IRow>() {
				@Override
				public double applyPrimitive(IRow in) {
					PathwayRow r = (PathwayRow) in;
					return PathwayManager.getContainedGenes(r.getPathway(), geneIDs, geneIDType).size();
				}
			};
		}

	}

}
