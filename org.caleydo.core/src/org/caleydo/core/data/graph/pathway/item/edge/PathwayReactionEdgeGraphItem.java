package org.caleydo.core.data.graph.pathway.item.edge;

import java.io.Serializable;

import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.item.GraphItem;

/**
 * Pathway reaction edge belonging to the overall pathway graph.
 * Used for KEGG pathways.
 * 
 * @author Marc Streit
 *
 */
public class PathwayReactionEdgeGraphItem 
extends GraphItem  
implements Serializable 
{	
	private static final long serialVersionUID = 1L;
	
	final String sReactionId;
	
	final EPathwayReactionEdgeType type;
	
	/**
	 * Constructor.
	 * 
	 * @param iId
	 * @param sReactionId
	 * @param sType
	 */
	public PathwayReactionEdgeGraphItem(
			final int iId,
			final String sReactionId,
			final String sType) {
		
		super(iId, EGraphItemKind.EDGE);

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