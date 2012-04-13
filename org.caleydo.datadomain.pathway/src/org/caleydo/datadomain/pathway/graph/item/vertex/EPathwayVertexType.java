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

public enum EPathwayVertexType {
	ortholog("Ortholog"), // the node is a KO (ortholog group)
	enzyme("Enzyme"), // the node is an enzyme
	gene("Gene"), // the node is a gene product (mostly a protein)
	group("Group"), // the node is a complex of gene products (mostly a protein
	// complex)
	compound("Compound"), // the node is a chemical compound (including a
	// glycan)
	map("Linked Pathway"),
	other("Unknown");

	private final String sName;

	EPathwayVertexType(final String sName) {

		this.sName = sName;
	}

	public String getName() {

		return sName;
	}
}
