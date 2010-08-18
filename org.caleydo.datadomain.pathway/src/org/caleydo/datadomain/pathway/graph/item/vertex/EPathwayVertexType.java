package org.caleydo.datadomain.pathway.graph.item.vertex;

public enum EPathwayVertexType {
	ortholog("Ortholog"), // the node is a KO (ortholog group)
	enzyme("Enzyme"), // the node is an enzyme
	gene("Gene"), // the node is a gene product (mostly a protein)
	group("Group"), // the node is a complex of gene products (mostly a protein
	// complex)
	compound("Compound"), // the node is a chemical compound (including a
	// glycan)
	map("Linked Pathway"), other("Unknown");

	private final String sName;

	EPathwayVertexType(final String sName) {

		this.sName = sName;
	}

	public String getName() {

		return sName;
	}
}
