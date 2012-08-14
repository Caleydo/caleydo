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
 * Possible shape states of vertex reps for BioCarta and Kegg. These names
 * correspond excatly to the wording used in the xml format provided by the
 * pathway suppliers, therefore coding style is violated.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 * 
 */
public enum EPathwayVertexShape {
	// KEGG types

	/**
	 * The shape is a rectangle, which is used in KEGG to represent a gene
	 * product and its complex (including an ortholog group).
	 */
	rectangle,
	/**
	 * The shape is a circle, which is in KEGG used to specify any other
	 * molecule such as a chemical compound and a glycan.
	 */
	circle,
	/**
	 * The shape is a round rectangle, which is used in KEGG to represent a
	 * linked pathway.
	 */
	roundrectangle,

	// BIOCARTA types
	rect,
	poly,
	line
}
