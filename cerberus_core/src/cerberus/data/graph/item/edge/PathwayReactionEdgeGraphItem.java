package cerberus.data.graph.item.edge;

import org.geneview.graph.EGraphItemKind;
import org.geneview.graph.item.GraphItem;


public class PathwayReactionEdgeGraphItem 
extends GraphItem {
	
	final String sReactionId;
	
	final EPathwayReactionEdgeType type;
	
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