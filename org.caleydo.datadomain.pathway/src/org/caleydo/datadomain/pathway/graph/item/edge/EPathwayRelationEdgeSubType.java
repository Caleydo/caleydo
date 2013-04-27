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
