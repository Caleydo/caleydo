package org.caleydo.core.data.graph.item.edge;

import java.io.Serializable;

import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.item.GraphItem;

/**
 * Pathway relation edge belonging to the overall pathway graph.
 * Used for KEGG pathways.
 * 
 * @author Marc Streit
 *
 */
public class PathwayRelationEdgeGraphItem 
extends GraphItem  
implements Serializable 
{	
	private static final long serialVersionUID = 1L;
	
	final EPathwayRelationEdgeType type;
	
	/**
	 * Constructor.
	 * 
	 * @param iId
	 * @param sType
	 */
	public PathwayRelationEdgeGraphItem(
			final int iId,
			final String sType) {
		
		super(iId, EGraphItemKind.EDGE);

		type = EPathwayRelationEdgeType.valueOf(sType);
	}

	public EPathwayRelationEdgeType getType() {
		
		return type;
	}
}