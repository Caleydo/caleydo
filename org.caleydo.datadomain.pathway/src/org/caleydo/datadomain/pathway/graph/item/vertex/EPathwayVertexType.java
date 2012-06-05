/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
