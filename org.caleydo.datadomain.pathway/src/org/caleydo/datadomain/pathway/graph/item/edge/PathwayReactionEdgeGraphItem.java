package org.caleydo.datadomain.pathway.graph.item.edge;

import java.io.Serializable;
import org.jgrapht.graph.DefaultEdge;

/**
 * Pathway reaction edge belonging to the overall pathway graph. Used for KEGG
 * pathways.
 * 
 * @author Marc Streit
 */
public class PathwayReactionEdgeGraphItem extends DefaultEdge implements
		Serializable {

	private static final long serialVersionUID = 1L;

	final String sReactionId;

	final EPathwayReactionEdgeType type;

	/**
	 * Constructor.
	 * 
	 * @param sReactionId
	 * @param sType
	 */
	public PathwayReactionEdgeGraphItem(final String sReactionId, final String sType) {

		this.sReactionId = sReactionId;

		type = EPathwayReactionEdgeType.valueOf(sType);
	}

	public EPathwayReactionEdgeType getType() {

		return type;
	}

	public String getReactionId() {

		return sReactionId;
	}
}