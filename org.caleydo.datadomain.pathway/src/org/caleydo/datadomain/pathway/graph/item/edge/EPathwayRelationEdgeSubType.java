package org.caleydo.datadomain.pathway.graph.item.edge;

/**
 * Subtype of pathway KEGG relations according to
 * http://www.kegg.jp/kegg/xml/docs/
 * 
 * @author Marc Streit
 * 
 */
public enum EPathwayRelationEdgeSubType {
	compound(""),
	hidden_compound(""),
	activation("-->"),
	inhibition("--|"),
	expression("-->"),
	repression("--|"),
	indirect_effect("..>"),
	state_change("..."),
	binding_association("---"),
	dissociation("-+-"),
	missing_interaction("-/-"),
	phosphorylation("+p"),
	dephosphorylation("-p"),
	glycosylation("+g"),
	ubiquitination("+u"),
	methylation("+m");

	private String symbol;

	private EPathwayRelationEdgeSubType(final String symbol) {
		this.symbol = symbol;
	}

	/**
	 * @return the symbol, see {@link #symbol}
	 */
	public String getSymbol() {
		return symbol;
	}
}
