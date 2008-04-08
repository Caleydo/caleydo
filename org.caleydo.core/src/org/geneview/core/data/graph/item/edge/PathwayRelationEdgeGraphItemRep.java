package org.geneview.core.data.graph.item.edge;

import org.geneview.util.graph.EGraphItemKind;
//import org.geneview.util.graph.EGraphItemProperty;
import org.geneview.util.graph.item.GraphItem;

//import org.geneview.core.data.graph.item.vertex.PathwayVertexGraphItem;


public class PathwayRelationEdgeGraphItemRep 
extends GraphItem {
	
	public PathwayRelationEdgeGraphItemRep(
			final int iId) {
		
		super(iId, EGraphItemKind.EDGE);
	}
}