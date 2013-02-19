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
package org.caleydo.datadomain.pathway.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;

/**
 * Event signaling that all nodes equivalent to a {@link PathwayVertexRep} (the portal) shall be indicated.
 *
 * @author Christian Partl
 *
 */
public class ShowPortalNodesEvent extends AEvent {

	/**
	 * Vertex rep that serves as portal.
	 */
	private PathwayVertexRep vertexRep;

	public ShowPortalNodesEvent(PathwayVertexRep vertexRep) {
		this.vertexRep = vertexRep;
	}

	@Override
	public boolean checkIntegrity() {
		return vertexRep != null;
	}

	/**
	 * @param vertexRep
	 *            setter, see {@link vertexRep}
	 */
	public void setVertexRep(PathwayVertexRep vertexRep) {
		this.vertexRep = vertexRep;
	}

	/**
	 * @return the vertexRep, see {@link #vertexRep}
	 */
	public PathwayVertexRep getVertexRep() {
		return vertexRep;
	}

}
