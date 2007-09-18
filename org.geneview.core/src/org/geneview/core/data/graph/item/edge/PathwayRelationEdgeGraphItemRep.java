package org.geneview.core.data.graph.item.edge;

import org.geneview.graph.EGraphItemKind;
//import org.geneview.graph.EGraphItemProperty;
import org.geneview.graph.item.GraphItem;

//import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;


public class PathwayRelationEdgeGraphItemRep 
extends GraphItem {
	
	public PathwayRelationEdgeGraphItemRep(
			final int iId) {
		
		super(iId, EGraphItemKind.EDGE);
	}
}