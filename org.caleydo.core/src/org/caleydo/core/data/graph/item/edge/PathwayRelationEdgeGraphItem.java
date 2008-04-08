package org.caleydo.core.data.graph.item.edge;

import org.caleydo.util.graph.EGraphItemKind;
import org.caleydo.util.graph.item.GraphItem;


public class PathwayRelationEdgeGraphItem 
extends GraphItem {
	
	final EPathwayRelationEdgeType type;
	
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