package cerberus.data.pathway.element;

import java.util.Vector;

import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.pathway.element.PathwayRelationEdge.EdgeRelationType;

public class PathwayReactionEdge 
extends APathwayEdge {
	
	public enum EdgeReactionType {
		REVERSIBLE,
		IRREVERSIBLE
	};
	
	protected String sReactionName;
	
	protected Vector<Integer> vecReactionSubstrates;
	
	protected Vector<Integer> vecReactionProducts;
	
	protected EdgeReactionType edgeReactionType;
	
	public PathwayReactionEdge(
			String sReactionName, 
			String sReactionType) {
		
		edgeType = EdgeType.REACTION;
		
		vecReactionSubstrates = new Vector<Integer>();
		vecReactionProducts = new Vector<Integer>();
		
		this.sReactionName = sReactionName;
		
		if (sReactionType.equals("reversible"))
			edgeReactionType = EdgeReactionType.REVERSIBLE;
		else if (sReactionType.equals("irreversible"))
			edgeReactionType = EdgeReactionType.IRREVERSIBLE;
	}
	
	public void addSubstrate(int iCompoundId) {
		
		vecReactionSubstrates.add(iCompoundId);
	}
	
	public void addProduct(int iCompoundId) {
		
		vecReactionProducts.add(iCompoundId);
	}

	public Vector<Integer> getSubstrates() {
		
		return vecReactionSubstrates;
	}
	
	public Vector<Integer> getProducts() {
		
		return vecReactionProducts;
	}
	
	public EdgeReactionType getEdgeReactionType() {
		
		return edgeReactionType;
	}
}
