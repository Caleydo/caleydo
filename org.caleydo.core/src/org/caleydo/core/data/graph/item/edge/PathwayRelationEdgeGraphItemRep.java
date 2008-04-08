package org.caleydo.core.data.graph.item.edge;

import org.caleydo.util.graph.EGraphItemKind;
//import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.item.GraphItem;

//import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItem;


public class PathwayRelationEdgeGraphItemRep 
extends GraphItem {
	
	public PathwayRelationEdgeGraphItemRep(
			final int iId) {
		
		super(iId, EGraphItemKind.EDGE);
	}
}