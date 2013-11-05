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
package org.caleydo.view.entourage.event;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.entourage.EEmbeddingID;
import org.caleydo.view.entourage.GLEntourage;

/**
 * Event that signals that a pathway, which is specified by an embedded {@link PathwayVertexRep}, should be added to
 * {@link GLEntourage}.
 *
 * @author Christian Partl
 *
 */
public class AddPathwayEvent extends AEvent {

	protected PathwayGraph pathway;
	protected EEmbeddingID pathwayLevel = EEmbeddingID.PATHWAY_LEVEL1;

	/**
	 *
	 */
	public AddPathwayEvent() {
		// TODO Auto-generated constructor stub
	}

	/**
	 *
	 */
	public AddPathwayEvent(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	public AddPathwayEvent(PathwayGraph pathway, EEmbeddingID pathwayLevel) {
		this.pathway = pathway;
		this.pathwayLevel = pathwayLevel;
	}

	@Override
	public boolean checkIntegrity() {
		return pathway != null;
	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * @param pathway
	 *            setter, see {@link pathway}
	 */
	public void setPathway(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	/**
	 * @param pathwayLevel
	 *            setter, see {@link pathwayLevel}
	 */
	public void setPathwayLevel(EEmbeddingID pathwayLevel) {
		this.pathwayLevel = pathwayLevel;
	}

	/**
	 * @return the pathwayLevel, see {@link #pathwayLevel}
	 */
	public EEmbeddingID getPathwayLevel() {
		return pathwayLevel;
	}

}
