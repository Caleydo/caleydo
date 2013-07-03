/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph.item.vertex;

/**
 * List of different types of possible vertices in a pathway.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * 
 */
public enum EPathwayVertexType {
	/** the node is a KO (ortholog group) */
	ortholog("Ortholog"),
	/** the node is an enzyme */
	enzyme("Enzyme"),
	/** the node is a gene product (mostly a protein) */
	gene("Gene"),
	/** the node is a complex of gene products (mostly a protein */
	group("Group"),
	/** the node is a chemical compound (including a glycan) */
	compound("Compound"),
	/** the node is a pathway */
	map("Linked Pathway"),
	/** the type of the node is unknwon */
	other("Unknown");
	
	private final String name;

	EPathwayVertexType(final String name) {

		this.name = name;
	}

	public String getName() {

		return name;
	}
}
