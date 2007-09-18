package org.geneview.core.data.graph.item.edge;

import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.item.GraphItem;


public class PathwayReactionEdgeGraphItemRep 
extends GraphItem {
	
	public PathwayReactionEdgeGraphItemRep(
			final int iId) {
		
		super(iId, EGraphItemKind.EDGE);
	}
}