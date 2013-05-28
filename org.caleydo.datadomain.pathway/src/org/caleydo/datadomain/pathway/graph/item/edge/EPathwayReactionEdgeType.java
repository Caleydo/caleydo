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
 * Quote from: http://www.kegg.jp/kegg/xml/docs/: 
 * 
 * The type attribute specifies
 * the distinction of reversible and irreversible reactions, which are indicated
 * by bi-directional and uni-directional arrows in the KEGG pathways. Note that
 * the terms "reversible" and "irreversible" do not necessarily reflect
 * biochemical properties of each reaction. They rather indicate the direction
 * of the reaction drawn on the pathway map that is extracted from text books
 * and literatures.
 * 
 * @author Marc Streit
 * 
 */
public enum EPathwayReactionEdgeType {
	reversible,
	irreversible
}
