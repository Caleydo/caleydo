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
package org.caleydo.datadomain.pathway.graph.item.vertex;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.base.IUniqueObject;

/**
 * Pathway vertex that belongs to the overall pathway graph.
 * 
 * @author Marc Streit
 */
public class PathwayVertex implements IUniqueObject {

	private int id;

	private final String name;

	private EPathwayVertexType type;

	private final String externalLink;

	private List<PathwayVertexRep> pathwayVertexReps = new ArrayList<PathwayVertexRep>();

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param type
	 * @param externalLink
	 * @param reactionId
	 */
	public PathwayVertex(final String name, final String type, final String externalLink) {

		id = GeneralManager.get().getIDCreator()
				.createID(ManagedObjectType.PATHWAY_VERTEX);

		// Check if type exists - otherwise assign "other"
		try {
			this.type = EPathwayVertexType.valueOf(type);
		} catch (IllegalArgumentException e) {
			this.type = EPathwayVertexType.other;
		}

		this.name = name;
		this.externalLink = externalLink;
	}

	@Override
	public int getID() {
		return id;
	}

	public String getName() {

		return name;
	}

	public EPathwayVertexType getType() {

		return type;
	}

	public String getExternalLink() {

		return externalLink;
	}

	@Override
	public String toString() {
		return name;
	}

	public void addPathwayVertexRep(PathwayVertexRep vertexRep) {
		pathwayVertexReps.add(vertexRep);
	}

	/**
	 * @return the pathwayVertexReps, see {@link #pathwayVertexReps}
	 */
	public List<PathwayVertexRep> getPathwayVertexReps() {
		return pathwayVertexReps;
	}
}
