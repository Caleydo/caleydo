package org.caleydo.datadomain.pathway.graph.item.edge;

import java.io.Serializable;
import org.jgrapht.graph.DefaultEdge;

/**
 * Pathway relation edge belonging to the overall pathway graph. Used for KEGG
 * pathways.
 * 
 * @author Marc Streit
 */
public class PathwayRelationEdge extends DefaultEdge implements
		Serializable {

	private static final long serialVersionUID = 1L;

	final EPathwayRelationEdgeType type;

	/**
	 * Constructor.
	 * 
	 * @param sType
	 */
	public PathwayRelationEdge(final String sType) {
		
		type = EPathwayRelationEdgeType.valueOf(sType);
	}

	public EPathwayRelationEdgeType getType() {
		return type;
	}
}