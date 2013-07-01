/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph.item.vertex;

/**
 * Possible shape states of vertex reps for Kegg. These names correspond excatly to the wording used in the xml format
 * provided by the pathway suppliers, therefore coding style is violated.
 *
 * @author Marc Streit
 * @author Alexander Lex
 *
 */
public enum EPathwayVertexShape {

	/**
	 * The shape is a rectangle, which is used in KEGG to represent a gene product and its complex (including an
	 * ortholog group).
	 */
	rectangle,
	/**
	 * The shape is a circle, which is in KEGG used to specify any other molecule such as a chemical compound and a
	 * glycan.
	 */
	circle,
	/**
	 * The shape is a round rectangle, which is used in KEGG to represent a linked pathway.
	 */
	roundrectangle,

	line
}
