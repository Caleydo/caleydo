package cerberus.data.pathway.element;

import java.util.Vector;

import cerberus.data.pathway.element.APathwayEdge;

public class PathwayReactionEdge 
extends APathwayEdge {
	
	protected String sReactionName;
	
	protected String sReactionType;
	
	protected Vector<Integer> vecReactionSubstrates;
	
	protected Vector<Integer> vecReactionProducts;
	
	public PathwayReactionEdge(
			String sReactionName, 
			String sReactionType) {
		
		vecReactionSubstrates = new Vector<Integer>();
		vecReactionProducts = new Vector<Integer>();
		
		this.sReactionName = sReactionName;
		this.sReactionType = sReactionType;
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
}
