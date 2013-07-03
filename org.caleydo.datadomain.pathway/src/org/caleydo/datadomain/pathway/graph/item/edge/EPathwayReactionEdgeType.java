/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
