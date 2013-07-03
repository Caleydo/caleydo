/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
