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
package org.caleydo.view.subgraph.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.subgraph.ranking.RankingElement;

/**
 * Event that signals that the {@link RankingElement} should rank the pathways by the nodes in common with the pathway
 * of this event.
 *
 * @author Christian Partl
 *
 */
public class ShowCommonNodesPathwaysEvent extends AEvent {

	protected PathwayGraph pathway;

	/**
	 *
	 */
	public ShowCommonNodesPathwaysEvent() {
		// TODO Auto-generated constructor stub
	}

	public ShowCommonNodesPathwaysEvent(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	@Override
	public boolean checkIntegrity() {
		return pathway != null;
	}

	/**
	 * @param pathway
	 *            setter, see {@link pathway}
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
