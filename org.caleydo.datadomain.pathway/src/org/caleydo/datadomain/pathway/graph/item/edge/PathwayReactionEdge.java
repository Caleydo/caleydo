/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph.item.edge;

import java.io.Serializable;

import org.jgrapht.graph.DefaultEdge;

/**
 * Pathway reaction edge belonging to the overall pathway graph. Used for KEGG
 * pathways.
 * 
 * @author Marc Streit
 */
public class PathwayReactionEdge extends DefaultEdge implements
		Serializable {

	private static final long serialVersionUID = 1L;

//	final String reactionId;
//
//	final EPathwayReactionEdgeType type;
//
//	/**
//	 * Constructor.
//	 * 
//	 * @param reactionId
//	 * @param type
//	 */
//	public PathwayReactionEdge(final String reactionId, final String type) {

//		this.reactionId = reactionId;
//		this.type = EPathwayReactionEdgeType.valueOf(type);
//	}

//	public EPathwayReactionEdgeType getType() {
//
//		return type;
//	}
//
//	public String getReactionId() {
//
//		return reactionId;
//	}
}
