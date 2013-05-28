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
package org.caleydo.datadomain.pathway.graph.item.edge;

import java.io.Serializable;
import java.util.ArrayList;

import org.jgrapht.graph.DefaultEdge;

/**
 * Pathway relation edge representation belonging to the overall pathway graph.
 * Used for KEGG pathways.
 * 
 * @author Marc Streit
 */
public class PathwayRelationEdgeRep extends DefaultEdge implements Serializable {

	private static final long serialVersionUID = 1L;

	private EPathwayRelationEdgeType relationType;

	private ArrayList<EPathwayRelationEdgeSubType> relationSubTypes = new ArrayList<EPathwayRelationEdgeSubType>();

	public PathwayRelationEdgeRep(final EPathwayRelationEdgeType relationType) {

		this.relationType = relationType;
	}

	public EPathwayRelationEdgeType getType() {
		return relationType;
	}

	public void addRelationSubType(String relationSubType) {

		relationSubType = relationSubType.replace(" ", "_");
		relationSubType = relationSubType.replace("/", "_");

		// Handle typo in KEGG XML file
		if (relationSubType.equals("ubiquination"))
			relationSubType = EPathwayRelationEdgeSubType.ubiquitination.name();

		relationSubTypes.add(EPathwayRelationEdgeSubType.valueOf(relationSubType));

	}

	/**
	 * @return the subTypes, see {@link #relationSubTypes}
	 */
	public ArrayList<EPathwayRelationEdgeSubType> getRelationSubTypes() {
		return relationSubTypes;
	}
}